package cn.wxl475.service.impl;

import cn.wxl475.mapper.ChargeMapper;
import cn.wxl475.pojo.Page;
import cn.wxl475.pojo.base.Charge.Charge;
import cn.wxl475.redis.CacheClient;
import cn.wxl475.service.ChargeService;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static cn.wxl475.redis.RedisConstants.*;

@Service
public class ChargeServiceImpl extends ServiceImpl<ChargeMapper, Charge> implements ChargeService {

    @Autowired
    private ChargeMapper chargeMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private CacheClient cacheClient;
    @Override
    @DS("slave")
    public Page<Charge> searchChargesWithKeyword(String keyword, Integer minFee, Integer maxFee, Integer pageNum, Integer pageSize, String sortField, Integer sortOrder) {
        Page<Charge> charges = new Page<>(0L, new ArrayList<>());
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder().withPageable(PageRequest.of(pageNum - 1, pageSize));
        if(keyword != null && !keyword.isEmpty()){
            queryBuilder.withQuery(QueryBuilders.multiMatchQuery(keyword,"detail", "payer", "petType"));
        }
        if(minFee != null && maxFee != null) {
            queryBuilder.withQuery(QueryBuilders.rangeQuery("sum").from(minFee).to(maxFee));
        }
        else if(minFee != null) {
            queryBuilder.withQuery(QueryBuilders.rangeQuery("sum").gte(minFee));
        }
        else if(maxFee != null) {
            queryBuilder.withQuery(QueryBuilders.rangeQuery("sum").lte(maxFee));
        }
        if(sortField == null || sortField.isEmpty()){
            sortField = "chargeId";
        }
        if(sortOrder == null || !(sortOrder == 1 || sortOrder == -1)){
            sortOrder = -1;
        }
        queryBuilder.withSorts(SortBuilders.fieldSort(sortField).order(sortOrder == -1? SortOrder.DESC: SortOrder.ASC));
        SearchHits<Charge> hits = elasticsearchRestTemplate.search(queryBuilder.build(), Charge.class);
        hits.forEach(charge -> charges.getData().add(charge.getContent()));
        charges.setTotalNumber(hits.getTotalHits());
        return charges;
    }

    @Override
    @DS("slave")
    public Charge selectById(Long chargeId) {
        return cacheClient.queryWithPassThrough(
                CACHE_CHARGES_KEY,
                LOCK_CHARGES_KEY,
                chargeId,
                Charge.class,
                id -> chargeMapper.selectById(chargeId),
                CACHE_CHARGES_TTL,
                TimeUnit.MINUTES
        );
    }


}
