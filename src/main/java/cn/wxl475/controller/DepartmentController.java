package cn.wxl475.controller;

import cn.wxl475.pojo.Result;
import cn.wxl475.pojo.base.department.Department;
import cn.wxl475.service.DepartmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;

@Slf4j
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
        if(department==null){
            return Result.error("科室信息异常为空。");
        }
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
        if(departmentService.departmentRoomNumberIsInUse(departmentRoomNumber)) {
            errorMsg += "科室房间号已被使用。";
        }
        if(!errorMsg.isEmpty()) {
            return Result.error(errorMsg);
        }

        return Result.success(departmentService.create(department));
    }

    @PostMapping("/delete")
    public Result delete(@RequestBody ArrayList<Long> departmentIds){
        if(departmentIds==null||departmentIds.isEmpty()){
            return Result.error("无科室需要删除");
        }
        try {
            departmentService.delete(departmentIds);
        } catch (Exception e) {
            log.info(Arrays.toString(e.getStackTrace()));
            return Result.error(e.getMessage());
        }
        return Result.success(true);
    }

    @PostMapping("/update")
    public Result update(@RequestBody ArrayList<Department> departments){
        if(departments==null||departments.isEmpty()){
            return Result.error("无科室需要更新");
        }
        return Result.success(departmentService.update(departments));
    }

    @PostMapping("/searchByKeyword")
    public Result searchByKeyword(@RequestParam(required = false) String keyword,
                         @RequestParam Integer pageNum,
                         @RequestParam Integer pageSize,
                         @RequestParam(required = false) String sortField,
                         @RequestParam(required = false) Integer sortOrder){
        if(pageNum<=0||pageSize<=0){
            return Result.error("页码或页大小不合法");
        }
        return Result.success(departmentService.searchByKeyword(keyword,pageNum,pageSize,sortField,sortOrder));
    }

    @PostMapping("/searchByIdOrRoomNumber")
    public Result searchByIdOrRoomNumber(@RequestParam(required = false) Long departmentId,@RequestParam(required = false) Integer departmentRoomNumber){
        if(departmentId!=null&&departmentRoomNumber!=null){
            return Result.error("只能根据科室ID或房间号查询");
        }
        if(departmentId==null&&departmentRoomNumber==null){
            return Result.error("科室ID和房间号不能同时为空");
        }
        if(departmentId!=null){
            return Result.success(departmentService.searchById(departmentId));
        }
        return Result.success(departmentService.searchByRoomNumber(departmentRoomNumber));
    }
}
