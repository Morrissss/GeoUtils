package cn.morrissss.geobase.utils;

import cn.morrissss.geobase.entity.GeoPoint;

public class OffsetUtils {
	
 	private static final double A = 6378137.0;
    private static final double EE = 0.00669342162296594323;
    private static final double X_PI = Math.PI * 3000.0 / 180.0;

    private static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x * Math.PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * Math.PI) + 40.0 * Math.sin(y / 3.0 * Math.PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * Math.PI) + 320 * Math.sin(y * Math.PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static double transformLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x * Math.PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * Math.PI) + 40.0 * Math.sin(x / 3.0 * Math.PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * Math.PI) + 300.0 * Math.sin(x / 30.0 * Math.PI)) * 2.0 / 3.0;
        return ret;
    }
    
    public static GeoPoint wgs84ToGcj02(GeoPoint point) {
    	if (point == null || !point.legal())
    		return point;
    	if (!GeoUtils.inChina(point))
    		return point;
        double wgLat = point.getLat();
        double wgLon = point.getLng();
        double dLat = transformLat(wgLon - 105.0, wgLat - 35.0);
        double dLon = transformLon(wgLon - 105.0, wgLat - 35.0);
        double radLat = wgLat / 180.0 * Math.PI;
        double magic = Math.sin(radLat);
        magic = 1 - EE * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((A * (1 - EE)) / (magic * sqrtMagic) * Math.PI);
        dLon = (dLon * 180.0) / (A / sqrtMagic * Math.cos(radLat) * Math.PI);
        return new GeoPoint(wgLat + dLat, wgLon + dLon);
    }

    public static GeoPoint gcj02ToWgs84(GeoPoint point) {
    	if (point == null || !point.legal())
    		return point;
    	if (!GeoUtils.inChina(point))
    		return point;
    	GeoPoint c2 = wgs84ToGcj02(point);
        return new GeoPoint(2 * point.getLat() - c2.getLat(), 2 * point.getLng() - c2.getLng());
    }

    public static GeoPoint mapbarToWgs84(GeoPoint point) {
    	if (point == null || !point.legal())
    		return point;
    	if (!GeoUtils.inChina(point))
    		return point;
        double lng = point.getLng() * 100000 % 36000000;
        double lat = point.getLat() * 100000 % 36000000;
        double lng1 = (int) (-(((Math.cos(lat / 100000)) * (lng / 18000)) + ((Math.sin(lng / 100000)) * (lat / 9000))) + lng);
        double lat1 = (int) (-(((Math.sin(lat / 100000)) * (lng / 18000)) + ((Math.cos(lng / 100000)) * (lat / 9000))) + lat);
        double lng2 = (int) (-(((Math.cos(lat1 / 100000)) * (lng1 / 18000)) + ((Math.sin(lng1 / 100000)) * (lat1 / 9000))) + lng + ((lng > 0) ? 1 : -1));
        double lat2 = (int) (-(((Math.sin(lat1 / 100000)) * (lng1 / 18000)) + ((Math.cos(lng1 / 100000)) * (lat1 / 9000))) + lat + ((lat > 0) ? 1 : -1));
        return new GeoPoint(lat2 / 100000.0, lng2 / 100000.0);
    }

    public static GeoPoint wgs84ToMapbar(GeoPoint point) {
    	if (point == null || !point.legal())
    		return point;
    	if (!GeoUtils.inChina(point))
    		return point;
        GeoPoint c2 = mapbarToWgs84(point);
        return new GeoPoint(2 * point.getLat() - c2.getLat(), 2 * point.getLng() - c2.getLng());
    }

    private static GeoPoint gcj02ToBaidu(GeoPoint point) {
    	if (point == null || !point.legal())
    		return point;
    	if (!GeoUtils.inChina(point))
    		return point;
        double x = point.getLng();
        double y = point.getLat();
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * X_PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * X_PI);
        double lngConv = z * Math.cos(theta) + 0.0065;
        double latConv = z * Math.sin(theta) + 0.006;
        return new GeoPoint(latConv, lngConv);
    }

    private static GeoPoint baiduToGcj02(GeoPoint point) {
    	if (point == null || !point.legal())
    		return point;
    	if (!GeoUtils.inChina(point))
    		return point;
        double x = point.getLng() - 0.0065;
        double y = point.getLat() - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * X_PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * X_PI);
        double lngConv = z * Math.cos(theta);
        double latConv = z * Math.sin(theta);
        return new GeoPoint(latConv, lngConv);
    }

    public static GeoPoint baiduToWgs84(GeoPoint point) {
    	if (point == null || !point.legal())
    		return point;
    	if (!GeoUtils.inChina(point))
    		return point;
        GeoPoint pointConv = baiduToGcj02(point);
        if (null != pointConv)
            pointConv = gcj02ToWgs84(pointConv);
        return pointConv;
    }

    public static GeoPoint wgs84ToBaidu(GeoPoint point) {
    	if (point == null || !point.legal())
    		return point;
    	if (!GeoUtils.inChina(point))
    		return point;
        GeoPoint pointConv = wgs84ToGcj02(point);
        if (null != pointConv)
            pointConv = gcj02ToBaidu(pointConv);
        return pointConv;
    }
    
    public static GeoPoint googleToWgs84(GeoPoint point) {
    	if (point == null || !point.legal())
    		return point;
    	if (!GeoUtils.inChinaMainLand(point))
    		return point;
    	return gcj02ToWgs84(point);
    }
    
    public static GeoPoint wgs84ToGoogle(GeoPoint point) {
    	if (point == null || !point.legal())
    		return point;
    	if (!GeoUtils.inChinaMainLand(point))
    		return point;
    	return wgs84ToGcj02(point);
    }
}
