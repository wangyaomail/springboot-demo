package demo.common.util.set;

import java.util.Arrays;

@SuppressWarnings("unchecked")
public class IdentityHashMap<K, V> {
    private final Entry<K, V>[] buckets;
    private final int indexMask;
    public final static int DEFAULT_SIZE = 8192;

    public IdentityHashMap() {
        this(DEFAULT_SIZE);
    }

    public IdentityHashMap(int tableSize) {
        this.indexMask = tableSize - 1;
        this.buckets = new Entry[tableSize];
    }

    public final V get(K key) {
        final int hash = System.identityHashCode(key);
        final int bucket = hash & indexMask;
        for (Entry<K, V> entry = buckets[bucket]; entry != null; entry = entry.next) {
            if (key == entry.key) {
                return (V) entry.value;
            }
        }
        return null;
    }

    public Class<?> findClass(String keyString) {
        for (int i = 0; i < buckets.length; i++) {
            Entry<K, V> bucket = buckets[i];
            if (bucket == null) {
                continue;
            }
            for (Entry<K, V> entry = bucket; entry != null; entry = entry.next) {
                Object key = bucket.key;
                if (key instanceof Class) {
                    Class<?> clazz = ((Class<?>) key);
                    String className = clazz.getName();
                    if (className.equals(keyString)) {
                        return clazz;
                    }
                }
            }
        }
        return null;
    }

    public boolean put(K key,
                       V value) {
        final int hash = System.identityHashCode(key);
        final int bucket = hash & indexMask;
        for (Entry<K, V> entry = buckets[bucket]; entry != null; entry = entry.next) {
            if (key == entry.key) {
                entry.value = value;
                return true;
            }
        }
        Entry<K, V> entry = new Entry<K, V>(key, value, hash, buckets[bucket]);
        buckets[bucket] = entry; // 并发是处理时会可能导致缓存丢失，但不影响正确性
        return false;
    }

    protected static final class Entry<K, V> {
        public final int hashCode;
        public final K key;
        public V value;
        public final Entry<K, V> next;

        public Entry(K key,
                     V value,
                     int hash,
                     Entry<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
            this.hashCode = hash;
        }
    }

    public void clear() {
        Arrays.fill(this.buckets, null);
    }
}
