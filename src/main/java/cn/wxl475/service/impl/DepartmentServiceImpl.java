package cn.wxl475.service.impl;

import cn.wxl475.mapper.DepartmentMapper;
import cn.wxl475.pojo.base.department.Department;
import cn.wxl475.service.DepartmentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {
}
