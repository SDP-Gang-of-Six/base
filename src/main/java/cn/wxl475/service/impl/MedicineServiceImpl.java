package cn.wxl475.service.impl;

import cn.wxl475.mapper.MedicineMapper;
import cn.wxl475.pojo.base.Medicine;
import cn.wxl475.service.MedicineService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class MedicineServiceImpl extends ServiceImpl<MedicineMapper, Medicine> implements MedicineService {
}
