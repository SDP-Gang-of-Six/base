package cn.wxl475;

import cn.wxl475.pojo.base.Department;
import cn.wxl475.repo.DepartmentEsRepo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@SpringBootTest(args = "--spring.profiles.active=dev")
@RunWith(SpringRunner.class)
public class DepartmentsEsTest {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private DepartmentEsRepo departmentEsRepo;

    @Test
    public void creatDepartmentsIndex(){
        Class<Department> aClass = Department.class;
        boolean created = elasticsearchRestTemplate.indexOps(aClass).createWithMapping();
        if(created){
            log.info("创建索引：{}",aClass.getName()+",成功");
        }else {
            log.info("创建索引：{}",aClass.getName()+",失败");
        }
    }

    @Test
    public void deleteDepartmentsIndex(){
        Class<Department> aClass = Department.class;
        boolean deleted = elasticsearchRestTemplate.indexOps(aClass).delete();
        if(deleted){
            log.info("删除索引：{}",aClass.getName()+",成功");
        }else {
            log.info("删除索引：{}",aClass.getName()+",失败");
        }
    }

    @Test
    public void rebuildDepartmentsIndex(){
        Class<Department> aClass = Department.class;
        boolean deleted = elasticsearchRestTemplate.indexOps(aClass).delete();
        boolean created = elasticsearchRestTemplate.indexOps(aClass).createWithMapping();
        if(deleted&&created){
            log.info("重建索引：{}",aClass.getName()+",成功");
        }else {
            log.info("重建索引：{}",aClass.getName()+",失败");
        }
    }

    @Test
    public void findAllDepartments(){
        departmentEsRepo.findAll().forEach(department -> { log.info(String.valueOf(department));});
    }

    @Test
    public void deleteAllDepartments(){
        departmentEsRepo.deleteAll();
    }
}
