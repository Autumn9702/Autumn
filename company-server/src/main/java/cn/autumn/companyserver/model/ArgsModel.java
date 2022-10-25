package cn.autumn.companyserver.model;

import cn.autumn.model.LngLat;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * @author cf
 * Created in 14:16 2022/9/28
 */
@Data
public final class ArgsModel implements Serializable {

    private String name;

    private List<List<LngLat>> points;
}
