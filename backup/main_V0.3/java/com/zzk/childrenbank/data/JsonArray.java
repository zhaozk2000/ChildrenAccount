package com.zzk.childrenbank.data;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class JsonArray extends com.zzk.childrenbank.data.JsonObject {
	List<com.zzk.childrenbank.data.JsonObject> entryList = new LinkedList<com.zzk.childrenbank.data.JsonObject>();
	
	public void add(com.zzk.childrenbank.data.JsonObject jsonObj){
		entryList.add(jsonObj);
	}

	@Override
	public String toJsonString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		Iterator<com.zzk.childrenbank.data.JsonObject> iter = entryList.iterator();
		while(iter.hasNext()) {
			com.zzk.childrenbank.data.JsonObject entry = iter.next();
			buffer.append(entry.toJsonString());
			if(iter.hasNext()) buffer.append(",");
		}
		buffer.append("]");
		return buffer.toString();
	}
}
