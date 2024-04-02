package cn.wxl475.controller;

import cn.wxl475.pojo.Result;
import cn.wxl475.pojo.base.department.Department;
import cn.wxl475.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/base/department")
public class DepartmentController {

    private final DepartmentService departmentService;

    @Autowired
    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @PostMapping("/create")
    public Result create(@RequestBody Department department){
        String errorMsg="";
        String departmentName = department.getDepartmentName();
        String departmentType = department.getDepartmentType();
        String departmentPrincipal = department.getDepartmentPrincipal();
        Integer departmentRoomNumber = department.getDepartmentRoomNumber();

        if(departmentName ==null || departmentName.isEmpty()) {
            errorMsg += "科室名称不能为空。";
        }
        if(departmentType ==null || departmentType.isEmpty()) {
            errorMsg += "科室类型不能为空。";
        }
        if(departmentPrincipal ==null || departmentPrincipal.isEmpty()) {
            errorMsg += "科室负责人不能为空。";
        }
        if(departmentRoomNumber ==null) {
            errorMsg += "科室房间号不能为空。";
        }
        if(!errorMsg.isEmpty()) {
            return Result.error(errorMsg);
        }

        return Result.success(departmentService.create(department));
    }

    @PostMapping("/delete")
    public Result delete(@RequestBody Department department){
        return Result.success(departmentService.delete(department));
    }
}
