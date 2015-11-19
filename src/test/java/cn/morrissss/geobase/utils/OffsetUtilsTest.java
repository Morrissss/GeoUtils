package cn.morrissss.geobase.utils;

import cn.morrissss.geobase.entity.GeoPoint;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class OffsetUtilsTest {

	@Test
	public void baidu2GpsTest() {
		GeoPoint source = new GeoPoint(39.25, 73.56);
		GeoPoint groundTruth = new GeoPoint(39.25, 73.56);
		GeoPoint result = OffsetUtils.baiduToWgs84(source);
		assertTrue(result.near(groundTruth, 1e-5));
		
		source = new GeoPoint(31.11, 121.22);
		groundTruth = new GeoPoint(31.10578, 121.20911);
		result = OffsetUtils.baiduToWgs84(source);
		assertTrue(result.near(groundTruth, 1e-5));
		
		source = new GeoPoint(22.11, 114.11);
		groundTruth = new GeoPoint(22.10636, 114.09843);
		result = OffsetUtils.baiduToWgs84(source);
		assertTrue(result.near(groundTruth, 1e-5));
	}

	@Test
	public void gps2BaiduTest() {
		GeoPoint source = new GeoPoint(39.25, 73.56);
		GeoPoint groundTruth = new GeoPoint(39.25, 73.56);
		GeoPoint result = OffsetUtils.wgs84ToBaidu(source);
		assertTrue(result.near(groundTruth, 1e-5));
		
		source = new GeoPoint(31.11, 121.22);
		groundTruth = new GeoPoint(31.11404, 121.23092);
		result = OffsetUtils.wgs84ToBaidu(source);
		assertTrue(result.near(groundTruth, 1e-5));
		
		source = new GeoPoint(22.11, 114.11);
		groundTruth = new GeoPoint(22.11373, 114.12153);
		result = OffsetUtils.wgs84ToBaidu(source);
		assertTrue(result.near(groundTruth, 1e-5));
	}

	@Test
	public void gps2GcjTest() {
		GeoPoint source = new GeoPoint(34.25, 102.21);
		GeoPoint groundTruth = new GeoPoint(34.24846, 102.211752);
		GeoPoint result = OffsetUtils.wgs84ToGcj02(source);
		assertTrue(result.near(groundTruth, 1e-5));
		
		source = new GeoPoint(43.1, 25.9);
		groundTruth = new GeoPoint(43.1, 25.9);
		result = OffsetUtils.wgs84ToGcj02(source);
		assertTrue(result.near(groundTruth, 1e-5));
		
		source = new GeoPoint(31.22956,121.51217);
		groundTruth = new GeoPoint(31.22750, 121.51655);
		result = OffsetUtils.wgs84ToGcj02(source);
		assertTrue(result.near(groundTruth, 1e-5));
	}

	@Test
	public void gcj2GpsTest() {
		GeoPoint source = new GeoPoint(34.25, 102.21);
		GeoPoint groundTruth = new GeoPoint(34.25153, 102.20824);
		GeoPoint result = OffsetUtils.gcj02ToWgs84(source);
		assertTrue(result.near(groundTruth, 1e-5));
		
		source = new GeoPoint(43.1, 25.9);
		groundTruth = new GeoPoint(43.1, 25.9);
		result = OffsetUtils.gcj02ToWgs84(source);
		assertTrue(result.near(groundTruth, 1e-5));
		
		source = new GeoPoint(31.22749, 121.51654);
		groundTruth = new GeoPoint(31.22956,121.51217);
		result = OffsetUtils.gcj02ToWgs84(source);
		assertTrue(result.near(groundTruth, 1e-5));
	}
}
