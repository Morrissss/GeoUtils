package cn.morrissss.geobase.entity;

public abstract class Cell {
	
	protected boolean connected;
	
	public Cell(boolean connected) {
		this.connected = connected;
	}
	
	public boolean isConnected() { return connected; }
	public void setConnected(boolean connected) { this.connected = connected; }
	
	public abstract boolean valid();
	
	public abstract double toDPLong();
}
