package com.pada.spider.event;

import com.pada.spider.bean.JsoupExpress;
import com.pada.spider.domain.SpiderResult;
import com.pada.spider.domain.SpiderStrategy;

import java.util.Map;

/**
 * Created by eqyun on 2014/10/31.
 */
public interface SpiderProcessor {

    public Map<String,Object[]> resultProcess(Map<String,Object[]> spiderResult,String spiderUrlId);


    public Object[] jsoupProcess(Object[] jsoupResult,JsoupExpress jsoupExpress);
    /**
     * 对结果的最后处理，是插入数据库还是写进文本，你喜欢。会在其他模块中调用
     * @param spiderResult
     * @param spiderStrategy
     */
    public void finalProcess(SpiderResult spiderResult,SpiderStrategy spiderStrategy);

    public String getIdentity();

    public void init() throws Exception;

}
