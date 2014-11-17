package com.pada.common;


import java.util.Date;
import java.util.Map;
import java.util.Set;

public class ArrUtil {
	
	public static boolean isEqu(Object[] a ,Object[] a2){
		if (a==a2)
            return true;
        if (a==null || a2==null)
            return false;
        int length = a.length;
        if (a2.length != length)
            return false;
        for (int i=0; i<a.length; i++)
        {
        	Object object1 = a[i];
    		Object object2 = a2[i];
    		if(object1 == object2)
    			continue;
    		if(object1 == null && object2!=null)
    			return false;
    		if(object1 !=null && object2 == null)
    			return false;
    		if(!object1.getClass().equals(object2.getClass()))
    			return false;
    		
    		if(!object1.equals(object2))
    			return false;
        }
        return true;
	}


    public static boolean isEqu(Map<String,Object[]> map1,Map<String,Object[]> map2){

        if(map1 == null && map2 == null)
            return true;

        if(map1!=null && map2 == null)
            return false;

        if(map2!=null && map1 == null)
            return false;
        if(map1.size() != map2.size())
            return false;

        Set<String> keys = map1.keySet();

        for(String key : keys){
            Object[] value1 = map1.get(key);
            Object[] value2 = map2.get(key);
            if(!isEqu(value1,value2))
                return false;
        }

        return true;
    }

}
