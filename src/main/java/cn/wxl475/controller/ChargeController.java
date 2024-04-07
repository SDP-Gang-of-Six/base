package cn.wxl475.controller;

import cn.wxl475.pojo.Result;
import cn.wxl475.pojo.base.Charge.Charge;
import cn.wxl475.repo.ChargeEsRepo;
import cn.wxl475.service.ChargeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

import static cn.wxl475.redis.RedisConstants.CACHE_CHARGES_KEY;

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


    @PostMapping("/addCharge")
    public Result addCharge(@RequestHeader("Authorization") String token, @RequestBody Charge charge) {
        String payer = charge.getPayer();
        String illnessName = charge.getIllnessName();
        String illnessType = charge.getIllnessType();
        String petType = charge.getPetType();
        Integer money = charge.getMoney();
        if(payer == null || payer.isEmpty()) {
            return Result.error("缴费人不能为空");
        }
        if(illnessType == null || illnessType.isEmpty()) {
            return Result.error("病例类型不能为空");
        }
        if(illnessName == null || illnessName.isEmpty()) {
            return Result.error("病例名称不能为空");
        }
        if(petType == null || petType.isEmpty()) {
            return Result.error("宠物种类不能为空");
        }
        if(money == null) {
            return Result.error("缴费金额不能为空");
        }
        chargeService.save(charge);
        chargeEsRepo.save(charge);
        return Result.success();
    }

    @PostMapping("/deleteCharges")
    public Result deleteCharges(@RequestHeader("Authorization") String token, @RequestBody List<Long> ids) {
        if(ids == null|| ids.isEmpty()){
            return Result.error("无科室需要删除");
        }
        try {
            chargeService.removeBatchByIds(ids);
            chargeEsRepo.deleteAllById(ids);
            for(Long id: ids) {
                stringRedisTemplate.delete(CACHE_CHARGES_KEY + id);
            }
        } catch (Exception e) {
            log.info(Arrays.toString(e.getStackTrace()));
            return Result.error(e.getMessage());
        }
        return Result.success();
    }

    @PostMapping("/updateCharges")
    public Result updateCharges(@RequestHeader("Authorization") String token, @RequestBody Charge charge) {
        if(charge == null) {
            return Result.error("没有收费条目需要修改");
        }
        chargeService.updateById(charge);
        chargeEsRepo.delete(charge);
        Long id = charge.getChargeId();
        stringRedisTemplate.delete(CACHE_CHARGES_KEY + id);
        return Result.success();
    }

    @PostMapping("/searchChargesByKeyword")
    public Result searchIllnessByKeyword(@RequestHeader("Authorization") String token,
                                         @RequestParam(required = false) String keyword,
                                         @RequestParam Integer pageNum,
                                         @RequestParam Integer pageSize,
                                         @RequestParam(required = false) String sortField,
                                         @RequestParam(required = false) Integer sortOrder){
        if(pageNum <= 0 || pageSize <= 0){
            return Result.error("页码或页大小不合法");
        }
        return Result.success(chargeService.searchChargesWithKeyword(keyword,pageNum,pageSize,sortField,sortOrder));
    }

    @GetMapping("/getChargeById/{chargeId}")
    public Result getChargeById(@RequestHeader("Authorization") String token, Long chargeId) {
        return Result.success(chargeService.selectById(chargeId));
    }

}
