package cn.morrissss.geobase.utils;

import cn.morrissss.geobase.entity.CdmaCell;
import cn.morrissss.geobase.entity.Cell;
import cn.morrissss.geobase.entity.Coord;
import cn.morrissss.geobase.entity.Coord.CoordSource;
import cn.morrissss.geobase.entity.Coord.CoordType;
import cn.morrissss.geobase.entity.GeoPoint;
import cn.morrissss.geobase.entity.GsmCell;
import cn.morrissss.geobase.entity.LteCell;
import cn.morrissss.geobase.entity.WcdmaCell;
import cn.morrissss.geobase.entity.Wifi;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogUtils {

    public static Properties logFormat(String strLog) {
    	Pattern logPattern = Pattern.compile(
                "^\\[INFO\\]\\[([ :0-9-]+)\\]\\[locate-log\\]-\\[(.*)\\]$");
        Matcher matcher = logPattern.matcher(strLog);
        
        Properties formatLog = new Properties();
        if (!matcher.find())
        	return formatLog;
        try {
            // add request time
            formatLog.setProperty("time", matcher.group(1));
            String context = matcher.group(2);
            // add wifi
        	Pattern wifiPattern = Pattern.compile(
                    "&wifi=(.*,(?:[0-9a-fA-F]{2}:){5}[0-9a-fA-F]{2},-?\\d+)&");
        	matcher = wifiPattern.matcher(context);
        	int endBeforeWifi = context.length();
        	int begAfterWifi = endBeforeWifi;
        	if (matcher.find()) {
	        	endBeforeWifi = matcher.start();
	        	begAfterWifi = matcher.end();
	        	formatLog.setProperty("wifi", matcher.group(1));
        	}
        	else {
        		formatLog.setProperty("wifi", "null");
        	}
        	// before wifi
        	String[] parts = context.substring(0, endBeforeWifi).split("&");
        	for (String part : parts) {
        		int spIdx = part.indexOf("=");
        		if (spIdx > 0)
        			formatLog.setProperty(part.substring(0, spIdx), part.substring(spIdx+1));
        	}
        	// after wifi
        	parts = context.substring(begAfterWifi).split("&");
        	for (String part : parts) {
        		int spIdx = part.indexOf("=");
        		if (spIdx > 0)
        			formatLog.setProperty(part.substring(0, spIdx), part.substring(spIdx+1));
        	}
        } catch (Exception e) {
        	return new Properties();
        }
        return formatLog;
    }

    public static List<Cell> parseCells(String cellType, String cellStr) {
    	if (cellType.toLowerCase().equals("gsm"))
    		return GsmCell.parseCells(cellStr);
    	else if (cellType.toLowerCase().equals("cdma"))
    		return CdmaCell.parseCells(cellStr);
    	else if (cellType.toLowerCase().equals("wcdma"))
    		return WcdmaCell.parseCells(cellStr);
    	else if (cellType.toLowerCase().equals("lte"))
    		return LteCell.parseCells(cellStr);
    	else
    		return new ArrayList<Cell>();
    }

    public static List<Wifi> parseWifis(String wifiStr) {
        List<Wifi> wifis = new ArrayList<Wifi>();
        Pattern pattern = Pattern.compile(
                "(?i)(?:\\|?)(.*?),((?:[0-9a-f]{2}:){5}[0-9a-f]{2},-?\\d+|null,-200)");
        Matcher matcher = pattern.matcher(wifiStr);
        try {
        	while (matcher.find()) {
        		String ssid = matcher.group(1);
        		String macRssi = matcher.group(2);
        		String[] parts = macRssi.split(",");
        		StringBuffer sb = new StringBuffer();
        		for (char c : ssid.toCharArray()) {
        			if (c > 31)
        				sb.append(c);
        		}
        		ssid = sb.toString();
        		if (parts.length != 2)
        			continue;
        		String mac = parts[0];
        		if (mac.equals("null"))
        			continue;
        		int rssi = Integer.parseInt(parts[1]);
        		wifis.add(new Wifi(ssid, mac, rssi, false));
        	}
        	if (wifis.size() > 0 && wifiStr.charAt(0) != '|')
        		wifis.get(0).setConnected(true);
        } catch (Exception e) {
        	return new ArrayList<Wifi>();
        }
        return wifis;
    }
    
    public static Coord parseCoord(String sourceStr, String coordStr) {
        if (StringUtils.isBlank(coordStr) || !sourceStr.startsWith("coord_"))
            return null;
        try {
            StringTokenizer st = new StringTokenizer(coordStr, ",@");
            if (st.countTokens() >= 4) {
                double lat = Double.parseDouble(st.nextToken());
                double lng = Double.parseDouble(st.nextToken());
                GeoPoint pos = new GeoPoint(lat, lng);
                int acc = Integer.parseInt(st.nextToken());
                long elapse = Long.parseLong(st.nextToken());
                sourceStr = sourceStr.substring(6);
                CoordSource source = CoordSource.valueOf(sourceStr.toUpperCase());
                return new Coord(CoordType.GPS, source, pos, acc, elapse);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
    
    public static Coord getSourceCoord(Properties logMap) {
    	if (!logMap.containsKey("source"))
    		return null;
    	String source = "coord_"+logMap.getProperty("source");
    	if (!logMap.containsKey(source))
    		return null;
    	return parseCoord(source, logMap.getProperty(source));
    }
    
    public static long getTimeStampInSecond(String timeStr) {
    	SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	try {
			return timeFormat.parse(timeStr).getTime() / 1000;
		} catch (ParseException e) {
			e.printStackTrace();
			return -1;
		}
    }
    
    public static void main(String[] args) throws Exception {
    	BufferedReader br = new BufferedReader(new FileReader(args[0]));
    	BufferedWriter bw = new BufferedWriter(new FileWriter(args[1]));
    	String line = "";
    	Properties logMap = null;
    	while ((line = br.readLine()) != null) {
			logMap = LogUtils.logFormat(line);
			if (!logMap.containsKey("wifi"))
				continue;
			List<Wifi> wifis = LogUtils.parseWifis(logMap.getProperty("wifi"));
			if (wifis.size() > 0) {
				StringBuffer sb = new StringBuffer();
				for (int i=0; i<wifis.size(); i++) {
					String mac = wifis.get(i).getMac();
					int rssi = wifis.get(i).getRssi();
					boolean conn = wifis.get(i).isConnected();
					sb.append(mac+"\t"+rssi+"\t"+conn+"\t");
				}
				if (sb.length() > 0) {
					sb.deleteCharAt(sb.length()-1);
					bw.write(sb+"\n");
				}
			}
    	}
    	br.close();
    	bw.close();
    }
}
