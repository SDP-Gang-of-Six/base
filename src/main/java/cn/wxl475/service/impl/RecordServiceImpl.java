package cn.wxl475.service.impl;

import cn.wxl475.mapper.RecordMapper;
import cn.wxl475.pojo.Page;
import cn.wxl475.pojo.base.Record.Record;
import cn.wxl475.redis.CacheClient;
import cn.wxl475.service.RecordService;
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
public class RecordServiceImpl extends ServiceImpl<RecordMapper, Record> implements RecordService {

    @Autowired
    private RecordMapper recordMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private CacheClient cacheClient;
    @Override
    @DS("slave")
    public Page<Record> searchRecordsWithKeyword(String keyword, Integer pageNum, Integer pageSize, String sortField, Integer sortOrder) {
        Page<Record> records = new Page<>(0L, new ArrayList<>());
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder().withPageable(PageRequest.of(pageNum - 1, pageSize));
        if(keyword != null && !keyword.isEmpty()){
            queryBuilder.withQuery(QueryBuilders.multiMatchQuery(keyword,"recordName", "recordType", "content"));
        }
        if(sortField == null || sortField.isEmpty()){
            sortField = "recordId";
        }
        if(sortOrder == null || !(sortOrder == 1 || sortOrder == -1)){
            sortOrder = -1;
        }
        queryBuilder.withSorts(SortBuilders.fieldSort(sortField).order(sortOrder == -1? SortOrder.DESC: SortOrder.ASC));
        SearchHits<Record> hits = elasticsearchRestTemplate.search(queryBuilder.build(), Record.class);
        hits.forEach(record -> records.getData().add(record.getContent()));
        records.setTotalNumber(hits.getTotalHits());
        return records;
    }

    @Override
    @DS("slave")
    public Record selectById(Long recordId) {
        return cacheClient.queryWithPassThrough(
                CACHE_RECORDS_KEY,
                LOCK_RECORDS_KEY,
                recordId,
                Record.class,
                id ->  recordMapper.selectById(recordId),
                CACHE_RECORDS_TTL,
                TimeUnit.MINUTES
        );
    }
}
