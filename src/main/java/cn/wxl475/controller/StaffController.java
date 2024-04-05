package cn.wxl475.controller;

import cn.wxl475.pojo.Result;
import cn.wxl475.pojo.base.Staff;
import cn.wxl475.service.StaffService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;

@Slf4j
@RestController
@RequestMapping("/base/staff")
public class StaffController {

    private final StaffService staffService;

    @Autowired
    public StaffController(StaffService staffService){
        this.staffService = staffService;
    }

    @PostMapping("/create")
    public Result create(@RequestBody Staff staff){
        if(staff==null){
            return Result.error("人员创建信息为空。");
        }
        String errorMsg="";
        if(staff.getStaffName()==null||staff.getStaffName().isEmpty()){
            errorMsg+="人员姓名为空。";
        }
        if(staff.getStaffGender()==null||staff.getStaffGender().isEmpty()){
            errorMsg+="人员性别为空。";
        }
        if(staff.getStaffAge()==null){
            errorMsg+="人员年龄为空。";
        }
        if(staff.getStaffPosition()==null||staff.getStaffPosition().isEmpty()){
            errorMsg+="人员职位为空。";
        }
        if(staff.getStaffPhoneNumber()==null||staff.getStaffPhoneNumber().isEmpty()){
            errorMsg+="人员电话号码为空。";
        }
        if(staffService.staffNameIsInUse(staff.getStaffName())){
            errorMsg+="人员姓名已存在。";
        }
        if(!errorMsg.isEmpty()){
            return Result.error(errorMsg);
        }
        return Result.success(staffService.create(staff));
    }

    @PostMapping("/delete")
    public Result delete(@RequestBody ArrayList<Long> staffIds){
        if(staffIds==null||staffIds.isEmpty()){
            return Result.error("无人员需要删除。");
        }
        try {
            staffService.delete(staffIds);
        }catch (Exception e){
            log.info(Arrays.toString(e.getStackTrace()));
            return Result.error(e.getMessage());
        }
        return Result.success(true);
    }

    @PostMapping("/update")
    public Result update(@RequestBody ArrayList<Staff> staffs){
        if(staffs==null||staffs.isEmpty()){
            return Result.error("无人员需要更新。");
        }
        return Result.success(staffService.update(staffs));
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
        return Result.success(staffService.searchByKeyword(keyword,pageNum,pageSize,sortField,sortOrder));
    }

    @PostMapping("/searchByIdsOrAll")
    public Result searchByIdsOrAll(@RequestBody(required = false) ArrayList<Long> staffIds){
        if(staffIds==null||staffIds.isEmpty()){
            return Result.success(staffService.list());
        }
        return Result.success(staffService.listByIds(staffIds));
    }
}
