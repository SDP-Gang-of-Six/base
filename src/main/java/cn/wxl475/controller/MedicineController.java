package cn.wxl475.controller;

import cn.wxl475.pojo.Result;
import cn.wxl475.pojo.base.Medicine;
import cn.wxl475.service.MedicineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;

@Slf4j
@RestController
@RequestMapping("/base/medicine")
public class MedicineController {

    private final MedicineService medicineService;

    @Autowired
    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    @PostMapping("/create")
    public Result create(@RequestBody Medicine medicine) {
        if (medicine == null) {
            return Result.error("药品创建信息为空。");
        }
        String errorMsg = getErrorMsg(medicine);
        if (medicine.getMedicineDetail() == null || medicine.getMedicineDetail().isEmpty()) {
            medicine.setMedicineDetail("暂无药品详细信息。");
        }
        if (!errorMsg.isEmpty()) {
            return Result.error(errorMsg);
        }
        return Result.success(medicineService.create(medicine));
    }

    @PostMapping("/delete")
    public Result delete(@RequestBody ArrayList<Long> medicineIds) {
        if(medicineIds==null||medicineIds.isEmpty()){
            return Result.error("无药品需要删除。");
        }
        try {
            medicineService.delete(medicineIds);
        }catch (Exception e){
            log.info(Arrays.toString(e.getStackTrace()));
            return Result.error(e.getMessage());
        }
        return Result.success(true);
    }

    @PostMapping("/update")
    public Result update(@RequestBody ArrayList<Medicine> medicines){
        if(medicines==null||medicines.isEmpty()){
            return Result.error("无药品需要更新。");
        }
        return Result.success(medicineService.update(medicines));
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
        return Result.success(medicineService.searchByKeyword(keyword,pageNum,pageSize,sortField,sortOrder));
    }

    private static String getErrorMsg(Medicine medicine) {
        String errorMsg = "";
        if (medicine.getMedicineName() == null || medicine.getMedicineName().isEmpty()) {
            errorMsg += "药品名称为空。";
        }
        if (medicine.getMedicineUsage() == null || medicine.getMedicineUsage().isEmpty()) {
            errorMsg += "药品用法为空。";
        }
        if (medicine.getMedicinePrescriptionCategory() == null || medicine.getMedicinePrescriptionCategory().isEmpty()) {
            errorMsg += "药品处方类别为空。";
        }
        if (medicine.getMedicineStock() == null) {
            errorMsg += "药品存量信息为空。";
        }
        return errorMsg;
    }
}
