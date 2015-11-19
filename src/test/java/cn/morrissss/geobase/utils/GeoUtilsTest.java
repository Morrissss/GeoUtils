package cn.morrissss.geobase.utils;

import cn.morrissss.geobase.entity.GeoPoint;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class GeoUtilsTest {

	@Test
	public void chinaBoundTest() {
		GeoPoint p = new GeoPoint(22.27512, 114.18256);
		assertTrue(GeoUtils.inChina(p));

		p = new GeoPoint(22.27512, 114.18256);
		assertTrue(!GeoUtils.inChinaMainLand(p));
	}
}
