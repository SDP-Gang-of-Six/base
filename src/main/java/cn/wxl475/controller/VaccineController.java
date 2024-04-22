package cn.wxl475.controller;

import cn.wxl475.pojo.Result;
import cn.wxl475.pojo.base.vaccine.Vaccine;
import cn.wxl475.service.VaccineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/base/vaccine")
public class VaccineController {

    @Autowired
    private VaccineService vaccineService;

    @PostMapping("create")
    public Result create(@RequestBody Vaccine vaccine){
        return Result.success(vaccineService.create(vaccine));
    }

    @PostMapping("delete")
    public Result delete(@RequestBody ArrayList<Long> vaccineIds){
        vaccineService.delete(vaccineIds);
        return Result.success();
    }

    @PostMapping("update")
    public Result update(@RequestBody Vaccine vaccine){
        return Result.success(vaccineService.update(vaccine));
    }

    @GetMapping("select")
    public Result select(@RequestParam(required = false) String keyword,
                         @RequestParam Integer pageNum,
                         @RequestParam Integer pageSize,
                         @RequestParam(required = false) String sortField,
                         @RequestParam(required = false) Integer sortOrder){
        if(pageNum<=0||pageSize<=0){
            return Result.error("页码或页大小不合法");
        }
        return Result.success(vaccineService.select(keyword, pageNum, pageSize, sortField, sortOrder));
    }

    @PostMapping("selectById")
    public Result selectById(@RequestBody Vaccine vaccine){
        return Result.success(vaccineService.selectById(vaccine.getVaccineId()));
    }

}
