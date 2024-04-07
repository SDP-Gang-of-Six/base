package cn.wxl475.service.impl;

import cn.wxl475.mapper.ItemMapper;
import cn.wxl475.pojo.Page;
import cn.wxl475.pojo.base.Item.Item;
import cn.wxl475.redis.CacheClient;
import cn.wxl475.service.ItemService;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static cn.wxl475.redis.RedisConstants.*;

@Service
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item> implements ItemService {

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private CacheClient cacheClient;

    @Override
    @DS("slave")
    public Page<Item> searchItemsWithKeyword(String keyword, Integer pageNum, Integer pageSize, String sortField, Integer sortOrder) {
        Page<Item> items = new Page<>(0L, new ArrayList<>());
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder().withPageable(PageRequest.of(pageNum - 1, pageSize));
        if(keyword != null && !keyword.isEmpty()){
            queryBuilder.withQuery(QueryBuilders.multiMatchQuery(keyword,"itemName", "itemType", "content"));
        }
        if(sortField == null || sortField.isEmpty()){
            sortField = "itemId";
        }
        if(sortOrder == null || !(sortOrder == 1 || sortOrder == -1)){
            sortOrder = -1;
        }
        queryBuilder.withSorts(SortBuilders.fieldSort(sortField).order(sortOrder == -1? SortOrder.DESC: SortOrder.ASC));
        SearchHits<Item> hits = elasticsearchRestTemplate.search(queryBuilder.build(), Item.class);
        hits.forEach(item -> items.getData().add(item.getContent()));
        items.setTotalNumber(hits.getTotalHits());
        return items;
    }

    @Override
    @DS("slave")
    public Item selectById(Long itemId) {
        return cacheClient.queryWithPassThrough(
                CACHE_ITEMS_KEY,
                LOCK_ITEMS_KEY,
                itemId,
                Item.class,
                id ->  itemMapper.selectById(itemId),
                CACHE_ITEMS_TTL,
                TimeUnit.MINUTES
        );
    }
}
