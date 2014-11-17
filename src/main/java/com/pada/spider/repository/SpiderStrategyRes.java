package com.pada.spider.repository;

import com.pada.spider.domain.SpiderStrategy;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by eqyun on 2014/10/31.
 */
public interface SpiderStrategyRes extends MongoRepository<SpiderStrategy,String> {

    public SpiderStrategy findByClassify(String classify);

}
