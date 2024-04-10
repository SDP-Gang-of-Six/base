package cn.wxl475.controller;

import cn.wxl475.pojo.Result;
import cn.wxl475.pojo.base.Charge.Charge;
import cn.wxl475.repo.ChargeEsRepo;
import cn.wxl475.service.ChargeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
        String petType = charge.getPetType();
        Integer registerFee = charge.getRegisterFee();
        Integer itemFee = charge.getItemFee();
        Integer operationFee = charge.getOperationFee();
        String detail = charge.getDetail();
        if(payer == null || payer.isEmpty()) {
            return Result.error("缴费人不能为空");
        }
        if(petType == null || petType.isEmpty()) {
            return Result.error("宠物种类不能为空");
        }
        if(registerFee == null) {
            return Result.error("检查费用不能为空");
        }
        if(itemFee == null) {
            return Result.error("化验费用不能为空");
        }
        if(operationFee == null) {
            return Result.error("手术费用不能为空");
        }
        if(detail == null || detail.isEmpty()) {
            return Result.error("详细信息不能为空");
        }
        charge.setSum(registerFee + itemFee + operationFee);
        chargeService.save(charge);
        chargeEsRepo.save(charge);
        return Result.success();
    }

    @PostMapping("/deleteCharges")
    public Result deleteCharges(@RequestHeader("Authorization") String token, @RequestBody List<Long> ids) {
        if(ids == null|| ids.isEmpty()){
            return Result.error("无收费条目需要删除");
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

    @PostMapping("/updateCharge")
    public Result updateCharge(@RequestHeader("Authorization") String token, @RequestBody Charge charge) {
        if(charge == null) {
            return Result.error("没有收费条目需要修改");
        }
        charge.setSum(charge.getRegisterFee() + charge.getItemFee() + charge.getOperationFee());
        chargeService.updateById(charge);
        Long id = charge.getChargeId();
        Charge newCharge = chargeService.selectById(id);
        chargeEsRepo.save(newCharge);
        stringRedisTemplate.delete(CACHE_CHARGES_KEY + id);
        return Result.success();
    }

    @PostMapping("/searchChargesByKeyword")
    public Result searchChargesByKeyword(@RequestHeader("Authorization") String token,
                                         @RequestParam(required = false) String keyword,
                                         @RequestParam(required = false) Integer minFee,
                                         @RequestParam(required = false) Integer maxFee,
                                         @RequestParam Integer pageNum,
                                         @RequestParam Integer pageSize,
                                         @RequestParam(required = false) String sortField,
                                         @RequestParam(required = false) Integer sortOrder){
        if(pageNum <= 0 || pageSize <= 0){
            return Result.error("页码或页大小不合法");
        }
        return Result.success(chargeService.searchChargesWithKeyword(keyword,minFee,maxFee,pageNum,pageSize,sortField,sortOrder));
    }

    @GetMapping("/getChargeById/{chargeId}")
    public Result getChargeById(@RequestHeader("Authorization") String token, @PathVariable Long chargeId) {
        Charge charge = chargeService.selectById(chargeId);
        return Result.success(charge);
    }

}
