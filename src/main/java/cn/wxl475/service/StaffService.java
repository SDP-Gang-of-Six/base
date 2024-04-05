package cn.wxl475.service;

import cn.wxl475.pojo.Page;
import cn.wxl475.pojo.base.Staff;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.ArrayList;

public interface StaffService extends IService<Staff> {
    Staff create(Staff staff);

    void delete(ArrayList<Long> staffIds)throws Exception;

    ArrayList<Staff> update(ArrayList<Staff> staffs);

    boolean staffNameIsInUse(String staffName);

    Page<Staff> searchByKeyword(String keyword, Integer pageNum, Integer pageSize, String sortField, Integer sortOrder);

    ArrayList<Staff> searchByIds(ArrayList<Long> staffIds);
}
