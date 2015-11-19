package cn.morrissss.geobase.entity;

import cn.morrissss.geobase.utils.GeoUtils;

import java.io.Serializable;
import java.util.List;

public class GeoPoint implements Serializable, Cloneable {
	
	private static final long serialVersionUID = 8144679314979809903L;
	
	public static GeoPoint getCentroid(List<GeoPoint> points) {
		if (points==null || points.size()<1)
			return null;
		double latSum = 0;
		double lngSum = 0;
		for (GeoPoint p : points) {
			double lat = p.getLat();
			double lng = p.getLng();
			latSum += lat;
			if (lng < -170)
				lng += 360;
			lngSum += lng;
		}
		double resLat = latSum / points.size();
		double resLng = lngSum / points.size();
		if (resLng > 180.0)
			resLng -= 360;
		return new GeoPoint(resLat, resLng);
	}
	
	private final double lat;
    
	private final double lng;
	
	public GeoPoint(GeoPoint p) {
		lat = p.lat;
		lng = p.lng;
	}
	
	public GeoPoint(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
	}
	
	public double getLat() { return lat; }
	
	public double getLng() { return lng; }
	
	public boolean legal() {
		return lat>=-90 && lat<=90 && lng>=-180 && lng<=180;
	}
	
	@Override
	public GeoPoint clone() {
		return new GeoPoint(lat, lng);
	}
	
	@Override
	public String toString() {
		return lat + "," + lng;
	}
	
	@Override
	public boolean equals(Object o) {
		return (o instanceof GeoPoint && ((GeoPoint) o).lat==lat && ((GeoPoint) o).lng==lng);
	}
	
	public boolean near(GeoPoint p, double accuracy) {
		return (Math.abs(p.lat - this.lat) < accuracy) && (Math.abs(p.lng - this.lng) < accuracy);
	}

	@Override
	public int hashCode() {
		return 37 * ((Double) lat).hashCode() + ((Double) lng).hashCode();
	}

	public static void main(String[] args) {
		GeoPoint p1 = new GeoPoint(31.1873, 121.454);
		GeoPoint p2 = new GeoPoint(31.1241, 121.577);
		System.out.println(GeoUtils.distanceInMeter(p1, p2));
	}
}
