package cn.wxl475.service.impl;

import cn.wxl475.mapper.HospitalizationMapper;
import cn.wxl475.pojo.Page;
import cn.wxl475.pojo.base.hospitalization.Hospitalization;
import cn.wxl475.redis.CacheClient;
import cn.wxl475.repo.HospitalizationEsRepo;
import cn.wxl475.service.HospitalizationService;
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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static cn.wxl475.redis.RedisConstants.*;

@Service
public class HospitalizationServiceImpl extends ServiceImpl<HospitalizationMapper, Hospitalization> implements HospitalizationService {

    @Autowired
    private HospitalizationMapper hospitalizationMapper;
    @Autowired
    private HospitalizationEsRepo hospitalizationEsRepo;
    @Autowired
    private CacheClient cacheClient;
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;


    @Override
    public Long create(Hospitalization hospitalization) {
        hospitalizationMapper.insert(hospitalization);
        hospitalizationEsRepo.save(hospitalization);
        return hospitalization.getHospitalizationId();
    }

    @Override
    public void delete(ArrayList<Long> hospitalizationIds) {
        hospitalizationEsRepo.deleteAllById(hospitalizationIds);
        hospitalizationMapper.deleteBatchIds(hospitalizationIds);
        hospitalizationIds.forEach(hospitalizationId->cacheClient.delete(CACHE_HOSPITALIZATION_KEY+hospitalizationId));
    }

    @Override
    public Page<Hospitalization> select(String allField, String keyword, Integer pageNum, Integer pageSize, String sortField, Integer sortOrder) {
        Page<Hospitalization> page = new Page<>(0L,new ArrayList<>());
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder().withPageable(PageRequest.of(pageNum-1, pageSize));
        if(allField!=null && !allField.isEmpty()){
            queryBuilder.withQuery(QueryBuilders.multiMatchQuery(allField, "patientSpecies", "hospitalizationReason","hospitalizationDepartment","hospitalizationDoctor","hospitalizationPosition","startTime","endTime"));
        }
        if(keyword!=null && !keyword.isEmpty()){
            queryBuilder.withQuery(QueryBuilders.termQuery("patientName", keyword));
        }
        if(sortField==null || sortField.isEmpty()){
            sortField = "hospitalizationId";
        }
        if(sortOrder==null || !(sortOrder==1 || sortOrder==-1)){
            sortOrder=-1;
        }
        queryBuilder.withSorts(SortBuilders.fieldSort(sortField).order(sortOrder==-1? SortOrder.DESC:SortOrder.ASC));
        SearchHits<Hospitalization> hits = elasticsearchRestTemplate.search(queryBuilder.build(), Hospitalization.class);
        hits.forEach(hospitalization -> page.getData().add(hospitalization.getContent()));
        page.setTotalNumber(hits.getTotalHits());
        return page;
    }

    @Override
    public Long update(Hospitalization hospitalization) {
        hospitalizationMapper.updateById(hospitalization);
        cacheClient.resetKey(
                CACHE_HOSPITALIZATION_KEY,
                LOCK_HOSPITALIZATION_KEY,
                hospitalization.getHospitalizationId(),
                Hospitalization.class,
                hospitalizationMapper::selectById,
                CACHE_HOSPITALIZATION_TTL,
                TimeUnit.MINUTES
        );
        hospitalizationEsRepo.save(hospitalization);
        return hospitalization.getHospitalizationId();
    }

    @Override
    @DS("slave")
    public Hospitalization selectById(Long hospitalizationId) {
        return cacheClient.queryWithPassThrough(
                CACHE_HOSPITALIZATION_KEY,
                LOCK_HOSPITALIZATION_KEY,
                hospitalizationId,
                Hospitalization.class,
                hospitalizationMapper::selectById,
                CACHE_HOSPITALIZATION_TTL,
                TimeUnit.MINUTES
        );
    }
}
