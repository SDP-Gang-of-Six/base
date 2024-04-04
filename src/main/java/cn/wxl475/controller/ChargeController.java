package cn.wxl475.controller;

import cn.wxl475.service.ChargeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/base/charge")
public class ChargeController {
    @Autowired
    private ChargeService chargeService;


}
