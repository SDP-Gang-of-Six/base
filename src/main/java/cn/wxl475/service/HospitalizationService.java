package cn.wxl475.service;

import cn.wxl475.pojo.Page;
import cn.wxl475.pojo.base.hospitalization.Hospitalization;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.ArrayList;

public interface HospitalizationService extends IService<Hospitalization> {
    Long create(Hospitalization hospitalization);

    void delete(ArrayList<Long> hospitalizationIds);

    Page<Hospitalization> select(String allField, String keyword, Integer pageNum, Integer pageSize, String sortField, Integer sortOrder);

    Long update(Hospitalization hospitalization);

    Hospitalization selectById(Long hospitalizationId);
}
