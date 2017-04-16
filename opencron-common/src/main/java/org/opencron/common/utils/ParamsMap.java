package org.opencron.common.utils;

import java.util.*;

/**
 * @Package: cn.damai.usercenter.common.util
 * @Description: 专门封装参数的Map,HashMap的增强
 * @author: Wanghuajie
 * @date: 13-3-6 - 上午11:17
 * @version: V1.0
 * @company: damai
 */
@SuppressWarnings({"serial","rawtypes", "unchecked"})
public class ParamsMap<K,V> extends HashMap<K,V> implements Map<K,V> {

    private Map<K,V> map = new HashMap<K,V>(0);

    public static ParamsMap instance(){
        return new ParamsMap();
    }

    public ParamsMap(Object...objects){
        this.put(objects);
    }

	public ParamsMap put(Object...objects){
        if (objects==null) return this;
        List<Object> argsSkipMap = new ArrayList<Object>(0);
        int argsCount = 0;
        for (int i=0;i<objects.length;i++) {
            if (objects[i] instanceof Map) {
                if ((argsCount&1) == 1) {
                    throw new IllegalArgumentException("arguments error,index of " +i+1+ " key can't be Map");
                }
                this.put((Map)objects[i]);
            } else {
                argsSkipMap.add(objects[i]);
                ++argsCount;
            }
        }
        //不是成对的参数
        if ((argsSkipMap.size()&1)==1){
            throw new RuntimeException("arguments error.. The number of arguments to remove the Map type arguments must be an even number!");
        }

        for (int j=0;j<argsSkipMap.size()/2;j++){
            K k = (K)argsSkipMap.get(j * 2);
            V v = (V)argsSkipMap.get(j * 2 + 1);
            this.map.put(k,v);
        }
        return this;
    }

    public ParamsMap put(Map<K,V> argsMap){
        for(Map.Entry entry:argsMap.entrySet()){
            this.put(entry.getKey(),entry.getValue());
        }
        return this;
    }

    public ParamsMap remove(int index){
        int tempIndex = 1;
        for (Map.Entry entry : map.entrySet()) {
            if (tempIndex == index) {
                this.remove(entry.getKey());
                break;
            }
            ++tempIndex;
        }
        return this;
    }

    @Override
    public int size() {
        return this.map.size();
    }

    public boolean isEmpty(){
        return this.map==null||this.map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.map.containsKey(key);
    }

    public boolean notEmpty(){
        return !this.isEmpty();
    }

    public ParamsMap putBefore(K key, V val,int insertIndex,Comparator<K> comparator) {
        //1,2,3
        if (insertIndex<=0||insertIndex>this.map.size()){
            throw new IndexOutOfBoundsException();
        }

        Set<K> keySet = new TreeSet(comparator);
        keySet.addAll(this.map.keySet());

        Map<K,V> tempMap = new HashMap<K,V>();

        int tempIndex = 0;
        for (K k: keySet) {
            if (tempIndex == insertIndex-1) {
                tempMap.put(key, val);
            }
            tempMap.put(k,map.get(k));
            ++tempIndex;
        }
        map = tempMap;
        return this;
    }

    public ParamsMap putAfter(K key, V val,int insertIndex,Comparator<K> comparator){
        if (insertIndex<=0||insertIndex>this.map.size()){
            throw new IndexOutOfBoundsException();
        }

        Set<K> keySet = new TreeSet(comparator);
        keySet.addAll(this.map.keySet());

        Map<K,V> tempMap = new HashMap<K,V>();

        int tempIndex = 1;
        for (K k: keySet) {
            if (tempIndex == insertIndex) {
                tempMap.put(key, val);
            }

            tempMap.put(k, map.get(k));
            ++tempIndex;
        }
        map = tempMap;
        return this;
    }

    public ParamsMap putBefore(Map<K,V> tarMap,int insertIndex,Comparator<K> comparator){
        if (insertIndex<=0||insertIndex>this.map.size()){
            throw new IndexOutOfBoundsException();
        }

        Set<K> keySet = new TreeSet(comparator);
        keySet.addAll(this.map.keySet());

        Map<K,V> tempMap = new HashMap<K,V>();

        int tempIndex = 0;
        for (K k: keySet) {
            if (tempIndex == insertIndex-1) {
                for (Map.Entry<K,V> tarEntry : tarMap.entrySet()) {
                    tempMap.put(tarEntry.getKey(),tarEntry.getValue());
                }
            }

            tempMap.put(k, map.get(k));
            ++tempIndex;
        }
        map = tempMap;
        return this;
    }

    public ParamsMap putAfter(Map<K,V> tarMap,int insertIndex,Comparator<K> comparator){
        if (insertIndex<=0||insertIndex>this.map.size()){
            throw new IndexOutOfBoundsException();
        }

        Map<K,V> tempMap = new HashMap<K,V>();
        int tempIndex = 1;

        Set<K> keySet = new TreeSet(comparator);
        keySet.addAll(this.map.keySet());
        for (K k: keySet) {
            if (tempIndex == insertIndex) {
                for (Map.Entry<K,V> tarEntry : tarMap.entrySet()) {
                    tempMap.put(tarEntry.getKey(),tarEntry.getValue());
                }
            }

            tempMap.put(k, map.get(k));
            ++tempIndex;
        }
        map = tempMap;
        return this;
    }

    @Override
    public Set<Map.Entry<K,V>> entrySet() {
        return this.map.entrySet();
    }

    @Override
    public Collection values() {
        return this.map.values();
    }

    @Override
    public Set keySet() {
        return this.map.keySet();
    }

    @Override
    public boolean containsValue(Object value) {
        return this.map.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return this.map.get(key);
    }

    @Override
    public V put(K key, V value) {
        return this.map.put(key,value);
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @Override
    public V remove(Object key) {
        return this.map.remove(key);
    }

    @Override
    public void putAll(Map m) {
        this.map.putAll(m);
    }
	
	public ParamsMap fill(K key,V val){
        map.put(key,val);
        return this;
    }
	
    public Map<K, V> getMap() {
        return map;
    }
	
	public static void main(String[] args){
	    ParamsMap paramsMap = new ParamsMap();
        ParamsMap<Integer,Object> map = paramsMap.put("A","B","2","d",4,"55",new Date(),"ff");
        map.fill(1,"ddd").fill("2","fdafds").fill(444,666);
        for(Map.Entry<Integer,Object> entry:map.entrySet()){
            System.out.println(entry.getKey()+"_____"+entry.getValue()+"____"+entry.getValue().getClass());
        }
    }

}