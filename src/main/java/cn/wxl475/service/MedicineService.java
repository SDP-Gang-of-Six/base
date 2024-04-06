package cn.wxl475.service;

import cn.wxl475.pojo.Page;
import cn.wxl475.pojo.base.Medicine;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.ArrayList;

public interface MedicineService extends IService<Medicine> {
    Medicine create(Medicine medicine);

    void delete(ArrayList<Long> medicineIds)throws Exception;

    ArrayList<Medicine> update(ArrayList<Medicine> medicines);

    Page<Medicine> searchByKeyword(String keyword, Integer pageNum, Integer pageSize, String sortField, Integer sortOrder);
}
