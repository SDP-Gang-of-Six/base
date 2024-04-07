package cn.wxl475.service.impl;

import cn.wxl475.mapper.RecordMapper;
import cn.wxl475.pojo.base.Record.Record;
import cn.wxl475.service.RecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class RecordServiceImpl extends ServiceImpl<RecordMapper, Record> implements RecordService {
}
