package com.pada.spider.repository;

import com.pada.spider.domain.SpiderResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

/**
 * Created by eqyun on 2014/10/31.
 */
public interface SpiderResultRes extends MongoRepository<SpiderResult,String> {

    public SpiderResult findByUrlAndSpiderStrategyId(String url,String strategyId);

    @Query("{spiderStrategyId: ?0}")
    Page<SpiderResult> findBySpiderStrategyId(String spiderStrategyId,Pageable pageable);

}
