package cn.morrissss.geobase.entity;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

public class WifiList implements Iterable<Wifi> {
    
	protected Wifi[] wifis;
	
	public WifiList(Collection<Wifi> input) {
		TreeSet<Wifi> tmp = new TreeSet<Wifi>();
		Iterator<Wifi> iter = input.iterator();
		while (iter.hasNext()) {
			Wifi wf = iter.next();
			if (wf.legal())
				tmp.add(wf);
		}
		wifis = new Wifi[tmp.size()];
		tmp.toArray(wifis);
	}
	
	public WifiList(WifiList input) {
		wifis = new Wifi[input.size()];
		for (int i=0; i<wifis.length; i++)
			wifis[i] = new Wifi(input.wifis[i]);
	}
	
	public int size() {
		return wifis.length;
	}
	
	@Override
	public String toString(){
		StringBuilder strb = new StringBuilder();
		for(int i=0; i<wifis.length; i++)
			strb.append(wifis[i]+"|");
		strb.deleteCharAt(strb.length()-1);
		return new String(strb);
	}

	@Override
	public Iterator<Wifi> iterator() {
		return new Iterator<Wifi>(){
			private int idx = 0;
			public boolean hasNext() { return idx < wifis.length; }
			public Wifi next() { return wifis[idx++]; }
			public void remove() { throw new UnsupportedOperationException(); }
		};
	}
}
