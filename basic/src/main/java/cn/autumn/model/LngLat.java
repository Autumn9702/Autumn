package cn.autumn.model;

import lombok.Data;

/**
 * @author cf
 * Created in 15:38 2022/9/26
 */
@Data
public class LngLat {

    private Double lat;

    private Double lng;

    public LngLat(Double lng, Double lat) {
        this.lat = lat;
        this.lng = lng;
    }

    public LngLat() {
    }

}
