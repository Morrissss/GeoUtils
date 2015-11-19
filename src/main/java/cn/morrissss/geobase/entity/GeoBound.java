package cn.morrissss.geobase.entity;

import java.util.ArrayList;
import java.util.List;

public class GeoBound {
	
	/*
	 * 输入经度在前纬度在后
	 */
    public static List<GeoPoint> parseBound(String str) {
    	if (str == null)
    		return null;
    	String[] coords = str.split(";");
    	List<GeoPoint> bound = new ArrayList<GeoPoint>(coords.length);
    	for (String coordStr : coords) {
    		String[] lngLat = coordStr.split(",");
    		double lng = Double.parseDouble(lngLat[0]);
    		double lat = Double.parseDouble(lngLat[1]);
    		bound.add(new GeoPoint(lat, lng));
    	}
    	return bound;
    }

    protected double maxLat, maxLng;
    protected double minLat, minLng;
    protected List<GeoPoint> bound;

    public GeoBound(List<GeoPoint> bound) {
        init(bound);
    }

    private void init(List<GeoPoint> bound) {
        maxLat = maxLng = Double.NEGATIVE_INFINITY;
        minLat = minLng = Double.POSITIVE_INFINITY;
        this.bound = bound;
        for (GeoPoint point : bound) {
            maxLat = point.getLat() > maxLat ? point.getLat() : maxLat;
            maxLng = point.getLng() > maxLng ? point.getLng() : maxLng;
            minLat = point.getLat() < minLat ? point.getLat() : minLat;
            minLng = point.getLng() < minLng ? point.getLng() : minLng;
        }
    }

    public List<GeoPoint> getBound() { return bound; }
    public void setBound(List<GeoPoint> bound) { this.bound = bound; }
    public double getMinLat() { return minLat; }
    public double getMaxLat() { return maxLat; }
    public double getMinLng() { return minLng; }
    public double getMaxLng() { return maxLng; }
    
    public boolean containInRoughBound(GeoPoint p) {
    	return containInRoughBound(p.getLat(), p.getLng());
    }
    
    public boolean containInRoughBound(double lat, double lng) {
        return (lat <= maxLat && lat >= minLat && lng <= maxLng && lng >= minLng);
    }

    public boolean contains(double lat, double lng) {
        if (bound == null || bound.size() < 3) {
            return false;
        } else if (!containInRoughBound(lat, lng)) {
            return false;
        } else {
            int cnt = 0;
            int size = bound.size();
            for (int i = 0; i < size; i++) {
                int j = (i + 1) % size;
                double lat1 = bound.get(i).getLat();
                double lng1 = bound.get(i).getLng();
                double lat2 = bound.get(j).getLat();
                double lng2 = bound.get(j).getLng();
                if ((((lng >= lng1) && (lng < lng2)) || ((lng >= lng2) && (lng < lng1)))
                        && (lng1 != lng2)
                        && (lat < (lat2 - lat1) * (lng - lng1) / (lng2 - lng1) + lat1))
                    cnt++;
            }
            return (cnt % 2 > 0) ? true : false;
        }
    }

    public boolean contains(GeoPoint point) {
    	if (point==null || !point.legal())
    		return false;
        return contains(point.getLat(), point.getLng());
    }
    
    public double getRadius(GeoPoint p) {
		if (p==null || !p.legal())
			return -1;
		double r = Double.MAX_VALUE;
		List<GeoPoint> bPoints = bound;
		for (int i=0; i<bPoints.size(); i++) {
			GeoPoint p1 = bPoints.get(i % bPoints.size());
			GeoPoint p2 = bPoints.get((i+1) % bPoints.size());
			double lat1 = p1.getLat();
			double lng1 = p1.getLng();
			double lat2 = p2.getLat();
			double lng2 = p2.getLng();
			// line function
			double a = lng1 - lng2;
			double b = lat2 - lat1;
			if (Math.abs(a) < 1e-6 && Math.abs(b) < 1e-6)
				continue;
			double c = lat1 * lng2 - lat2 * lng1;
			// test if the cut point is on the segment
			double lat = p.getLat();
			double lng = p.getLng();
			double lineDis = Math.abs(a * lat + b * lng + c) / Math.sqrt(a * a + b * b);
			double dot1 = (lat1-lat)*(lat1-lat2) + (lng1-lng)*(lng1-lng2);
			double dot2 = (lat2-lat)*(lat1-lat2) + (lng2-lng)*(lng1-lng2);
			boolean patalInBetween = ((dot1*dot2) < 0);
			// get min distance
			double disP1 = Math.sqrt((lat1 - lat) * (lat1 - lat) + (lng1 - lng) * (lng1 - lng));
			double disP2 = Math.sqrt((lat2 - lat) * (lat2 - lat) + (lng2 - lng) * (lng2 - lng));
			double segMinDis = Math.min(disP1, disP2);
			if (patalInBetween)
				segMinDis = Math.min(segMinDis, lineDis);
			if (segMinDis < r)
				r = segMinDis;
		}
		return r;
	}
    
	public GeoPoint getCentroid() {
		if (bound==null || bound.size() < 3)
			return null;
		double lat0 = bound.get(0).getLat();
		double lng0 = bound.get(0).getLng();
		double weightedLatSum = 0;
		double weightedLngSum = 0;
		double weightSum = 0;
		for (int i=1; i<bound.size()-1; i++) {
			double lat1 = bound.get(i).getLat();
			double lng1 = bound.get(i).getLng();
			double lat2 = bound.get(i+1).getLat();
			double lng2 = bound.get(i+1).getLng();
			double signedArea = ((lat1-lat0)*(lng2-lng0) - (lat2-lat0)*(lng1-lng0))/2;
			double latTri = (lat0 + lat1 + lat2) / 3;
			double lngTri = (lng0 + lng1 + lng2) / 3;
			weightedLatSum += latTri * signedArea;
			weightedLngSum += lngTri * signedArea;
			weightSum += signedArea;
		}
		return new GeoPoint(weightedLatSum/weightSum, weightedLngSum/weightSum);
	}
    
	public double getArea() {
		if (bound==null || bound.size() < 3)
			return Double.MIN_VALUE;
		double area = 0;
		double lat0 = bound.get(0).getLat();
		double lng0 = bound.get(0).getLng();
		for (int i=1; i<bound.size()-1; i++) {
			double lat1 = bound.get(i).getLat();
			double lng1 = bound.get(i).getLng();
			double lat2 = bound.get(i+1).getLat();
			double lng2 = bound.get(i+1).getLng();
			area += ((lat1-lat0)*(lng2-lng0) - (lat2-lat0)*(lng1-lng0))/2;
		}
		return Math.abs(area);
	}
}
