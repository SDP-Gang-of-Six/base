package cn.wxl475.controller;

import cn.wxl475.pojo.Result;
import cn.wxl475.pojo.base.Record.Record;
import cn.wxl475.repo.RecordEsRepo;
import cn.wxl475.service.RecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

import static cn.wxl475.redis.RedisConstants.CACHE_RECORDS_KEY;

@Slf4j
@RestController
@RequestMapping("/base/record")
public class RecordController {
    @Autowired
    private RecordService recordService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RecordEsRepo recordEsRepo;


    @PostMapping("/addRecord")
    public Result addRecord(@RequestHeader("Authorization") String token, @RequestBody Record record) {
        String recordType = record.getRecordType();
        String recordName = record.getRecordName();
        String content = record.getContent();
        if(recordType == null || recordType.isEmpty()) {
            return Result.error("档案类型不能为空");
        }
        if(recordName == null || recordName.isEmpty()) {
            return Result.error("档案名称不能为空");
        }
        if(content == null || content.isEmpty()) {
            return Result.error("档案内容不能为空");
        }
        recordService.save(record);
        recordEsRepo.save(record);
        return Result.success();
    }

    @PostMapping("/deleteRecords")
    public Result deleteRecords(@RequestHeader("Authorization") String token, @RequestBody List<Long> ids) {
        if(ids == null|| ids.isEmpty()){
            return Result.error("无档案需要删除");
        }
        try {
            recordService.removeBatchByIds(ids);
            recordEsRepo.deleteAllById(ids);
            for(Long id: ids) {
                stringRedisTemplate.delete(CACHE_RECORDS_KEY + id);
            }
        } catch (Exception e) {
            log.info(Arrays.toString(e.getStackTrace()));
            return Result.error(e.getMessage());
        }
        return Result.success();
    }

    @PostMapping("/updateRecord")
    public Result updateRecord(@RequestHeader("Authorization") String token, @RequestBody Record record) {
        if(record == null) {
            return Result.error("没有档案需要修改");
        }
        recordService.updateById(record);
        recordEsRepo.delete(record);
        Long id = record.getRecordId();
        stringRedisTemplate.delete(CACHE_RECORDS_KEY + id);
        return Result.success();
    }

    @PostMapping("/searchRecordsByKeyword")
    public Result searchRecordsByKeyword(@RequestHeader("Authorization") String token,
                                       @RequestParam(required = false) String keyword,
                                       @RequestParam Integer pageNum,
                                       @RequestParam Integer pageSize,
                                       @RequestParam(required = false) String sortField,
                                       @RequestParam(required = false) Integer sortOrder){
        if(pageNum <= 0 || pageSize <= 0){
            return Result.error("页码或页大小不合法");
        }
        return Result.success(recordService.searchRecordsWithKeyword(keyword,pageNum,pageSize,sortField,sortOrder));
    }

    @GetMapping("/getRecordById/{recordId}")
    public Result getRecordById(@RequestHeader("Authorization") String token, Long recordId) {
        return Result.success(recordService.selectById(recordId));
    }
}
