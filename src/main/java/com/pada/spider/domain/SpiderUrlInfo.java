package com.pada.spider.domain;

import com.google.common.collect.Maps;
import com.pada.mongo.domain.BaseDomain;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
@Document(collection = "spider_url")
public class SpiderUrlInfo extends BaseDomain{

    private String spiderStrategyId;

    private String parentId = "0";//上一级是哪个，二叉树结构

    private boolean isTarget;


    private int depth = 0;


    @Indexed
    private String url;//原url

    private String linkDescription;//url的来源描述，看到什么点过来的

    private Map<String,Object[]> captureInfos = Maps.newHashMap();


    private Map<String,Map<String,Object[]>> nextCaptureInfos= Maps.newHashMap();


    public SpiderUrlInfo() {
    }
    
    


    public SpiderUrlInfo(String spiderStrategyId, String url) {
		super();
		this.spiderStrategyId = spiderStrategyId;
		this.url = url;
	}





    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public boolean isTarget() {
        return isTarget;
    }

    public void setTarget(boolean isTarget) {
        this.isTarget = isTarget;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLinkDescription() {
        return linkDescription;
    }

    public void setLinkDescription(String linkDescription) {
        this.linkDescription = linkDescription;
    }



    public String getSpiderStrategyId() {
        return spiderStrategyId;
    }

    public void setSpiderStrategyId(String spiderStrategyId) {
        this.spiderStrategyId = spiderStrategyId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }




	public Map<String, Object[]> getCaptureInfos() {
		return captureInfos;
	}




	public void setCaptureInfos(Map<String, Object[]> captureInfos) {
		this.captureInfos = captureInfos;
	}




	public Map<String, Map<String, Object[]>> getNextCaptureInfos() {
		return nextCaptureInfos;
	}




	public void setNextCaptureInfos(
			Map<String, Map<String, Object[]>> nextCaptureInfos) {
		this.nextCaptureInfos = nextCaptureInfos;
	}
}
