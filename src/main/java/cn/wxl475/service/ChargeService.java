package cn.wxl475.service;

import cn.wxl475.pojo.Page;
import cn.wxl475.pojo.base.Charge.Charge;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ChargeService extends IService<Charge> {
    Page<Charge> searchChargesWithKeyword(String keyword, Integer pageNum, Integer pageSize, String sortField, Integer sortOrder);

    Charge selectById(Long chargeId);
}
