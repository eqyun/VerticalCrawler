package com.pada.spider.bean;

import com.google.common.collect.Lists;
import com.pada.spider.domain.SpiderResult;
import com.pada.spider.domain.SpiderUrlInfo;
import com.pada.spider.event.SpiderProcessor;

import java.util.List;

/**
 * Created by eqyun on 2014/10/31.
 */
public class FullTestDelRecord {

    List<SpiderUrlInfo> spiderUrlInfos = Lists.newArrayList();

    List<SpiderResult> spiderResults = Lists.newArrayList();

    public List<SpiderUrlInfo> getSpiderUrlInfos() {
        return spiderUrlInfos;
    }

    public void setSpiderUrlInfos(List<SpiderUrlInfo> spiderUrlInfos) {
        this.spiderUrlInfos = spiderUrlInfos;
    }

    public List<SpiderResult> getSpiderResults() {
        return spiderResults;
    }

    public void setSpiderResults(List<SpiderResult> spiderResults) {
        this.spiderResults = spiderResults;
    }
}
