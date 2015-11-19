package cn.morrissss.geobase.utils;

import cn.morrissss.geobase.entity.Wifi;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LogUtilsTest {
	
	@Test
	public void logParseTest() {
		// 字段转为map
    	Properties m = LogUtils.logFormat("[INFO][2014-03-30 00:00:00][locate-log]-[did=357066052886629&client=Android_api&version=6.3&uid=0&ua=MApi 1.1 (com.dianping.v1 6.3 om_sd_360sz GT-I9128I; Android 4.2.2)&session=d20cbb62-86cf-45d6-bf5f-bd50ab37a29c&impl=15&action=loc&elapse=null&debug=null&gsm=460,0:67382370,49412,0&cdma=null&wifi=null&coord_cell=null&coord_wifi=null&coord_acell=null&coord_awifi=null&coord_bcell=null&coord_bwifi=null&coord_network=null&coord_gps=null&coord_cell2=41.82183,123.41452@511,0&coord_wifi2=null&coord_anetwork=41.826326,123.412934@1375,0&source=cell2&cityId=18&extra=null&phone=null&ip=117.136.5.126&uuid=e53eb098-37a2-4692-80b5-cbbd7e51f81d&cellexp=0&wifiexp=0]");
    	assertEquals(LogUtils.parseCoord("coord_anetwork",m.getProperty("coord_anetwork")).toDPString(), 
    				 "41.826326,123.412934,1375,GPS,ANETWORK");
    	assertEquals(LogUtils.parseCells("gsm", m.getProperty("gsm")).toString(), 
				 	 "[GSM,460,0,49412,67382370]");
    	// 时间
    	assertEquals(m.get("time"), "2014-03-30 00:00:00");
    	// device id
    	assertEquals(m.get("did"), "357066052886629");
    	// 定位坐标
    	assertEquals(LogUtils.getSourceCoord(m).toDPString(), "41.82183,123.41452,511,GPS,CELL2");
	}
	
	@Test
	public void wifiParseTest() {
		Properties m = LogUtils.logFormat("[INFO][2014-09-23 00:00:41][locate-log]-[did=99000535925064&client=Android_api&version=6.8.1&uid=57071550&ua=MApi 1.1 (com.dianping.v1 6.8.1 om_sd_xiaomishichang MI_3C; Android 4.4.2)&session=9929e32a-1f6d-4f11-9ba6-e6fa40f868a6&impl=281&action=loc&elapse=1411401640907&localLocElapse=1004&assistLocElapse=null&cellScanElapse=90&wifiScanElapse=56&debug=0&gsm=null&cdma=460,13884:4673,37,0,0|4673,37,0,0|4673,37,0,0|4673,37,0,0|4673,37,0,0|4673,37,0,0&wifi=|\"louxiashabi\",a8:57:4e:95:34:1e,-39|xiao=huoban,9c:21:6a:ac:b3:6c,-50|MERCURY&C49A,e4:d3:32:d3:c4:9a,-63|你看到我的小熊了吗,94:39:e5:03:23:a4,-81|xxx,null,-200|sfd@dwrt,10:bf:48:e7:12:31,-85&lte=null&wcdma=null&coord_cell=null&coord_wifi=null&coord_acell=null&coord_awifi=null&coord_bcell=null&coord_bwifi=30.5809,114.36676@65,1411401640020&coord_network=null&coord_gps=null&coord_cell2=30.58153,114.36714@500,0&coord_wifi2=30.58077,114.36665@100,0,30&coord_tencent=30.58089,114.36675@205,0&coord_anetwork=null&source=tencent&cityId=16&extra=null&phone=null&ip=183.94.244.233&uuid=0ce2b436-2b92-4eee-b86b-33833be7acf1&cellexp=0&wifiexp=0]");
    	List<Wifi> wifis = LogUtils.parseWifis(m.getProperty("wifi"));
    	Collections.sort(wifis);
    	List<Wifi> groundTruth = Arrays.asList(
                new Wifi("sfd@dwrt", "10:bf:48:e7:12:31", -85, false),
                new Wifi("你看到我的小熊了吗", "94:39:e5:03:23:a4", -81, false),
                new Wifi("xiao=huoban", "9c:21:6a:ac:b3:6c", -50, false),
                new Wifi("\"louxiashabi\"", "a8:57:4e:95:34:1e", -39, false),
                new Wifi("MERCURY&C49A", "e4:d3:32:d3:c4:9a", -63, false));
    	assertEquals(wifis.size(), groundTruth.size());
    	for (int i=0; i<groundTruth.size(); i++)
    		assertTrue(wifis.get(i).fullEquals(groundTruth.get(i)));
	}
}
