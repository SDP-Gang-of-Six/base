package cn.wxl475.service.impl;

import cn.wxl475.mapper.DepartmentMapper;
import cn.wxl475.pojo.Page;
import cn.wxl475.pojo.base.Department;
import cn.wxl475.redis.CacheClient;
import cn.wxl475.repo.DepartmentEsRepo;
import cn.wxl475.service.DepartmentService;
import cn.wxl475.service.MedicineService;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
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
import java.util.List;
import java.util.concurrent.TimeUnit;

import static cn.wxl475.redis.RedisConstants.*;

@Slf4j
@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {

    private final DepartmentMapper departmentMapper;
    private final DepartmentEsRepo departmentEsRepo;
    private final MedicineService medicineService;
    private final CacheClient cacheClient;
    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    public DepartmentServiceImpl(DepartmentMapper departmentMapper, DepartmentEsRepo departmentEsRepo, MedicineService medicineService, CacheClient cacheClient, ElasticsearchRestTemplate elasticsearchRestTemplate) {
        this.departmentMapper = departmentMapper;
        this.departmentEsRepo = departmentEsRepo;
        this.medicineService = medicineService;
        this.cacheClient = cacheClient;
        this.elasticsearchRestTemplate = elasticsearchRestTemplate;
    }

    @Override
    @Transactional
    public Department create(@NonNull Department department) {
        String departmentFunction = department.getDepartmentFunction();
        if(departmentFunction ==null || departmentFunction.isEmpty()) {
            setDefaultDepartmentFunctionByType(department);
        }
        departmentMapper.insert(department);
        departmentEsRepo.save(department);
        return department;
    }

    @Override
    @Transactional
    public void delete(ArrayList<Long> departmentIds) throws Exception {
        try {
            List<Department> list = list();
            departmentMapper.deleteBatchIds(departmentIds);
            departmentEsRepo.deleteAllById(departmentIds);
            departmentIds.forEach(departmentId-> cacheClient.delete(CACHE_DEPARTMENT_DETAIL_KEY+departmentId));
        }catch (Exception e){
            throw new Exception(e);
        }
    }

    @Override
    @Transactional
    public ArrayList<Department> update(@NonNull ArrayList<Department> departments) {
        for (int i=0;i<departments.size();i++) {
            Department department = departments.get(i);
            Long departmentId = department.getDepartmentId();
            if(department.getDepartmentRoomNumber()!=null){
                if (departmentRoomNumberIsInUse(department.getDepartmentRoomNumber())) {
                    departments.set(i, null);
                    continue;
                }
            }
            if(department.getDepartmentType()!=null){
                if(department.getDepartmentFunction()==null){
                    setDefaultDepartmentFunctionByType(department);
                }
            }
            departmentMapper.updateById(department);
            departments.set(i,cacheClient.resetKey(
                    CACHE_DEPARTMENT_DETAIL_KEY,
                    LOCK_DEPARTMENT_DETAIL_KEY,
                    departmentId,
                    Department.class,
                    departmentMapper::selectById,
                    CACHE_DEPARTMENT_DETAIL_TTL,
                    TimeUnit.MINUTES));
            departmentEsRepo.save(departments.get(i));
        }
        return departments;
    }

    private void setDefaultDepartmentFunctionByType(@NonNull Department department) {
        String departmentType = department.getDepartmentType();
        switch (departmentType) {
            case "前台":
                department.setDepartmentFunction("接待挂号、导医咨询、病历档案发出与回收、收费等。");
                break;
            case "档案室":
                department.setDepartmentFunction("病例档案的合理保存与数据统计等。");
                break;
            case "诊室":
                department.setDepartmentFunction("诊室的布局介绍；对宠物进行临床基本检查（视、听、触、嗅等）、疾病诊断；与宠物主人交流并根据情况开具处方。");
                break;
            case "免疫室":
                department.setDepartmentFunction("为健康宠物接种疫苗的流程，对常见并发症的处理流程，对常见免疫相关问题的解答等。");
                break;
            case "化验室":
                department.setDepartmentFunction("对送检样本的预处理，对相应样本进行血常规、血液生化、电解质、血气、血凝指标、激素指标、尿常规、微生物学检查、药敏、皮肤刮片、粪便检查、" +
                        "传染病检查等检查操作流程。");
                break;
            case "影像室":
                department.setDepartmentFunction("X线检查、B超检查以及CT、MRI检查。如X线检查：X光机的结构功能介绍、全身各部位的摆位、拍摄条件的选择、拍摄流程、洗片的操作流程。B超检查：" +
                        "扫查探头的选择、全身各个部位扫查的摆位、腹部扫查流程。");
                break;
            case "专科检查室":
                department.setDepartmentFunction("对眼科、骨科、神经科、心脏科等专科疾病的检查，如眼科（检眼镜检查、眼压检查、裂隙灯检查、眼底检查、泪液分泌量检查等）、心脏科检查（心脏听诊、" +
                        "心电图检查等）、神经学检查（步态检查、各种反射检查等）等。");
                break;
            case "处置室":
                department.setDepartmentFunction("口服投药、换药、清洗耳道、挤肛门腺、修剪指甲、鼻饲管放置、灌肠、安乐死等基本处置操作流程。");
                break;
            case "药房":
                department.setDepartmentFunction("对各种药物的存放要求、处方的审核流程、药物的发放流程、常见药物的使用说明等。");
                break;
            case "注射室":
                department.setDepartmentFunction("静脉注射、皮下注射、肌肉注射、局部封闭注射的操作流程，常见问题的处理方法，输液泵、加热垫的使用方法，注射室的消毒流程。");
                break;
            case "手术准备室":
                department.setDepartmentFunction("术前对宠物进行麻前给药、注射麻醉、吸入麻醉的流程，保定、剃毛、消毒的流程，常见手术器械的介绍，手术器械包的准备、灭菌流程，手术人员的消毒、" +
                        "穿戴手术衣流程等。");
                break;
            case "手术室":
                department.setDepartmentFunction("手术室的布局介绍，手术室消毒流程，手术无菌要求，常规手术、特殊手等的操作规范。");
                break;
            case "住院部":
                department.setDepartmentFunction("对需要住院的病例进行护理分级，不同护理级别的要求，住院部的工作流程，住院部的消毒流程等。");
                break;
            case "病理剖检室":
                department.setDepartmentFunction("对病死动物剖解的流程，病理剖检室的消毒流程，病历剖检过程的人员要求，病理剖检过程中的人道关怀。");
                break;
            default:
                department.setDepartmentFunction(departmentType+"：暂无");
                break;
        }
    }

    @Override
    @DS("slave")
    public Boolean departmentRoomNumberIsInUse(Integer departmentRoomNumber) {
        QueryWrapper<Department> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("department_room_number",departmentRoomNumber);
        return departmentMapper.exists(queryWrapper);
    }

    @Override
    public Page<Department> searchByKeyword(String keyword, Integer pageNum, Integer pageSize, String sortField, Integer sortOrder) {
        Page<Department> departments = new Page<>(0L,new ArrayList<>());
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder().withPageable(PageRequest.of(pageNum-1, pageSize));
        if(keyword!=null && !keyword.isEmpty()){
            queryBuilder.withQuery(QueryBuilders.multiMatchQuery(keyword,"departmentName","departmentType","departmentPrincipal","departmentFunction","departmentRoomNumber"));
        }
        if(sortField==null || sortField.isEmpty()){
            sortField = "departmentId";
        }
        if(sortOrder==null || !(sortOrder==1 || sortOrder==-1)){
            sortOrder=-1;
        }
        queryBuilder.withSorts(SortBuilders.fieldSort(sortField).order(sortOrder==-1? SortOrder.DESC:SortOrder.ASC));
        SearchHits<Department> hits = elasticsearchRestTemplate.search(queryBuilder.build(), Department.class);
        hits.forEach(department -> departments.getData().add(department.getContent()));
        departments.setTotalNumber(hits.getTotalHits());
        return departments;
    }

    @Override
    @DS("slave")
    public Department searchById(Long departmentId) {
        Department department = cacheClient.queryWithPassThrough(
                CACHE_DEPARTMENT_DETAIL_KEY,
                LOCK_DEPARTMENT_DETAIL_KEY,
                departmentId,
                Department.class,
                departmentMapper::selectById,
                CACHE_DEPARTMENT_DETAIL_TTL,
                TimeUnit.MINUTES);
        return getDepartmentDetails(department);
    }

    @Override
    @DS("slave")
    public Department searchByRoomNumber(Integer departmentRoomNumber) {
        QueryWrapper<Department> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("department_room_number",departmentRoomNumber);
        return getDepartmentDetails(departmentMapper.selectOne(queryWrapper));
    }

    private Department getDepartmentDetails(Department department) {
        if(department!=null){
            String departmentType = department.getDepartmentType();
            switch (departmentType){
                case "档案室":
                    department.setData(null);
                    return department;
                case "化验室":
                    department.setData(null);
                    return department;
                case "免疫室":
                    department.setData(null);
                    return department;
                case "住院部":
                    department.setData(null);
                    return department;
                case "药房":
                    department.setData(medicineService.list());
                    return department;
                case "前台":
                    department.setData(null);
                    return department;
                default:
                    department.setData(null);
                    return department;
            }
        }
        return null;
    }
}
