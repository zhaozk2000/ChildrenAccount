package com.zzk.childrenbank.data;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * JsonObject提供将对象转化为Json表示的字符串的方法。
 * JsonObject维护一个“键-值”对列表，根据该键值对列表生成Json表示的字符串。
 * 键值对的值可以是基本数据类型、String、JsonObject、JsonArray、其他任意的对象
 * @author Zhao Zhikun
 *
 */
public class JsonObject {
	static class Entry {
		String key;
		Object value;
		public Entry(String key, Object value){
			this.key = key;
			this.value = value;
		}
	}

	List<Entry> entryList = new LinkedList<Entry>();

	/**
	 * 清空键值对列表
	 */
	public void clear() {
		entryList.clear();
	}

	/**
	 * 添加一个键值对，不检查是否有重复的键
	 * @param key
	 * @param value	值可以是基本数据类型、String、JsonObject、JsonArray、其他任意的对象
	 */
	public void put(String key, Object value){
		entryList.add(new Entry(key, value));
	}

	/**
	 * 设置一个键值对，如果列表中已有该键，则直接设置其值；如果列表中没有该键，则创建一个新的键值对加入。
	 * @param key
	 * @param value	值可以是基本数据类型、String、JsonObject、JsonArray、其他任意的对象
	 */
	public void set(String key, Object value) {
		Entry entry = findEntry(key);
		if(entry==null) {
			entryList.add(new Entry(key, value));
		} else {
			entry.value = value;
		}
	}

	/**
	 * 查找是否有一个键
	 * @param key
	 * @return 如果有，则返回该键值对；否则返回null
	 */
	public Entry findEntry(String key){
		Iterator<Entry> iter = entryList.iterator();
		while(iter.hasNext()) {
			Entry entry = iter.next();
			if(entry.key.equals(key)) return entry;
		}
		return null;
	}

	/**
	 * 生成Json格式的字符串
	 * @return
	 */
	public String toJsonString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("{");
		boolean isFirst = true;
		Iterator<Entry> iter = entryList.iterator();
		while(iter.hasNext()){
			Entry entry = iter.next();
			if(!isFirst) {
				buffer.append(", ");
			}
			buffer.append("\"");
			buffer.append(entry.key);
			buffer.append("\"");
			buffer.append(":");
			Object value = entry.value;
			if(value==null) {
				buffer.append("null");
			} else {
				if(value instanceof String) {
					buffer.append("\"");
					buffer.append(value.toString());
					buffer.append("\"");
				} else if(value instanceof JsonObject){
					buffer.append(((JsonObject) value).toJsonString());
				} else if(value instanceof JsonArray) {
					buffer.append(((JsonArray) value).toJsonString());
				} else {
					buffer.append(value.toString());
				}
			}
			isFirst = false;
		}
		buffer.append("}");
		return buffer.toString();
	}

	/**
	 * 从键值对Map中取出一个int值
	 * @param msgMap
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static int getInt(Map<String, Object> msgMap, String key, int defaultValue){
		Integer valueObj = (Integer)msgMap.get(key);
		if(valueObj!=null) return valueObj.intValue();
		else return defaultValue;
	}

	public static long getLong(Map<String, Object> msgMap, String key, long defaultValue){
		Long valueObj = (Long)msgMap.get(key);
		if(valueObj!=null) return valueObj.longValue();
		else return defaultValue;
	}

	public static byte getByte(Map<String, Object> msgMap, String key, int defaultValue){
		Integer valueObj = (Integer)msgMap.get(key);
		if(valueObj!=null) return valueObj.byteValue();
		else return (byte)defaultValue;
	}

	public static float getFloat(Map<String, Object> msgMap, String key, float defaultValue){
		Double valueObj = (Double)msgMap.get(key);
		if(valueObj!=null) return valueObj.floatValue();
		else return defaultValue;
	}

	/**
	 * 从键值对Map中取出一个boolean值
	 * @param msgMap
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static boolean getBoolean(Map<String, Object> msgMap, String key, boolean defaultValue){
		Boolean valueObj = (Boolean)msgMap.get(key);
		if(valueObj!=null) return valueObj.booleanValue();
		else return defaultValue;
	}

	/**
	 * 从键值对Map中取出一个String值
	 * @param msgMap
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static String getString(Map<String, Object> msgMap, String key, String defaultValue){
		String valueObj = (String)msgMap.get(key);
		if(valueObj!=null) return valueObj;
		else return defaultValue;
	}

	/**
	 * 从键值对Map中取出一个Json对象值，即一个键值对Map
	 * @param msgMap
	 * @param key
	 * @return
	 */
	public static Map<String, Object> getJsonObject(Map<String, Object> msgMap, String key){
		return (Map<String, Object>) msgMap.get(key);
	}

	/**
	 * 从键值对Map中取出一个Json对象数组，即一个键值对Map的列表
	 * @param msgMap
	 * @param key
	 * @return
	 */
	public static List<Map<String,Object>> getArray(Map<String, Object> msgMap, String key){
		return (List<Map<String, Object>>) msgMap.get(key);
	}
}
