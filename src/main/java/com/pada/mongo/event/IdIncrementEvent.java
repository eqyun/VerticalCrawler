package com.pada.mongo.event;

import org.springframework.beans.factory.annotation.Autowired;

import com.pada.mongo.domain.BaseDomain;
import com.pada.mongo.domain.Counter;
import com.pada.mongo.respository.DBIdRepository;

public class IdIncrementEvent {
    @Autowired
    private DBIdRepository dbIdRepository;

    public void incrementIdSetting(BaseDomain baseDomain) {
        if (baseDomain.getId() == null) {
            String clazz = baseDomain.get_class();

            Counter counter = dbIdRepository.findByClazz(clazz);
            if (counter == null) {
                counter = new Counter();
                counter.setClazz(baseDomain.get_class());
                counter.setCount(1);
                dbIdRepository.save(counter);
                baseDomain.setId("1");
            } else {
                long count = counter.getCount();
                count++;
                counter.setCount(count);
                dbIdRepository.save(counter);
                baseDomain.setId(String.valueOf(count));
            }
        }
    }

}
