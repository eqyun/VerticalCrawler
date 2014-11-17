package com.pada.spider.tool;


import com.pada.mydao.bean.Wp_terms;
import com.pada.spider.bean.JsoupExpress;
import com.pada.spider.bean.SpiderPath;
import com.pada.spider.domain.SpiderResult;
import com.pada.spider.domain.SpiderStrategy;
import com.pada.spider.domain.SpiderUrlInfo;
import com.pada.spider.event.SpiderProcessor;

import org.jsoup.nodes.Document;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by eqyun on 2014/10/31.
 */
public interface SpiderAnalyse {

    public SpiderPath getSpiderPath(SpiderStrategy spiderStrategy,int depth);


    public SpiderPath getNextSpiderPath(SpiderStrategy spiderStrategy,int nowDepth);


    public SpiderPath getPreSpiderPath(SpiderStrategy spiderStrategy,int nowDepth);


    public String[] fetchJsoup(JsoupExpress jsoupExpress, Document body_copy);


    public String[] fetchJsoup(JsoupExpress jsoupExpress,String html);

    //找出所有的url
    public List<String> fetchUrl(String html);


    public String[][] fetchUrlAndLinkDescription(String html,boolean isRss);

    //找长辈
    public List<SpiderUrlInfo> getSpiderUrlInfoOldFamily(SpiderUrlInfo spiderUrlInfo);
    
    public boolean isEqu(JsoupExpress je1, JsoupExpress je2);
    
    /**
     * 
     * @param entityType  要转成的类
     * @param spiderResult 
     * @param spiderStrategy
     * @param ordinal 如果这个策略中在实例多个这个对象，这个变量是控制第几个的，默认为0，表示只有一个
     * @return
     */
    public List<?> entityMapping(Class<?> entityType,SpiderResult spiderResult,SpiderProcessor spiderProcessStrategy);
    
    
    /**
     * 拿next captureInfo
     * @param spiderUrlInfoid
     * @param howOld 如果找上一层的，就是大一岁，为1
     * @return
     */
    public Map<String,Object[]> analyseOldNextCaptureInfos(String spiderUrlInfoId,int howOld);
    
    /**
     * 拿这一层中指定要捉的信息，不是为一层服务的那种
     * @param spiderUrlInfoId
     * @param howOld 如果找上一层的，就是大一岁，为1
     * @return
     */
    public Map<String,Object[]> analyseOldWebCaptureInfos(String spiderUrlInfoId,int howOld);

    public SpiderUrlInfo analyseOldSpiderUrlInfo(String spiderUriInfoId,int howOld);

    public void completeImageUrl(Document document,String domain);


    public List<Wp_terms> generateDefaultTerms(Collection catOrTags);
    
    

}
