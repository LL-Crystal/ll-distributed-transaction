package com.github.llcrystal.transaction.infrastructure.cache;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ThreadSafeMap<K, V> {
    private final int maxSize;
    private final ConcurrentHashMap<K, V> map;
    private final Queue<K> keysQueue;

    public ThreadSafeMap(int maxSize) {
        this.maxSize = maxSize;
        this.map = new ConcurrentHashMap<>();
        this.keysQueue = new LinkedList<>();
    }

    public synchronized void put(K key, V value) {
        if (map.size() >= maxSize) {
            K oldestKey = keysQueue.poll();
            if (oldestKey != null) {
                map.remove(oldestKey);
            }
        }
        map.put(key, value);
        keysQueue.offer(key);
    }

    public V get(K key) {
        return map.get(key);
    }

    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    public V remove(K key) {
        keysQueue.remove(key);
        return map.remove(key);
    }

    public int size() {
        return map.size();
    }

    public ConcurrentMap<K, V> getMap() {
        return this.map;
    }
}
