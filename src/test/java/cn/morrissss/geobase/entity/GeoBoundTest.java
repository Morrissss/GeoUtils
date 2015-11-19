package cn.morrissss.geobase.entity;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class GeoBoundTest {
	
	@Test
	public void getCentroidTest() {
		GeoPoint[] bounds = {new GeoPoint(0.0, 0.0), new GeoPoint(0.0, 2.0), 
							 new GeoPoint(3.0, 2.0), new GeoPoint(3.0, 0.0), 
							 new GeoPoint(2.0, 0.0), new GeoPoint(2.0, 1.0), 
							 new GeoPoint(1.0, 1.0), new GeoPoint(1.0, 0.0), 
							 new GeoPoint(0.0, 0.0)};
		GeoBound b = new GeoBound(Arrays.asList(bounds));
		GeoPoint c = b.getCentroid();
		assertEquals(c.getLat(), 1.5, 1e-6);
		assertEquals(c.getLng(), 1.1, 1e-6);
	}
}
