package cn.autumn.util;

import org.springframework.data.geo.Point;

/**
 * @author cf
 * Created in 16:00 2022/9/26
 */
public final class PositionUtil {

    public static double pi = 3.1415926535897932384626;
    public static double x_pi = pi * 3000.0 / 180.0;
    private static double a = 6378137.0;
    private static double df = 298.257222101;
    private static double b = a * (1 - 1 / df);
    private static double ee = (Math.pow(a, 2) - Math.pow(b, 2)) / Math.pow(a, 2);

    public static double[] mercator2lonLat(double mercatorX,double mercatorY) {

        double[] xy = new double[2];
        double x = mercatorX/20037508.34*180;
        double y = mercatorY/20037508.34*180;
        y= 180/pi*(2*Math.atan(Math.exp(y*pi/180))-pi/2);

        xy[0] = x;
        xy[1] = y;
        return xy;

    }

    public static Point bd09ToGcj02(double lat, double lng) {
        double x = lng - 0.0065, y = lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
        double ggLon = z * Math.cos(theta);
        double ggLat = z * Math.sin(theta);
        return new Point(ggLat, ggLon);
    }

    public static Point gcj02ToWgs84(double lat, double lon) {
        Point point1 = new Point(lat, lon);

        double lon1 = point1.getY();
        double lat1 = point1.getX();
        Point point2 = wgs84ToGcj02(lat1, lon1);
        double lontitude = lon - (point2.getY() - lon1);
        double latitude = (lat - (point2.getX() - lat1));

        /*double lontitude = lon * 2 - gps.getLng();
        double latitude = lat * 2 - gps.getLat();*/


        return new Point(latitude, lontitude);
    }

    public static Point wgs84ToGcj02(double lat, double lng) {
        if (outOfChina(lat, lng)) {
            return new Point(lat, lng);
        }
        double dLat = transformLat(lng - 105.0, lat - 35.0);
        double dLon = transformLon(lng - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
        double mgLat = lat + dLat;
        double mgLon = lng + dLon;
        return new Point(mgLat, mgLon);
    }

    public static boolean outOfChina(double lat, double lng) {
        if (lng < 72.004 || lng > 137.8347) {
            return true;
        }
        if (lat < 0.8293 || lat > 55.8271) {
            return true;
        }
        return false;
    }

    public static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y
                + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    public static double transformLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1
                * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0
                * pi)) * 2.0 / 3.0;
        return ret;
    }

    public static Point bd09ToWgs84(double lat, double lng) {

        Point gcj02 = bd09ToGcj02(lat, lng);
        return gcj02ToWgs84(gcj02.getX(), gcj02.getY());

    }
}
