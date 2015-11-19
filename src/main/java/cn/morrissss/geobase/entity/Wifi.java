package cn.morrissss.geobase.entity;

import java.io.Serializable;
import java.util.Comparator;

public class Wifi implements Comparable<Wifi>, Serializable {

	private static final long serialVersionUID = 3264056485687243407L;

	public static int NO_SIGNAL_RSSI = -100;
	
	public static final String MAC_REGEX = "(?i)(?:[0-9a-f]{2}:){5}[0-9a-f]{2}";
	
	public static final Comparator<Wifi> RSSI_REVERSE_ORDER =
					new Comparator<Wifi>() {
						public int compare(Wifi o1, Wifi o2) {
							return o1.rssi<o2.rssi ? 1 : (o1.rssi>o2.rssi ? -1 : (
									o1.mac<o2.mac ? -1 : (o1.mac==o2.mac ? 0 : 1)));
						}
					};
						
	public static final Comparator<Wifi> MAC_ORDER =
					new Comparator<Wifi>() {
						public int compare(Wifi o1, Wifi o2) {
							return o1.mac<o2.mac ? -1 : (o1.mac==o2.mac ? 0 : 1);
						}
					};
	
	public static String convertMacTo36(String mac) {
		String m = mac.replaceAll(":", "");
		long l = Long.parseLong(m, 16);
		return Long.toString(l, 36);
	}

	public static long convertMacToLong(String mac) {
		long result = 0;
		try{
			if (mac.matches(MAC_REGEX)) {
				String[] parts = mac.split(":");
				for (String part : parts)
					result = result*256 + Long.parseLong(part, 16);
			}
			else
				return -1;
		} catch (NumberFormatException e) {
			return -1;
		}
		return result;
	}

	public static String convertLongToMac(long mac){
		if (mac <= 0)
			return "";
		StringBuilder macTmp = new StringBuilder();
		for (int i=5; i>=0; i--) {
			long seg = mac >>> (8*i);
			macTmp.append(String.format("%02x", seg));
			macTmp.append(":");
			mac -= (seg<<(8*i));
		}
		if (mac != 0)
			return "";
		macTmp.deleteCharAt(macTmp.length()-1);
		return macTmp.toString();
	}
	
	private String ssid;
	
	private long mac;
	
	private int rssi;
	
	private boolean connected;
	
	public Wifi(String ssid, long mac, int rssi, boolean connected) {
		this.ssid = ssid;
		this.mac = mac;
		if (rssi < NO_SIGNAL_RSSI)
			this.rssi = NO_SIGNAL_RSSI;
		else
			this.rssi = rssi;
		this.connected = connected;
	}

	public Wifi(String ssid, String mac, int rssi, boolean connected){
		this.ssid = ssid;
		this.mac = convertMacToLong(mac);
		if (rssi < NO_SIGNAL_RSSI)
			this.rssi = NO_SIGNAL_RSSI;
		else
			this.rssi = rssi;
		this.connected = connected;
	}
	
	public Wifi(Wifi w){
		this.ssid = w.ssid;
		this.mac = w.mac;
		this.rssi = w.rssi;
		this.connected = w.connected;
	}
	
	public String getSsid() { return ssid; }
	
	public String getMac() { return convertLongToMac(mac); }
	
	public int getRssi() { return rssi; }
	
	public boolean isConnected() { return connected; }
	
	public void setConnected(boolean connected) { this.connected = connected; }

	public boolean legal() { return mac > 0 && mac < (1L<<48) && rssi > NO_SIGNAL_RSSI; }
	
	@Override
	public String toString(){
		return ssid+","+convertLongToMac(mac)+","+rssi;
	}
	
	public String toDPString(){
		return ssid+","+convertLongToMac(mac)+","+rssi+","+connected;
	}
	
	@Override
	public int compareTo(Wifi w){
		if (connected && !w.connected)
			return -1;
		if (!connected && w.connected)
			return 1;
		return mac<w.mac ? -1 : (mac==w.mac ? 0 : 1);
	}
	
	public boolean fullEquals(Object w) {
		if (w instanceof Wifi) {
			Wifi ww = (Wifi) w;
			return mac == ww.mac && ssid.equals(ww.ssid) && 
					rssi == ww.rssi && connected == ww.connected;
		}
		return false;
	}
	
	@Override
	public boolean equals(Object w) {
		if (w instanceof Wifi) {
			Wifi ww = (Wifi) w;
			return mac == ww.mac && ssid.equals(ww.ssid);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return ((Long) mac).hashCode() + ssid.hashCode();
	}
}
