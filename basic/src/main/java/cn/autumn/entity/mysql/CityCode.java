package cn.autumn.entity.mysql;

import cn.autumn.entity.mybatis.DataBase;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * author: autumn
 * created in 2022/9/11 Class CityCode
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_city_code")
public final class CityCode extends DataBase {

    private String cityName;

    private Integer cityCode;

    public CityCode() {}

    public CityCode(String cityName, Integer cityCode) {
        this.cityName = cityName;
        this.cityCode = cityCode;
    }
}
