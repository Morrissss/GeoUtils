package cn.morrissss.geobase.utils;

import cn.morrissss.geobase.entity.GeoPoint;
import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class ConvertTest {

	@Ignore
	@Test
	public void convert() throws Exception {
		BufferedReader br = new BufferedReader(new FileReader("E:\\parsedlog"));
		BufferedWriter bw = new BufferedWriter(new FileWriter("E:\\convertedlog"));
		String line = "";
		while ((line = br.readLine()) != null) {
			String[] entries = line.split("\t");
			double lat = Double.parseDouble(entries[0]);
			double lng = Double.parseDouble(entries[1]);
			GeoPoint pos = OffsetUtils.wgs84ToGcj02(new GeoPoint(lat, lng));
			bw.write(pos.getLat() + "\t" + pos.getLng());
			for (int i=2; i<entries.length; i++) {
				String[] poi = entries[i].split(",");
				lat = Double.parseDouble(poi[0]);
				lng = Double.parseDouble(poi[1]);
				pos = OffsetUtils.gcj02ToWgs84(new GeoPoint(lat, lng));
				bw.write(String.format("\t%.5f,%.5f", pos.getLat(), pos.getLng()));
			}
			bw.write("\n");
		}
		br.close();
		bw.close();
	}
}
