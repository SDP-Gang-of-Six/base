package cn.wxl475;

import cn.wxl475.pojo.base.Item.Item;
import cn.wxl475.repo.ItemEsRepo;
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
public class ItemEsTest {
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private ItemEsRepo itemEsRepo;

    @Test
    public void creatItemsIndex(){
        Class<Item> aClass = Item.class;
        boolean created = elasticsearchRestTemplate.indexOps(aClass).createWithMapping();
        if(created){
            log.info("创建索引：{}",aClass.getName()+",成功");
        }else {
            log.info("创建索引：{}",aClass.getName()+",失败");
        }
    }

    @Test
    public void deleteItemsIndex(){
        Class<Item> aClass = Item.class;
        boolean deleted = elasticsearchRestTemplate.indexOps(aClass).delete();
        if(deleted){
            log.info("删除索引：{}",aClass.getName()+",成功");
        }else {
            log.info("删除索引：{}",aClass.getName()+",失败");
        }
    }

    @Test
    public void rebuildItemsIndex(){
        Class<Item> aClass = Item.class;
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
        itemEsRepo.findAll().forEach(charge -> { log.info(String.valueOf(charge));});
    }

    @Test
    public void deleteAllCharges(){
        itemEsRepo.deleteAll();
    }
}
