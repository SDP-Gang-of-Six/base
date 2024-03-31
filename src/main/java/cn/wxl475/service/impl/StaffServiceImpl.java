package cn.wxl475.service.impl;

import cn.wxl475.mapper.StaffMapper;
import cn.wxl475.pojo.base.Staff;
import cn.wxl475.service.StaffService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class StaffServiceImpl extends ServiceImpl<StaffMapper, Staff> implements StaffService {
}
