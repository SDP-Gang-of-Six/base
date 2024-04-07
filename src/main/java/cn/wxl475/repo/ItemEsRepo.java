package cn.wxl475.repo;

import cn.wxl475.pojo.base.Item.Item;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ItemEsRepo extends ElasticsearchRepository<Item, Long> {
}
