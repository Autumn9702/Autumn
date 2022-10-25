package cn.autumn.companyserver.controller;


import cn.autumn.companyserver.entity.Error;
import cn.autumn.companyserver.entity.Region;
import cn.autumn.companyserver.model.ArgsModel;
import cn.autumn.companyserver.repository.DivisionRepository;
import cn.autumn.companyserver.repository.ErrorRepository;
import cn.autumn.companyserver.repository.RegionRepository;
import cn.autumn.companyserver.serve.DivisionConvert;
import cn.autumn.companyserver.service.ProcessData;
import cn.autumn.enumeration.cpf.Division;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author cf
 * Created in 14:00 2022/9/27
 */
@RestController
@RequestMapping("/comp")
public class CompController {

    private static List<String> ERR_REGS = new ArrayList<>();
    private static List<String> DIV_REGS = new ArrayList<>();

    @Resource
    private DivisionConvert divisionConvert;

    @Resource
    private RegionRepository regionRepository;

    @Resource
    private DivisionRepository divisionRepository;

    @Resource
    private ErrorRepository errorRepository;

    @Resource
    private ProcessData processData;

    @PostMapping("/prodiv")
    public ResponseEntity<Object> processDivision(@RequestBody ArgsModel am) {
        for (String reg : DIV_REGS) {
            if (reg.contains(am.getName())) {
                return ResponseEntity.ok("");
            }
        }
        boolean b = divisionConvert.arBd09ToWgs84(am);
        DIV_REGS.add(am.getName());
        return ResponseEntity.ok(b);
    }

    @GetMapping("/get/region/all")
    public ResponseEntity<Object> getRegionAll() {
        List<Region> regionAll = regionRepository.getRegionAll();
        return ResponseEntity.ok(regionAll);
    }

    @GetMapping("/get/same/region")
    public ResponseEntity<Object> getSameRegion() {
        return ResponseEntity.ok(processData.jointColumn());
    }

    @GetMapping("/get/all/region")
    public ResponseEntity<Object> getAllRegion() {
        List<Region> regionAll = regionRepository.getRegionAll();
        DIV_REGS = divisionRepository.getDivisionNameAll();
        ERR_REGS = errorRepository.getErrAllStr();
        for (String regName : DIV_REGS) {
            regionAll.removeIf(r -> regName.contains(r.getName()) ||
                    Division.SX_DISTRICT.getVul().equals(r.getName()) ||
                    Division.SX_COUNTY.getVul().equals(r.getName()) ||
                    Division.S_ADMINISTRATIVE_UNITS.getVul().equals(r.getName()) ||
                    Division.COUNTY.getVul().equals(r.getName()) ||
                    Division.SXN_CITY.getVul().equals(r.getName()));
        }
        for (String errReg : ERR_REGS) {
            regionAll.removeIf(region -> errReg.contains(region.getName()));
        }
        return ResponseEntity.ok(regionAll);
    }

    @PostMapping("/manual/process/xzqh")
    public ResponseEntity<Object> processDivisionManual(@RequestParam("xzqh") String xzqh) throws InterruptedException {
        divisionConvert.manualProcessDivision(xzqh);
        return ResponseEntity.ok("");
    }

    @DeleteMapping("/delete/same/data")
    public ResponseEntity<Object> deleteSameData() throws InterruptedException {
        processData.deleteSameData();
        return ResponseEntity.ok("");
    }

    @PostMapping("/save/error/region")
    public ResponseEntity<Object> saveErrorRegion(@RequestBody Map<String, String> errObj) {
        String errReg = errObj.get("errReg");
        if (StringUtils.isEmpty(errReg)) {
            return ResponseEntity.ok("");
        }
        for (String reg : ERR_REGS) {
            if (reg.contains(errReg)) {
                return ResponseEntity.ok("");
            }
        }
        errorRepository.save(new Error(errReg));
        ERR_REGS.add(errReg);
        return ResponseEntity.ok("");
    }
}
