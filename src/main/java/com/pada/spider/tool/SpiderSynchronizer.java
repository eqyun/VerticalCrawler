/*
package com.pada.spider.tool;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Observable;
import java.util.Set;

*/
/**
 * Created by eqyun on 2014/11/4.
 *//*

@Component
public class SpiderSynchronizer extends Observable {

    public Table<Long, String, Object> tables = HashBasedTable.create();



    public void checkSynchronize(String key) throws InterruptedException {

        if(tables.containsColumn(key)){
            Stopper stopper = new Stopper(key);
            this.addObserver(stopper);
            stopper.start();
            stopper.join();
            this.deleteObserver(stopper);
        }

    }

    public void put(Long threadId,String key,Object object){
        tables.put(threadId,key,object);
    }

    public void remove(Long threadId){
        if(tables.containsRow(threadId)){
            try {
               Map<String,Object> map = tables.rowMap().get(threadId);
                if(map!=null){
                    Set<String > keys = map.keySet();
                    if(keys == null || keys.size()==0)
                        return;
                    for(String key :keys){
                        if(tables.contains(threadId,key)) {
                            setChanged();
                            notifyObservers(key);
                        }
                    }
                    tables.rowMap().remove(threadId);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }


}
*/
