package cn.wxl475.service;

import cn.wxl475.pojo.Page;
import cn.wxl475.pojo.base.Record.Record;
import com.baomidou.mybatisplus.extension.service.IService;



public interface RecordService extends IService<Record> {
    Page<Record> searchRecordsWithKeyword(String keyword, Integer pageNum, Integer pageSize, String sortField, Integer sortOrder);

    Record selectById(Long recordId);
}
