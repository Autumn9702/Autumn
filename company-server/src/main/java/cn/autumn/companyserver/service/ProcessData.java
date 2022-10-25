package cn.autumn.companyserver.service;

import cn.autumn.companyserver.entity.Region;
import cn.autumn.companyserver.repository.DivisionRepository;
import cn.autumn.companyserver.repository.RegionRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cf
 * Created in 10:07 2022/10/21
 */
@Service
public class ProcessData {

    @Resource
    private RegionRepository regionRepository;

    @Resource
    private DivisionRepository divisionRepository;

    public List<String> jointColumn() {
        List<Region> sameDivision = regionRepository.getRegionRepeat();
        List<Region> regionAll = regionRepository.getRegionAll();
        List<String> jointNames = new ArrayList<>();
        for (Region r : sameDivision) {
            for (Region ra : regionAll) {
                if (r.getPid().equals(ra.getId())) {
                    if ("市辖区".equals(ra.getName()) || "市辖县".equals(ra.getName()) || "县".equals(ra.getName())) {
                        for (Region ra1 : regionAll) {
                            if (ra.getPid().equals(ra1.getId())) {
                                jointNames.add(ra1.getName() + r.getName());
                            }
                        }
                    }else {
                        jointNames.add(ra.getName() + r.getName());
                    }
                }
            }
        }
        return jointNames;
    }

    public void deleteSameData() {
        List<Region> sameDivision = regionRepository.getRegionRepeat();
        for (Region region : sameDivision) {
            divisionRepository.delRegionRepeat(region.getName());
        }
    }
}
