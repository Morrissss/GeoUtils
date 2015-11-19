package cn.morrissss.geobase.entity;

import java.util.List;

public class GeoBoundWithCity extends GeoBound {
	
	private int cityId;
	
	public GeoBoundWithCity(int cityId, List<GeoPoint> bound) {
		super(bound);
		this.cityId = cityId;
	}
	
	public int getCityId() { return cityId; }
}
