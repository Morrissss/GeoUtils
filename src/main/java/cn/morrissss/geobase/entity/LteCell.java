package cn.morrissss.geobase.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class LteCell extends Cell {

	public static List<Cell> parseCells(String cellStr) {
        List<Cell> cells = new ArrayList<Cell>();
        try {
            StringTokenizer st = new StringTokenizer(cellStr, ",:");
            int mcc = Integer.parseInt(st.nextToken());
            int sid = Integer.parseInt(st.nextToken());
            int bid = Integer.parseInt(st.nextToken());
            int nid = Integer.parseInt(st.nextToken());
            cells.add(new LteCell(mcc, sid, nid, bid, false));
            if (cells.size() > 0 && cellStr.charAt(0) != '|')
            	cells.get(0).setConnected(true);
            return cells;
        } catch (Exception e) {
            return new ArrayList<Cell>();
        }
	}

    private int mcc;
    private int sid; // mnc
    private int nid; // lac
    private int bid; // cid

    public LteCell(int mcc, int sid, int nid, int bid, boolean connected) {
    	super(connected);
        this.mcc = mcc;
        this.sid = sid;
        this.nid = nid;
        this.bid = bid;
    }
    
    public int getMcc() { return mcc; }
    public void setMcc(int mcc) { this.mcc = mcc; }
    
    public int getSid() { return sid; }
    public void setSid(int sid) { this.sid = sid; }
    
    public int getNid() { return nid; }
    public void setNid(int nid) { this.nid = nid; }
    
    public int getBid() { return bid; }
    public void setBid(int bid) { this.bid = bid; }
    
    @Override
    public boolean valid() {
        if (bid == 65535 && nid == 0)
            return false;
        if (mcc <= 0 && sid <= 0 || bid <= 0 || nid < 0)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("CDMA,%s,%s,%s,%s", mcc, sid, nid, bid);
    }

    @Override
    public double toDPLong() {
        return 1 * 10e25 + mcc * 10e22 + sid * 10e16 + nid * 10e10 + bid;
    }
}
