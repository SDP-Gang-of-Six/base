package cn.wxl475.service.impl;

import cn.wxl475.mapper.HospitalizationMapper;
import cn.wxl475.pojo.base.hospitalization.Hospitalization;
import cn.wxl475.service.HospitalizationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class HospitalizationServiceImpl extends ServiceImpl<HospitalizationMapper, Hospitalization> implements HospitalizationService {
}
