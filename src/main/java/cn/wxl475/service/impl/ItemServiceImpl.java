package cn.wxl475.service.impl;

import cn.wxl475.mapper.ItemMapper;
import cn.wxl475.pojo.base.Item.Item;
import cn.wxl475.service.ItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item> implements ItemService {
}
