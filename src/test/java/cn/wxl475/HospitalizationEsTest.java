package cn.wxl475;

import cn.wxl475.pojo.base.hospitalization.Hospitalization;
import cn.wxl475.repo.HospitalizationEsRepo;
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
public class HospitalizationEsTest {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private HospitalizationEsRepo hospitalizationEsRepo;

    @Test
    public void creatHospitalizationIndex(){
        Class<Hospitalization> aClass = Hospitalization.class;
        boolean created = elasticsearchRestTemplate.indexOps(aClass).createWithMapping();
        if(created){
            log.info("创建索引：{}",aClass.getName()+",成功");
        }else {
            log.info("创建索引：{}",aClass.getName()+",失败");
        }
    }

    @Test
    public void deleteHospitalizationIndex(){
        Class<Hospitalization> aClass = Hospitalization.class;
        boolean deleted = elasticsearchRestTemplate.indexOps(aClass).delete();
        if(deleted){
            log.info("删除索引：{}",aClass.getName()+",成功");
        }else {
            log.info("删除索引：{}",aClass.getName()+",失败");
        }
    }

    @Test
    public void rebuildHospitalizationIndex(){
        Class<Hospitalization> aClass = Hospitalization.class;
        boolean deleted = elasticsearchRestTemplate.indexOps(aClass).delete();
        boolean created = elasticsearchRestTemplate.indexOps(aClass).createWithMapping();
        if(deleted&&created){
            log.info("重建索引：{}",aClass.getName()+",成功");
        }else {
            log.info("重建索引：{}",aClass.getName()+",失败");
        }
    }

    @Test
    public void findAllHospitalization(){
        hospitalizationEsRepo.findAll().forEach(hospitalization -> { log.info(String.valueOf(hospitalization));});
    }

    @Test
    public void deleteAllHospitalization(){
        hospitalizationEsRepo.deleteAll();
    }
}
