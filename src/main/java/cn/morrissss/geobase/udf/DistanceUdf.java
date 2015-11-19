package cn.morrissss.geobase.udf;

import cn.morrissss.geobase.entity.GeoPoint;
import cn.morrissss.geobase.utils.GeoUtils;
import cn.morrissss.geobase.utils.OffsetUtils;
import org.apache.hadoop.hive.ql.exec.UDF;

public class DistanceUdf extends UDF {

	/*
	 * 0: Wgs84, 1: Google, 2: Baidu, 3: Mapbar
	 */
	public Double evaluate(final Double lat1, final Double lng1, final Integer type1,
						   final Double lat2, final Double lng2, final Integer type2) {
		if (lat1 == null || lng1 == null || type1 == null || lat2 == null || lng2 == null) {
			return null;
		}
		GeoPoint p1 = new GeoPoint(lat1, lng1);
		GeoPoint p2 = new GeoPoint(lat2, lng2);
		if (!p1.legal() || !p2.legal()) {
			return null;
		}
		
		GeoPoint wgs1 = convertToWgs84(p1, type1);
		GeoPoint wgs2 = convertToWgs84(p2, type2);
		return GeoUtils.distanceInMeter(wgs1, wgs2);
	}
	
	private GeoPoint convertToWgs84(GeoPoint origin, int type) {
		switch (type) {
		case 0: return origin;
		case 1: return OffsetUtils.googleToWgs84(origin);
		case 2: return OffsetUtils.baiduToWgs84(origin);
		case 3: return OffsetUtils.mapbarToWgs84(origin);
		default: return origin;
		}
	}
}
