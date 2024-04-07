package cn.wxl475.controller;

import cn.wxl475.pojo.Result;
import cn.wxl475.pojo.base.hospitalization.Hospitalization;
import cn.wxl475.service.HospitalizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/base/hospitalization")
public class HospitalizationController {

    @Autowired
    private HospitalizationService hospitalizationService;

    @PostMapping("create")
    public Result create(@RequestBody Hospitalization hospitalization){
        return Result.success(hospitalizationService.create(hospitalization));
    }

    @PostMapping("delete")
    public Result delete(@RequestBody ArrayList<Long> hospitalizationIds){
        hospitalizationService.delete(hospitalizationIds);
        return Result.success();
    }

    @PostMapping("update")
    public Result update(@RequestBody Hospitalization hospitalization){
        return Result.success(hospitalizationService.update(hospitalization));
    }

    @PostMapping("select")
    public Result select(@RequestBody(required = false) String allField,
                         @RequestParam(required = false) String keyword,
                         @RequestParam Integer pageNum,
                         @RequestParam Integer pageSize,
                         @RequestParam(required = false) String sortField,
                         @RequestParam(required = false) Integer sortOrder){
        if(pageNum<=0||pageSize<=0){
            return Result.error("页码或页大小不合法");
        }
        return Result.success(hospitalizationService.select(allField, keyword, pageNum, pageSize, sortField, sortOrder));
    }

    @PostMapping("selectById")
    public Result selectById(@RequestBody Hospitalization hospitalization){
        return Result.success(hospitalizationService.selectById(hospitalization.getHospitalizationId()));
    }

}
