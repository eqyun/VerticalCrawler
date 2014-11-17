package com.pada.spider.domain;

import com.pada.mongo.domain.BaseDomain;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
import java.util.Set;

@Document(collection = "spider_result")
public class SpiderResult extends BaseDomain {
	
	private String spiderUrlId;
	
	private String spiderStrategyId;
	
	private Map<String,Object[]> spiderResult;
	
	private String url;


    public SpiderResult(String spiderStrategyId, String url) {
        this.spiderStrategyId = spiderStrategyId;
        this.url = url;
    }

    public SpiderResult() {
    }

    private boolean isRead;//如有更新，便标记未读



    public String getSpiderUrlId() {
        return spiderUrlId;
    }

    public void setSpiderUrlId(String spiderUrlId) {
        this.spiderUrlId = spiderUrlId;
    }

    public String getSpiderStrategyId() {
        return spiderStrategyId;
    }

    public void setSpiderStrategyId(String spiderStrategyId) {
        this.spiderStrategyId = spiderStrategyId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

	public Map<String, Object[]> getSpiderResult() {
		return spiderResult;
	}

	public void setSpiderResult(Map<String, Object[]> spiderResult) {
		this.spiderResult = spiderResult;
	}
}
