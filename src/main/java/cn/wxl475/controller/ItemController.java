package cn.wxl475.controller;

import cn.wxl475.pojo.Result;
import cn.wxl475.pojo.base.Item.Item;
import cn.wxl475.repo.ItemEsRepo;
import cn.wxl475.service.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

import static cn.wxl475.redis.RedisConstants.CACHE_ITEMS_KEY;

@Slf4j
@RestController
@RequestMapping("/base/item")
public class ItemController {
    @Autowired
    private ItemService itemService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ItemEsRepo itemEsRepo;


    @PostMapping("/addItem")
    public Result addItem(@RequestHeader("Authorization") String token, @RequestBody Item item) {
        String itemType = item.getItemType();
        String itemName = item.getItemName();
        String content = item.getContent();
        if(itemType == null || itemType.isEmpty()) {
            return Result.error("化验项目类型不能为空");
        }
        if(itemName == null || itemName.isEmpty()) {
            return Result.error("化验项目名称不能为空");
        }
        if(content == null || content.isEmpty()) {
            return Result.error("化验项目内容不能为空");
        }
        itemService.save(item);
        itemEsRepo.save(item);
        return Result.success();
    }

    @PostMapping("/deleteItems")
    public Result deleteItems(@RequestHeader("Authorization") String token, @RequestBody List<Long> ids) {
        if(ids == null|| ids.isEmpty()){
            return Result.error("无化验项目需要删除");
        }
        try {
            itemService.removeBatchByIds(ids);
            itemEsRepo.deleteAllById(ids);
            for(Long id: ids) {
                stringRedisTemplate.delete(CACHE_ITEMS_KEY + id);
            }
        } catch (Exception e) {
            log.info(Arrays.toString(e.getStackTrace()));
            return Result.error(e.getMessage());
        }
        return Result.success();
    }

    @PostMapping("/updateItem")
    public Result updateItem(@RequestHeader("Authorization") String token, @RequestBody Item item) {
        if(item == null) {
            return Result.error("没有化验项目需要修改");
        }
        itemService.updateById(item);
        Long id = item.getItemId();
        Item newItem = itemService.selectById(id);
        itemEsRepo.save(newItem);
        stringRedisTemplate.delete(CACHE_ITEMS_KEY + id);
        return Result.success();
    }

    @PostMapping("/searchItemsByKeyword")
    public Result searchItemsByKeyword(@RequestHeader("Authorization") String token,
                                         @RequestParam(required = false) String keyword,
                                         @RequestParam Integer pageNum,
                                         @RequestParam Integer pageSize,
                                         @RequestParam(required = false) String sortField,
                                         @RequestParam(required = false) Integer sortOrder){
        if(pageNum <= 0 || pageSize <= 0){
            return Result.error("页码或页大小不合法");
        }
        return Result.success(itemService.searchItemsWithKeyword(keyword,pageNum,pageSize,sortField,sortOrder));
    }

    @GetMapping("/getItemById/{itemId}")
    public Result getItemById(@RequestHeader("Authorization") String token, @PathVariable Long itemId) {
        return Result.success(itemService.selectById(itemId));
    }
}
