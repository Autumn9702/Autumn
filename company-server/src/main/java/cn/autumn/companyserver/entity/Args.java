package cn.autumn.companyserver.entity;

import cn.autumn.model.LngLat;
import lombok.Data;

import java.util.List;

/**
 * @author cf
 * Created in 14:20 2022/9/27
 */
@Data
public final class Args {

    private List<List<LngLat>> points;
}
