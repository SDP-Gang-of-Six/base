package cn.wxl475.service.impl;

import cn.wxl475.mapper.MedicineMapper;
import cn.wxl475.pojo.Page;
import cn.wxl475.pojo.base.Medicine;
import cn.wxl475.redis.CacheClient;
import cn.wxl475.repo.MedicineEsRepo;
import cn.wxl475.service.MedicineService;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static cn.wxl475.redis.RedisConstants.*;

@Service
public class MedicineServiceImpl extends ServiceImpl<MedicineMapper, Medicine> implements MedicineService {

    private final MedicineMapper medicineMapper;
    private final MedicineEsRepo medicineEsRepo;
    private final CacheClient cacheClient;
    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    public MedicineServiceImpl(MedicineMapper medicineMapper, MedicineEsRepo medicineEsRepo, CacheClient cacheClient, ElasticsearchRestTemplate elasticsearchRestTemplate) {
        this.medicineMapper = medicineMapper;
        this.medicineEsRepo = medicineEsRepo;
        this.cacheClient = cacheClient;
        this.elasticsearchRestTemplate = elasticsearchRestTemplate;
    }

    @Override
    @Transactional
    public Medicine create(Medicine medicine) {
        medicineMapper.insert(medicine);
        medicineEsRepo.save(medicine);
        return medicine;
    }

    @Override
    @Transactional
    public void delete(ArrayList<Long> medicineIds) throws Exception {
        try {
            medicineEsRepo.deleteAllById(medicineIds);
            medicineMapper.deleteBatchIds(medicineIds);
            medicineIds.forEach(staffId->cacheClient.delete(CACHE_MEDICINE_DETAIL_KEY+staffId));
        }catch (Exception e){
            throw new Exception(e);
        }
    }

    @Override
    @Transactional
    public ArrayList<Medicine> update(ArrayList<Medicine> medicines) {
        for (int i=0;i<medicines.size();i++) {
            Medicine medicine = medicines.get(i);
            Long medicineId = medicine.getMedicineId();
            medicineMapper.updateById(medicine);
            medicines.set(i,cacheClient.resetKey(
                    CACHE_STAFF_DETAIL_KEY,
                    LOCK_STAFF_DETAIL_KEY,
                    medicineId,
                    Medicine.class,
                    medicineMapper::selectById,
                    CACHE_STAFF_DETAIL_TTL,
                    TimeUnit.MINUTES));
            medicineEsRepo.save(medicines.get(i));
        }
        return medicines;
    }

    @Override
    public Page<Medicine> searchByKeyword(String keyword, Integer pageNum, Integer pageSize, String sortField, Integer sortOrder) {
        Page<Medicine> medicines = new Page<>(0L,new ArrayList<>());
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder().withPageable(PageRequest.of(pageNum-1, pageSize));
        if(keyword!=null && !keyword.isEmpty()){
            queryBuilder.withQuery(QueryBuilders.multiMatchQuery(keyword,"medicineName","medicineUsage","medicinePrescriptionCategory","medicineStock","medicineDetail"));
        }
        if(sortField==null || sortField.isEmpty()){
            sortField = "medicineId";
        }
        if(sortOrder==null || !(sortOrder==1 || sortOrder==-1)){
            sortOrder=-1;
        }
        queryBuilder.withSorts(SortBuilders.fieldSort(sortField).order(sortOrder==-1? SortOrder.DESC:SortOrder.ASC));
        SearchHits<Medicine> hits = elasticsearchRestTemplate.search(queryBuilder.build(), Medicine.class);
        hits.forEach(medicine -> medicines.getData().add(medicine.getContent()));
        medicines.setTotalNumber(hits.getTotalHits());
        return medicines;
    }
}
