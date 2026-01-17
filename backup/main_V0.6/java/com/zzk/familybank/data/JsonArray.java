package com.zzk.familybank.data;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class JsonArray extends com.zzk.familybank.data.JsonObject {
	List<com.zzk.familybank.data.JsonObject> entryList = new LinkedList<com.zzk.familybank.data.JsonObject>();
	
	public void add(com.zzk.familybank.data.JsonObject jsonObj){
		entryList.add(jsonObj);
	}

	@Override
	public String toJsonString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		Iterator<com.zzk.familybank.data.JsonObject> iter = entryList.iterator();
		while(iter.hasNext()) {
			com.zzk.familybank.data.JsonObject entry = iter.next();
			buffer.append(entry.toJsonString());
			if(iter.hasNext()) buffer.append(",");
		}
		buffer.append("]");
		return buffer.toString();
	}
}
