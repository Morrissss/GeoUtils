package cn.morrissss.geobase.entity;

import java.util.ArrayList;
import java.util.List;

public class CityBounds {
	
	public static CityBounds getRelatedBounds(List<GeoBoundWithCity> sources, int cityId) {
		List<GeoBound> bounds = new ArrayList<GeoBound>();
		for (GeoBoundWithCity b : sources) {
			if (b.getCityId() == cityId)
				bounds.add(b);
		}
		List<GeoBound> relatedBounds = new ArrayList<GeoBound>();
		for (GeoBoundWithCity targetBound : sources) {
			if (targetBound.getCityId() != cityId) {
				for (GeoBound bound : bounds) {
					if (bound.contains(targetBound.getCentroid()) && 
									bound.containInRoughBound(targetBound.getMinLat(), 
											  				  targetBound.getMinLng()) && 
					  				bound.containInRoughBound(targetBound.getMaxLat(), 
										  					  targetBound.getMaxLng()))
						relatedBounds.add(targetBound);
				}
			}
		}
		bounds.addAll(relatedBounds);
		return new CityBounds(cityId, bounds);
	}
	
	private int cityId;
	private List<GeoBound> fullBounds;
	
	public int getCityId() { return cityId; }
	
	public List<GeoBound> getFullBounds() { return fullBounds; }
	
	public CityBounds(int cityId, List<GeoBound> fullBounds) {
		this.cityId = cityId;
		this.fullBounds = fullBounds;
	}
	
	public boolean contains(double lat, double lng) {
		int containNum = 0;
		for (GeoBound bound : fullBounds) {
			if (bound.contains(lat, lng))
				containNum++;
		}
		return (containNum % 2) == 1;
	}
	
	public boolean contains(GeoPoint point) {
		return contains(point.getLat(), point.getLng());
	}
	
	public GeoPoint getCentroid() {
		if (fullBounds==null || fullBounds.size()<1)
			return null;
		GeoBound maxBound = null;
		double maxArea = 0;
		for (GeoBound b : fullBounds) {
			double area = b.getArea();
			if (area > maxArea) {
				maxArea = area;
				maxBound = b;
			}
		}
		return maxBound.getCentroid();
	}
	
	public double getRadius(GeoPoint p) {
		if (p==null || !p.legal())
			return -1;
		if (!contains(p))
			return 0;
		double r = Double.MAX_VALUE;
		for (GeoBound bound : fullBounds) {
			double minDis = bound.getRadius(p);
			if (minDis < r)
				r = minDis;
		}
		return r;
	}
}
