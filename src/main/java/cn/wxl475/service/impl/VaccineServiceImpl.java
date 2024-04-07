package cn.wxl475.service.impl;

import cn.wxl475.mapper.VaccineMapper;
import cn.wxl475.pojo.Page;
import cn.wxl475.pojo.base.hospitalization.Hospitalization;
import cn.wxl475.pojo.base.vaccine.Vaccine;
import cn.wxl475.redis.CacheClient;

import cn.wxl475.repo.VaccineEsRepo;
import cn.wxl475.service.VaccineService;
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
import static cn.wxl475.redis.RedisConstants.CACHE_VACCINE_TTL;

@Service
public class VaccineServiceImpl extends ServiceImpl<VaccineMapper, Vaccine> implements VaccineService {

    @Autowired
    private VaccineMapper vaccineMapper;
    @Autowired
    private VaccineEsRepo vaccineEsRepo;
    @Autowired
    private CacheClient cacheClient;
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public Page<Vaccine> select(String keyword, Integer pageNum, Integer pageSize, String sortField, Integer sortOrder) {
        Page<Vaccine> page = new Page<>(0L,new ArrayList<>());
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder().withPageable(PageRequest.of(pageNum-1, pageSize));
        if(keyword!=null && !keyword.isEmpty()){
            queryBuilder.withQuery(QueryBuilders.multiMatchQuery(keyword, "vaccineName","vaccineDetail"));
        }
        if(sortField==null || sortField.isEmpty()){
            sortField = "vaccineId";
        }
        if(sortOrder==null || !(sortOrder==1 || sortOrder==-1)){
            sortOrder=-1;
        }
        queryBuilder.withSorts(SortBuilders.fieldSort(sortField).order(sortOrder==-1? SortOrder.DESC:SortOrder.ASC));
        SearchHits<Vaccine> hits = elasticsearchRestTemplate.search(queryBuilder.build(), Vaccine.class);
        hits.forEach(vaccine -> page.getData().add(vaccine.getContent()));
        page.setTotalNumber(hits.getTotalHits());
        return page;
    }

    @Override
    public void delete(ArrayList<Long> vaccineIds) {
        vaccineMapper.deleteBatchIds(vaccineIds);
        vaccineEsRepo.deleteAllById(vaccineIds);
        vaccineIds.forEach(vaccineId->cacheClient.delete("vaccine:"+vaccineId));
    }

    @Override
    public Long create(Vaccine vaccine) {
        vaccineMapper.insert(vaccine);
        vaccineEsRepo.save(vaccine);
        return vaccine.getVaccineId();
    }

    @Override
    public Long update(Vaccine vaccine) {
        vaccineMapper.updateById(vaccine);
        cacheClient.resetKey(
                CACHE_VACCINE_KEY,
                LOCK_VACCINE_KEY,
                vaccine.getVaccineId(),
                Vaccine.class,
                vaccineMapper::selectById,
                CACHE_VACCINE_TTL,
                TimeUnit.MINUTES
        );
        vaccineEsRepo.save(vaccine);
        return vaccine.getVaccineId();
    }

    @Override
    public Vaccine selectById(Long vaccineId) {
        return cacheClient.queryWithPassThrough(
                CACHE_VACCINE_KEY,
                LOCK_VACCINE_KEY,
                vaccineId,
                Vaccine.class,
                vaccineMapper::selectById,
                CACHE_VACCINE_TTL,
                TimeUnit.MINUTES
        );
    }
}
