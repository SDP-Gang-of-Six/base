package cn.wxl475.controller;

import cn.wxl475.pojo.Result;
import cn.wxl475.pojo.base.hospitalization.Hospitalization;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/base/hospitalization")
public class HospitalizationController {

    @PostMapping("create")
    public Result create(@RequestBody Hospitalization hospitalization){

        return Result.success();
    }

}
