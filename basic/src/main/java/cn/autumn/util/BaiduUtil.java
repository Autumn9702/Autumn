package cn.autumn.util;

import cn.autumn.model.LngLat;

import java.util.*;

/**
 * @author cf
 * Created in 16:02 2022/9/27
 */
public final class BaiduUtil {

    public static Map<Double, Double> strToMap(String data) {
        String[] split = data.split(",");
        List<String> eps = Arrays.asList(split);
        Map<Double, Double> epM = new HashMap<>();
        int size = eps.size();
        for (int i = 0; i < size; i++) {
            epM.put(Double.parseDouble(eps.get(i)), Double.parseDouble(eps.get(++i)));
        }
        return epM;
    }

    public static List<LngLat> convertCoordinate(Map<Double, Double> epm) {
        List<LngLat> ad = new ArrayList<>();
        for (Map.Entry<Double, Double> entry : epm.entrySet()) {
            double[] xy = PositionUtil.mercator2lonLat(entry.getKey(), entry.getValue());
            ad.add(new LngLat(xy[0], xy[1]));
        }
        return ad;
    }

    public static String filterStr(String geo) {
        String cityArea = geo.replace("4|", "")
                .replace(" ", "")
                .replaceAll("\\|\\d+-", ",")
                .replaceAll(";\\d+-", ",")
                .replace("-", ",")
                .replace(";", ",")

                ;
        cityArea = cityArea.substring(0, cityArea.length() -1);

        return cityArea;
    }

}