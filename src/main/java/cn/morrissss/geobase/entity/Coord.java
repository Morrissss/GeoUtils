package cn.morrissss.geobase.entity;

public class Coord {

    private CoordType type;	// 坐标类型
    private CoordSource source;	// 定位源
    private GeoPoint pos;	// 定位坐标
    private int acc;	// 定位精度
    private long elapse;	// 耗时

    public Coord(CoordType type, CoordSource source, GeoPoint pos, int acc, long elapse) {
        this.type = type;
        this.source = source;
        this.pos = pos;
        this.acc = acc;
        this.elapse = elapse;
    }
    
    public CoordType getType() { return type; }

    public void setType(CoordType type) { this.type = type; }

    public CoordSource getSource() { return source; }

    public void setSource(CoordSource source) { this.source = source; }

    public GeoPoint getPos() { return pos; }

    public void setPos(GeoPoint pos) { this.pos = pos; }

    public int getAcc() { return acc; }

    public void setAcc(int acc) { this.acc = acc; }

    public long getElapse() { return elapse; }

    public void setElapse(long elapse) { this.elapse = elapse; }
    
    @Override
    public Coord clone() {
    	return new Coord(type, source, pos.clone(), acc, elapse);
    }

    @Override
    public String toString() {
        return String.format("%s@%s,%s", pos, acc, elapse);
    }

    public String toDPString() {
        return String.format("%s,%s,%s,%s", pos, acc, type, source);
    }

    public enum CoordType {
        GPS, // 原始GPS坐标系
        GMAP, // Google地图坐标系
        AMAP, // 高德地图坐标系
        BMAP, // 百度地图坐标系
        MAPBAR // 图吧地图坐标系
    }

    public enum CoordSource {
        GPS, // 手机GPS芯片定位源
        NETWORK, // android手机定位API提供位置
        AWIFI, // 高德wifi定位
        BWIFI, // 百度wifi定位
        DCELL, // 点评新产出基站定位数据
        DWIFI, // 点评新产出wifi定位数据
        CELL2, // 点评上一个版本基站定位数据
        WIFI2, // 点评上一个版本wifi定位数据
        ANETWORK, //高德综合定位
        TENCENT,	// 腾讯定位
        DP          //点评综合定位
    }
}
