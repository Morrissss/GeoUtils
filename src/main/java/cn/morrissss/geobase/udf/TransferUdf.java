package cn.morrissss.geobase.udf;

import cn.morrissss.geobase.entity.GeoPoint;
import cn.morrissss.geobase.utils.OffsetUtils;
import org.apache.hadoop.hive.ql.exec.UDF;

public class TransferUdf extends UDF {

	/*
	 * 0: Wgs84, 1: Google, 2: Baidu, 3: Mapbar
	 */
	public String evaluate(final Double lat, final Double lng, final Integer type) {
		if (lat == null || lng == null || type == null) {
			return null;
		}
		GeoPoint p = new GeoPoint(lat, lng);
		if (!p.legal()) {
			return null;
		}

		GeoPoint result = p;
		switch (type) {
		case 1: result = OffsetUtils.googleToWgs84(p); break;
		case 2: result = OffsetUtils.baiduToWgs84(p); break;
		case 3: result = OffsetUtils.mapbarToWgs84(p); break;
		}
		
		return result.getLat() + ":" + result.getLng();
	}
	
}
