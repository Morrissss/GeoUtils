package cn.morrissss.geobase.entity;

import org.junit.Test;

import java.util.TreeSet;

import static org.junit.Assert.assertEquals;

public class WifiListTest {

	@Test
	public void sortWifiTest() {
		TreeSet<Wifi> t1 = new TreeSet<Wifi>();
		TreeSet<Wifi> t2 = new TreeSet<Wifi>();
		t1.add(new Wifi("a", "44:33:4c:1a:ea:98", -30, false));
		t1.add(new Wifi("b", "a8:15:4d:3d:99:0c", -35, false));
		t2.add(new Wifi("c", "1c:fa:68:1d:17:14", -101, false));
		t2.add(new Wifi("a", "44:33:4c:1a:ea:98", -50, true));
		t2.add(new Wifi("d", "5c:63:bf:8f:b1:0e", -40, false));
		WifiList l1 = new WifiList(t1);
		WifiList l2 = new WifiList(t2);
		assertEquals(l1.iterator().next().getMac(), "44:33:4c:1a:ea:98");
		assertEquals(l2.iterator().next().getMac(), "44:33:4c:1a:ea:98");
	}
}
