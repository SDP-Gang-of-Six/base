package cn.wxl475.controller;

import cn.wxl475.repo.ChargeEsRepo;
import cn.wxl475.service.ChargeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/base/charge")
public class ChargeController {
    @Autowired
    private ChargeService chargeService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ChargeEsRepo chargeEsRepo;

    @Value("${jwt.signKey}")
    private String signKey;

//    @PostMapping("/addCharge")
//    public Result addCharge(@RequestHeader("Authorization") String token, @RequestBody Charge charge) {
//
//    }
}
