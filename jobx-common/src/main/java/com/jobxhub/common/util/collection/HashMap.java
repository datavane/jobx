package com.jobxhub.common.util.collection;

import com.jobxhub.common.util.CommonUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HashMap<K,V> extends ConcurrentHashMap<K,V> {

    public HashMap(){
        super();
    }

    public HashMap(int initialCapacity){
        super(initialCapacity);
    }

    public HashMap(Map<K,V> map) {
        super(map);
    }

    @Override
    public V put(K key, V value) {
        if (CommonUtils.notEmpty(key,value)) {
            return super.put(key, value);
        }
        return value;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        if (m!=null) {
            super.putAll(m);
        }
    }

    @Override
    public V putIfAbsent(K key, V value) {
        if (CommonUtils.notEmpty(key,value)) {
            return super.putIfAbsent(key, value);
        }
        return value;
    }

    @Override
    public boolean remove(Object key, Object value) {
        if (CommonUtils.notEmpty(key,value)) {
            return super.remove(key, value);
        }
        return false;
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        if (CommonUtils.notEmpty(key,oldValue,newValue)) {
            return super.replace(key, oldValue,newValue);
        }
        return false;
    }

    @Override
    public V replace(K key, V value) {
        if (CommonUtils.notEmpty(key,value)) {
            return super.replace(key, value);
        }
        return value;
    }

    public String getString(K key) {
        Object val = get(key);
        if (val!=null) {
            return val.toString();
        }
        return null;
    }

    public Integer getInt(K key) {
        String val = getString(key);
        if (val!=null) {
            return Integer.parseInt(val);
        }
        return null;
    }

    public Long getLong(K key) {
        String val = getString(key);
        if (val!=null) {
            return Long.parseLong(val);
        }
        return null;
    }

}
