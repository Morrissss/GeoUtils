package cn.morrissss.geobase.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class GsmCell extends Cell {
	
	public static List<Cell> parseCells(String cellStr) {
        List<Cell> cells = new ArrayList<Cell>();
        try {
            String[] strs = cellStr.split("\\|");
            int mcc = 0, mnc = 0;
            for (int i = 0; i < strs.length; ++i) {
                StringTokenizer st = new StringTokenizer(strs[i], ",:");
                if (i == 0) {
                    mcc = Integer.parseInt(st.nextToken());
                    mnc = Integer.parseInt(st.nextToken());
                }
                int cid = Integer.parseInt(st.nextToken());
                int lac = Integer.parseInt(st.nextToken());
                cells.add(new GsmCell(mcc, mnc, lac, cid, 0, false));
            }
            if (cellStr.charAt(0) != '|' && cells.size() > 0)
            	cells.get(0).setConnected(true);
            return cells;
        } catch (Exception e) {
        	return new ArrayList<Cell>();
        }
	}

    private int mcc;
    private int mnc;
    private int lac;
    private int cid;
    private int asu;
    
    public GsmCell(int mcc, int mnc, int lac, int cid, int asu, boolean connected) {
    	super(connected);
        this.mcc = mcc;
        this.mnc = mnc;
        this.lac = lac;
        this.cid = cid;
        this.asu = asu;
        this.connected = connected;
    }
    
    public int getMcc() { return mcc; }
    public void setMcc(int mcc) { this.mcc = mcc; }
    
    public int getMnc() { return mnc; }
    public void setMnc(int mnc) { this.mnc = mnc; }
    
    public int getLac() { return lac; }
    public void setLac(int lac) { this.lac = lac; }
    
    public int getCid() { return cid; }
    public void setCid(int cid) { this.cid = cid; }
    
    public int getAsu() { return asu; }
    public void setAsu(int asu) { this.asu = asu; }
    
    @Override
    public boolean valid() {
        if (cid == 65535 && lac == 0)
            return false;
        if (mcc <= 0 && mnc <= 0 || cid <= 0 || lac < 0)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("GSM,%s,%s,%s,%s", mcc, mnc, lac, cid);
    }

    @Override
    public double toDPLong() {
        return 0 * 10e25 + mcc * 10e22 + mnc * 10e16 + lac * 10e10 + cid;
    }
}
