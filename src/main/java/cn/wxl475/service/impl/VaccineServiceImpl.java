package cn.wxl475.service.impl;

import cn.wxl475.mapper.VaccineMapper;
import cn.wxl475.pojo.base.vaccine.Vaccine;
import cn.wxl475.service.VaccineService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class VaccineServiceImpl extends ServiceImpl<VaccineMapper, Vaccine> implements VaccineService {
}
