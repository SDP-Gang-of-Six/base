package cn.wxl475.service;

import cn.wxl475.pojo.base.department.Department;
import com.baomidou.mybatisplus.extension.service.IService;

public interface DepartmentService extends IService<Department> {
    Department create(Department department);

    Boolean delete(Department department);
}
