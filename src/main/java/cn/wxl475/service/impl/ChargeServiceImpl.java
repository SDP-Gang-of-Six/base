package cn.wxl475.service.impl;

import cn.wxl475.mapper.ChargeMapper;
import cn.wxl475.pojo.base.Charge.Charge;
import cn.wxl475.service.ChargeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ChargeServiceImpl extends ServiceImpl<ChargeMapper, Charge> implements ChargeService {
}
