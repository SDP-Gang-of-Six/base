package cn.wxl475.service;

import cn.wxl475.pojo.Page;
import cn.wxl475.pojo.base.Item.Item;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ItemService extends IService<Item> {
    Page<Item> searchItemsWithKeyword(String keyword, Integer pageNum, Integer pageSize, String sortField, Integer sortOrder);

    Item selectById(Long itemId);
}
