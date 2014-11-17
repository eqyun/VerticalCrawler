package com.pada.spider.event.impl;

import com.pada.spider.domain.SpiderResult;
import com.pada.spider.domain.SpiderStrategy;
import com.pada.spider.domain.SpiderUrlInfo;
import com.pada.spider.event.SpiderEvent;
import com.pada.spider.repository.SpiderResultRes;
import com.pada.spider.repository.SpiderStrategyRes;
import com.pada.spider.repository.SpiderUrlRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by eqyun on 2014/10/31.
 */
@Service
public class SpiderEventImpl implements SpiderEvent {
    @Autowired
    private SpiderResultRes spiderResultRes;
    @Autowired
    private SpiderStrategyRes spiderStrategyRes;
    @Autowired
    private SpiderUrlRes spiderUrlRes;

    @Override
    public SpiderUrlInfo getSpiderUrlByUrl(String url, String strategyId) {
        return spiderUrlRes.findByUrlAndSpiderStrategyId(url, strategyId);
    }

    @Override
    public Object saveOrUpdate(Object entity) {
        if (entity instanceof SpiderUrlInfo)
            return spiderUrlRes.save((SpiderUrlInfo) entity);
        if (entity instanceof SpiderResult)
            return spiderResultRes.save((SpiderResult) entity);
        if (entity instanceof SpiderStrategy)
            return spiderStrategyRes.save((SpiderStrategy) entity);

        throw new RuntimeException("not match any entity for mongodb");
    }

    @Override
    public SpiderUrlInfo saveNextSpiderUrl(SpiderUrlInfo father, String[] descriptionAndUrl) {

        String url = descriptionAndUrl[1];


        SpiderUrlInfo spiderUrlInfo = getSpiderUrlByUrl(url, father.getSpiderStrategyId());
        if (spiderUrlInfo == null) {
            spiderUrlInfo = new SpiderUrlInfo(father.getSpiderStrategyId(),url);

        }
        spiderUrlInfo.setDepth(father.getDepth() + 1);
        spiderUrlInfo.setParentId(father.getId());
        spiderUrlInfo.setLinkDescription(descriptionAndUrl[0]);

        saveOrUpdate(spiderUrlInfo);

        return spiderUrlInfo;
    }

    @Override
    public SpiderUrlInfo saveBrotherSpiderUrl(SpiderUrlInfo brother, String[] descriptionAndUrl) {

        String url = descriptionAndUrl[1];

        SpiderUrlInfo spiderUrlInfo = getSpiderUrlByUrl(url, brother.getSpiderStrategyId());
        if (spiderUrlInfo == null) {
            spiderUrlInfo = new SpiderUrlInfo(brother.getSpiderStrategyId(),url);

        }
        spiderUrlInfo.setDepth(brother.getDepth());
        spiderUrlInfo.setParentId(brother.getParentId());

        spiderUrlInfo.setLinkDescription(descriptionAndUrl[0]);
        

        saveOrUpdate(spiderUrlInfo);
        return spiderUrlInfo;
    }

    @Override
    public SpiderResult getSpiderResultByUrl(String url, String strategyId) {

        return spiderResultRes.findByUrlAndSpiderStrategyId(url, strategyId);
    }

    @Override
    public List<SpiderStrategy> getSpiderStrategies() {
        return spiderStrategyRes.findAll();
    }

    @Override
    public SpiderStrategy getSpiderStrategyByClassify(String classify) {
    	return spiderStrategyRes.findByClassify(classify);
    }

    @Override
    public void delete(Object entity) {
        if (entity instanceof SpiderUrlInfo)
            spiderUrlRes.delete((SpiderUrlInfo) entity);
        else if (entity instanceof SpiderResult)
             spiderResultRes.delete((SpiderResult) entity);
        else if (entity instanceof SpiderStrategy)
             spiderStrategyRes.delete((SpiderStrategy) entity);
        else
        	throw new RuntimeException("not match any entity for mongodb");
    }

    @Override
    public SpiderUrlInfo getSpiderUrlInfoById(String spiderUrlInfoId) {
        return spiderUrlRes.findOne(spiderUrlInfoId);
    }

    @Override
    public Page<SpiderResult> getSpiderResultPageByStrategyId(String strategyId,Pageable pageable) {

        return spiderResultRes.findBySpiderStrategyId(strategyId,pageable);
    }
}
