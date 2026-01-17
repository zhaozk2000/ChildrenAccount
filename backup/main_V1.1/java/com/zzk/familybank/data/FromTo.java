package com.zzk.familybank.data;

public class FromTo {
    public int id;
    //public int child;
    public String name;
    public int direction;        //  1=来源；2=去向；3=来源+去向；4=自己  4用在统计收支时去掉内部转账

    public static String[] FROMTOSTRING = {"来源", "去向", "来源+去向", "自己"};

    public String getFromToString() {
        return FROMTOSTRING[direction-1];
    }

    @Override
    public boolean equals(Object object){
        FromTo other = (FromTo) object;
        if(!name.equals(other.name)) return false;
        if(direction!=other.direction) return false;
        return true;
    }

    public void copyFrom(FromTo fromTo) {
        id = fromTo.id;
        name = fromTo.name;
        direction = fromTo.direction;
    }
}
