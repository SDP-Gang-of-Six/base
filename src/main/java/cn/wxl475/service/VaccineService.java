package cn.wxl475.service;

import cn.wxl475.pojo.Page;
import cn.wxl475.pojo.base.hospitalization.Hospitalization;
import cn.wxl475.pojo.base.vaccine.Vaccine;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.ArrayList;

public interface VaccineService extends IService<Vaccine> {


    Page<Vaccine> select(String keyword, Integer pageNum, Integer pageSize, String sortField, Integer sortOrder);

    void delete(ArrayList<Long> vaccineIds);

    Long create(Vaccine vaccine);

    Long update(Vaccine vaccine);

    Vaccine selectById(Long vaccineId);
}
