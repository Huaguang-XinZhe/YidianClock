package com.example.yidianClock;

import androidx.annotation.Nullable;

import java.util.HashMap;

public class MyHashMap<K, V> extends HashMap<K, V> {

    @Nullable
    @Override
    public V put(K key, V value) {
        if (containsKey(key)) {
            return get(key);
        } else {
            return super.put(key, value);
        }
    }
}
