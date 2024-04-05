package cn.wxl475.service.impl;

import cn.wxl475.mapper.StaffMapper;
import cn.wxl475.pojo.Page;
import cn.wxl475.pojo.base.Staff;
import cn.wxl475.pojo.base.department.Department;
import cn.wxl475.redis.CacheClient;
import cn.wxl475.repo.StaffEsRepo;
import cn.wxl475.service.StaffService;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
public class StaffServiceImpl extends ServiceImpl<StaffMapper, Staff> implements StaffService {

    private final StaffMapper staffMapper;
    private final StaffEsRepo staffEsRepo;
    private final CacheClient cacheClient;
    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    public StaffServiceImpl(StaffMapper staffMapper, StaffEsRepo staffEsRepo, CacheClient cacheClient, ElasticsearchRestTemplate elasticsearchRestTemplate) {
        this.staffMapper = staffMapper;
        this.staffEsRepo = staffEsRepo;
        this.cacheClient = cacheClient;
        this.elasticsearchRestTemplate = elasticsearchRestTemplate;
    }

    @Override
    @Transactional
    public Staff create(Staff staff) {
        staff.setDeleted(false);
        staffMapper.insert(staff);
        staffEsRepo.save(staff);
        return staff;
    }

    @Override
    @Transactional
    public void delete(ArrayList<Long> staffIds) throws Exception {
        try {
            staffEsRepo.deleteAllById(staffIds);
            staffMapper.deleteBatchIds(staffIds);
            staffIds.forEach(staffId->cacheClient.delete(CACHE_STAFF_DETAIL_KEY+staffId));
        }catch (Exception e){
            throw new Exception(e);
        }
    }

    @Override
    @Transactional
    public ArrayList<Staff> update(ArrayList<Staff> staffs) {
        for (int i=0;i<staffs.size();i++) {
            Staff staff = staffs.get(i);
            Long staffId = staff.getStaffId();
            if(staff.getStaffName()!=null){
                if (staffNameIsInUse(staff.getStaffName())) {
                    staffs.set(i, null);
                    continue;
                }
            }
            staffMapper.updateById(staff);
            staffs.set(i,cacheClient.resetKey(
                    CACHE_STAFF_DETAIL_KEY,
                    LOCK_STAFF_DETAIL_KEY,
                    staffId,
                    Staff.class,
                    staffMapper::selectById,
                    CACHE_STAFF_DETAIL_TTL,
                    TimeUnit.MINUTES));
            staffEsRepo.save(staffs.get(i));
        }
        return staffs;
    }

    @Override
    @DS("slave")
    public boolean staffNameIsInUse(String staffName) {
        QueryWrapper<Staff> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("staff_name", staffName);
        return staffMapper.exists(queryWrapper);
    }

    @Override
    public Page<Staff> searchByKeyword(String keyword, Integer pageNum, Integer pageSize, String sortField, Integer sortOrder) {
        Page<Staff> staffs = new Page<>(0L,new ArrayList<>());
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder().withPageable(PageRequest.of(pageNum-1, pageSize));
        if(keyword!=null && !keyword.isEmpty()){
            queryBuilder.withQuery(QueryBuilders.multiMatchQuery(keyword,"staffName","staffGender","staffAge","staffPosition","staffPhoneNumber"));
        }
        if(sortField==null || sortField.isEmpty()){
            sortField = "staffId";
        }
        if(sortOrder==null || !(sortOrder==1 || sortOrder==-1)){
            sortOrder=-1;
        }
        queryBuilder.withSorts(SortBuilders.fieldSort(sortField).order(sortOrder==-1? SortOrder.DESC:SortOrder.ASC));
        SearchHits<Staff> hits = elasticsearchRestTemplate.search(queryBuilder.build(), Staff.class);
        hits.forEach(staff -> staffs.getData().add(staff.getContent()));
        staffs.setTotalNumber(hits.getTotalHits());
        return staffs;
    }

    @Override
    public ArrayList<Staff> searchByIds(ArrayList<Long> staffIds) {
        return (ArrayList<Staff>) staffMapper.selectBatchIds(staffIds);
    }
}
