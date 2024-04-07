package cn.wxl475;

import cn.wxl475.pojo.base.Item.Item;
import cn.wxl475.pojo.base.Record.Record;
import cn.wxl475.repo.ItemEsRepo;
import cn.wxl475.repo.RecordEsRepo;
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
public class RecordEsTest {
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private RecordEsRepo recordEsRepo;

    @Test
    public void creatRecordsIndex(){
        Class<Record> aClass = Record.class;
        boolean created = elasticsearchRestTemplate.indexOps(aClass).createWithMapping();
        if(created){
            log.info("创建索引：{}",aClass.getName()+",成功");
        }else {
            log.info("创建索引：{}",aClass.getName()+",失败");
        }
    }

    @Test
    public void deleteRecordsIndex(){
        Class<Record> aClass = Record.class;
        boolean deleted = elasticsearchRestTemplate.indexOps(aClass).delete();
        if(deleted){
            log.info("删除索引：{}",aClass.getName()+",成功");
        }else {
            log.info("删除索引：{}",aClass.getName()+",失败");
        }
    }

    @Test
    public void rebuildRecordsIndex(){
        Class<Record> aClass = Record.class;
        boolean deleted = elasticsearchRestTemplate.indexOps(aClass).delete();
        boolean created = elasticsearchRestTemplate.indexOps(aClass).createWithMapping();
        if(deleted&&created){
            log.info("重建索引：{}",aClass.getName()+",成功");
        }else {
            log.info("重建索引：{}",aClass.getName()+",失败");
        }
    }

    @Test
    public void findAllCharges(){
        recordEsRepo.findAll().forEach(charge -> { log.info(String.valueOf(charge));});
    }

    @Test
    public void deleteAllCharges(){
        recordEsRepo.deleteAll();
    }
}
