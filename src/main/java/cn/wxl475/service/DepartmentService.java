package cn.wxl475.service;

import cn.wxl475.pojo.Page;
import cn.wxl475.pojo.base.department.Department;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.ArrayList;

public interface DepartmentService extends IService<Department> {
    Department create(Department department);

    void delete(ArrayList<Long> departmentIds)throws Exception;

    ArrayList<Department> update(ArrayList<Department> departments);

    Boolean departmentRoomNumberIsInUse(Integer departmentRoomNumber);

    Page<Department> searchByKeyword(String keyword, Integer pageNum, Integer pageSize, String sortField, Integer sortOrder);
}
