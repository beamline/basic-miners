package beamline.miners.timeDecay;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class FifoHashMap<K, V> extends ConcurrentHashMap<K, V> {

	private static final long serialVersionUID = -879365680716056531L;
	private Queue<K> keyHistory = new LinkedList<K>();
	private int maxSize;
	
	public FifoHashMap(int maxSize) {
		this.maxSize = maxSize;
	}

	@Override
	public V put(K key, V value) {
		if (containsKey(key)) {
			return super.put(key, value);
		} else {
			if (keyHistory.size() >= maxSize) {
				K toRemove = keyHistory.poll();
				super.remove(toRemove);
			}
			super.put(key, value);
			keyHistory.add(key);
			return null;
		}
	}

	@Override
	public V remove(Object key) {
		keyHistory.remove(key);
		return super.remove(key);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for(K k : m.keySet()) {
			put(k, m.get(k));
		}
	}

	@Override
	public void clear() {
		super.clear();
		keyHistory.clear();
	}
}
