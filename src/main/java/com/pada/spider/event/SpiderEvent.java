package com.pada.spider.event;

import com.pada.spider.domain.SpiderResult;
import com.pada.spider.domain.SpiderStrategy;
import com.pada.spider.domain.SpiderUrlInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by eqyun on 2014/10/31.
 */
public interface SpiderEvent {

    public SpiderUrlInfo getSpiderUrlByUrl(String url,String strategyId);

    public Object saveOrUpdate(Object entity);

    public SpiderUrlInfo saveNextSpiderUrl(SpiderUrlInfo father,String[] descriptionAndUrl);

    public SpiderUrlInfo saveBrotherSpiderUrl(SpiderUrlInfo brother,String[] descriptionAndUrl);

    public SpiderResult getSpiderResultByUrl(String url,String strategyId);


    public List<SpiderStrategy> getSpiderStrategies();


    public SpiderStrategy getSpiderStrategyByClassify(String classify);


    public void delete(Object entity);


    public SpiderUrlInfo getSpiderUrlInfoById(String spiderUrlInfoId);


    public Page<SpiderResult> getSpiderResultPageByStrategyId(String strategyId,Pageable pageable);
}
