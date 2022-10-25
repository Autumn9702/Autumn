package cn.autumn.wxserver.mapper;

import cn.autumn.entity.mysql.CityCode;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: autumn
 * created in 2022/9/11 Class CityCodeMapper
 */
@Repository
public interface CityCodeMapper extends BaseMapper<CityCode> {

    List<CityCode> queryMatchCityName(String cityName);

    String queryCityName(Integer cityCode);
}
