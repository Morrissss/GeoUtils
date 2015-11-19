package cn.morrissss.geobase.utils;

import cn.morrissss.geobase.entity.GeoBound;
import cn.morrissss.geobase.entity.GeoBoundWithCity;
import cn.morrissss.geobase.entity.GeoPoint;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;

public class GeoUtils {

    private static final double CH_MIN_LNG = 73.33;
    private static final double CH_MAX_LNG = 135.05;
    private static final double CH_MIN_LAT = 15.51;
    private static final double CH_MAX_LAT = 53.33;
    
    private static GeoBound CHINA_BOUND = null;
    private static GeoBound HONGKONG_BOUND = null;
    private static GeoBound MACAU_BOUND = null;
    static {
    	BufferedReader br = null;
    	try {
    		 ClassLoader classLoader = GeoPoint.class.getClassLoader();
    //		String  file = classLoader.getResource("bounds.txt").getPath();  
   // 		System.out.println(file);
    		InputStream is=classLoader.getClass().getResourceAsStream("/"+"bounds.txt");
    		br = new BufferedReader(new InputStreamReader(is));
    		String boundStr = br.readLine();
    		CHINA_BOUND = new GeoBound(GeoBound.parseBound(boundStr));
    		boundStr = br.readLine();
    		HONGKONG_BOUND = new GeoBound(GeoBound.parseBound(boundStr));
    		boundStr = br.readLine();
    		MACAU_BOUND = new GeoBound(GeoBound.parseBound(boundStr));
    		br.close();
    	} catch (Exception e) {
    		System.err.println("Error in loading Chinese bound!");
    		e.printStackTrace();
    		System.exit(1);
    	} finally {
    		if (br != null) {
    			try {
    				br.close();
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    		}
    	}
    }
    
	public static final double EARTH_RADIUS_METER = 6378137;
	
	public static double deg2Rad(double angle) { return angle * Math.PI / 180; }
	
	public static double rad2Deg(double angle) { return angle * 180 / Math.PI; }
	
	public static double distanceInMeter(GeoPoint p1, GeoPoint p2) {
		if (p1==null || p2==null || !p1.legal() || !p2.legal())
			return Double.MIN_VALUE;
		double lat1 = p1.getLat();
		double lng1 = p1.getLng();
		double lat2 = p2.getLat();
		double lng2 = p2.getLng();
		// haversine formula
		double latDiffRad = deg2Rad(lat2 - lat1);
		double lngDiffRad = deg2Rad(lng2 - lng1);
		double lat1Rad = deg2Rad(lat1);
		double lat2Rad = deg2Rad(lat2);
		double a = Math.pow(Math.sin(latDiffRad / 2), 2);
		double b = Math.pow(Math.sin(lngDiffRad / 2), 2);
		double delta = 2* Math.asin(Math.sqrt(a + Math.cos(lat1Rad) * Math.cos(lat2Rad) * b));
		return EARTH_RADIUS_METER * delta;
	}
	
	public static double distanceInCoord(GeoPoint p1, GeoPoint p2) {
		if (p1==null || p2==null || !p1.legal() || !p2.legal())
			return Double.MIN_VALUE;
		double lat1 = p1.getLat();
		double lng1 = p1.getLng();
		double lat2 = p2.getLat();
		double lng2 = p2.getLng();
		double dLat = lat1 - lat2;
		double dLng = lng1 - lng2;
		return Math.sqrt(dLat * dLat + dLng * dLng);
	}

    public static boolean inRoughChina(double lat, double lng) {
        if (lat < CH_MAX_LAT && lng < CH_MAX_LNG && lat > CH_MIN_LAT && lng > CH_MIN_LNG)
            return true;
        return false;
    }
    
    public static boolean inChina(GeoPoint p) {
    	return CHINA_BOUND.contains(p);
    }
    
    public static boolean inChinaMainLand(GeoPoint p) {
    	return inChina(p) && !HONGKONG_BOUND.contains(p) && !MACAU_BOUND.contains(p);
    }
    
    public static boolean isInRect(GeoPoint point, GeoPoint low, GeoPoint hi) {
        if (point.getLat() <= hi.getLat() && point.getLat() >= low.getLat()
        		&& point.getLng() <= hi.getLng() && point.getLng() >= low.getLng()) {
            return true;
        }
        return false;
    }
    
    public static GeoBoundWithCity selectBound(Collection<GeoBoundWithCity> bounds, GeoPoint p) {
    	GeoBoundWithCity selectedBound = null;
	    for (GeoBoundWithCity bound : bounds) {
			boolean isIn = bound.contains(p);
			if (isIn && selectedBound == null) {
				selectedBound = bound;
			} else if (isIn && selectedBound != null
					&& selectedBound.containInRoughBound(bound.getMinLat(), bound.getMinLng())
					&& selectedBound.containInRoughBound(bound.getMaxLat(), bound.getMaxLng())
					&& selectedBound.contains(bound.getCentroid())) {
				selectedBound = bound;
			}
		}
	    return selectedBound;
    }
}
