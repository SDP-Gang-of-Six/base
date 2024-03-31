package cn.wxl475;

import cn.wxl475.pojo.base.Staff;
import cn.wxl475.repo.StaffEsRepo;
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
public class StaffsEsTest {
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private StaffEsRepo staffEsRepo;

    @Test
    public void creatStaffsIndex(){
        Class<Staff> aClass = Staff.class;
        boolean created = elasticsearchRestTemplate.indexOps(aClass).createWithMapping();
        if(created){
            log.info("创建索引：{}",aClass.getName()+",成功");
        }else {
            log.info("创建索引：{}",aClass.getName()+",失败");
        }
    }

    @Test
    public void deleteStaffsIndex(){
        Class<Staff> aClass = Staff.class;
        boolean deleted = elasticsearchRestTemplate.indexOps(aClass).delete();
        if(deleted){
            log.info("删除索引：{}",aClass.getName()+",成功");
        }else {
            log.info("删除索引：{}",aClass.getName()+",失败");
        }
    }
    @Test
    public void rebuildStaffsIndex(){
        Class<Staff> aClass = Staff.class;
        boolean deleted = elasticsearchRestTemplate.indexOps(aClass).delete();
        boolean created = elasticsearchRestTemplate.indexOps(aClass).createWithMapping();
        if(deleted&&created){
            log.info("重建索引：{}",aClass.getName()+",成功");
        }else {
            log.info("重建索引：{}",aClass.getName()+",失败");
        }
    }

    @Test
    public void findAllStaffs(){
        staffEsRepo.findAll().forEach(staff -> { log.info(String.valueOf(staff));});
    }

    @Test
    public void deleteAllStaffs(){
        staffEsRepo.deleteAll();
    }
}
