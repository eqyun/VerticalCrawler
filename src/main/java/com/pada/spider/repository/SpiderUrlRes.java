package com.pada.spider.repository;

import com.pada.spider.domain.SpiderUrlInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by eqyun on 2014/10/31.
 */
public interface SpiderUrlRes extends MongoRepository<SpiderUrlInfo,String> {


    public SpiderUrlInfo findByUrlAndSpiderStrategyId(String url,String strategyId);


}
