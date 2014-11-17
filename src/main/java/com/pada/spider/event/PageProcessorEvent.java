package com.pada.spider.event;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.pada.common.ArrUtil;
import com.pada.common.RegexUtil;
import com.pada.spider.bean.FullTestDelRecord;
import com.pada.spider.bean.JsoupExpress;
import com.pada.spider.bean.SpiderPath;
import com.pada.spider.domain.SpiderResult;
import com.pada.spider.domain.SpiderStrategy;
import com.pada.spider.domain.SpiderUrlInfo;
import com.pada.spider.tool.SpiderAnalyse;
import org.apache.http.HttpHost;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class PageProcessorEvent implements PageProcessor {

    private boolean isDebug = false;


    private Site site;


    private SpiderStrategy spiderStrategy;

    private SpiderEvent spiderEvent;

    private List<SpiderProcessor> spiderProcessors;

    private FullTestDelRecord fullTestDelRecord;

    private SpiderAnalyse spiderAnalyse;


    public PageProcessorEvent() {
        super();
    }


    /**
     * debug用的构造函数
     */
    public PageProcessorEvent(SpiderEvent spiderEvent, SpiderStrategy spiderStrategy, List<SpiderProcessor> spiderProcessors, SpiderAnalyse spiderAnalyse ,FullTestDelRecord fullTestDelRecord) {
        this.spiderEvent = spiderEvent;
        this.spiderStrategy = spiderStrategy;
        this.spiderProcessors = spiderProcessors;
        this.spiderAnalyse = spiderAnalyse;
        this.site = Site.me().setRetryTimes(spiderStrategy.getRetryTimes())
                .setSleepTime(spiderStrategy.getSleepTime())
                .setCharset(spiderStrategy.getEncoding())
                .setTimeOut(spiderStrategy.getTimeOut());
        if (spiderStrategy.getProxyIP() != null && spiderStrategy.getProxyIP().length() > 0) {
            HttpHost httpHost = new HttpHost(spiderStrategy.getProxyIP(), spiderStrategy.getProxyPort());
            this.site.setHttpProxy(httpHost);
        }
        this.isDebug = true;
        this.fullTestDelRecord = fullTestDelRecord;
    }


    /**
     * 正常的构造
     */
    public PageProcessorEvent(SpiderEvent spiderEvent, SpiderStrategy spiderStrategy, List<SpiderProcessor> spiderProcessors,SpiderAnalyse spiderAnalyse) {
    	this.spiderAnalyse = spiderAnalyse;
        this.spiderEvent = spiderEvent;
        this.spiderStrategy = spiderStrategy;
        this.spiderProcessors = spiderProcessors;
        this.site = Site.me().setRetryTimes(spiderStrategy.getRetryTimes())
                .setSleepTime(spiderStrategy.getSleepTime())
                .setCharset(spiderStrategy.getEncoding())
                .setTimeOut(spiderStrategy.getTimeOut());
        if (spiderStrategy.getProxyIP() != null && spiderStrategy.getProxyIP().length() > 0) {
            HttpHost httpHost = new HttpHost(spiderStrategy.getProxyIP(), spiderStrategy.getProxyPort());
            this.site.setHttpProxy(httpHost);
        }
        this.isDebug = false;
    }

    @Override
    public void process(Page page) {

        spiderAnalyse.completeImageUrl(page.getHtml().getDocument(),spiderStrategy.getDomain());

        String url = page.getUrl().toString();

        SpiderUrlInfo spiderUrlInfo = spiderEvent.getSpiderUrlByUrl(url,spiderStrategy.getId());

        analysePage(page, spiderUrlInfo);
    }

    //第1层为startUrl
    private void analysePage(Page page, SpiderUrlInfo spiderUrlInfo) {

        String url = page.getUrl().toString();



        if (spiderUrlInfo == null) {
            //表明不是从上一层来的,判定为startPath
        	spiderUrlInfo = new SpiderUrlInfo();
            spiderUrlInfo.setUrl(url);
            spiderUrlInfo = (SpiderUrlInfo) spiderEvent.saveOrUpdate(spiderUrlInfo);
            spiderUrlInfo.setSpiderStrategyId(spiderStrategy.getId());
            spiderUrlInfo.setDepth(1);
            spiderUrlInfo.setLinkDescription("Home");
            spiderEvent.saveOrUpdate(spiderUrlInfo);
            if(isDebug){
                fullTestDelRecord.getSpiderUrlInfos().add(spiderUrlInfo);
            }
        }

        int nowDepth = spiderUrlInfo.getDepth();


        SpiderPath spiderPath = spiderAnalyse.getSpiderPath(spiderStrategy, nowDepth);


        List<String> nextUrls = searchAndStoreNextPathUrl(page, spiderPath, spiderUrlInfo);

        page.addTargetRequests(nextUrls);

    }
    private void storeResult(Page page,SpiderPath spiderPath,SpiderUrlInfo spiderUrlInfo) {

        String url = page.getUrl().toString();

        SpiderResult spiderResult = spiderEvent.getSpiderResultByUrl(url,spiderStrategy.getId());

        if(spiderResult == null) {
            spiderResult = new SpiderResult(spiderStrategy.getId(),url);
        }


        Map<String,JsoupExpress> webInfoCaptures = spiderPath.getWebInfoCaptures();

        Map<String,Object[]> webCaptureInfos = utilFetchJsoup(webInfoCaptures,page.getHtml().toString());

        webCaptureInfos = doResultProcess(webCaptureInfos,spiderUrlInfo.getId());



        Map<String,Object[]> oldWebCaptureInfos = spiderResult.getSpiderResult();

        if(!ArrUtil.isEqu(webCaptureInfos,oldWebCaptureInfos)){
            spiderResult.setRead(false);
            spiderResult.setSpiderResult(webCaptureInfos);
        }

        spiderEvent.saveOrUpdate(spiderResult);

        if(isDebug)
            fullTestDelRecord.getSpiderResults().add(spiderResult);

        doFinalProcess(spiderResult);
        

    }

    private synchronized  void doFinalProcess(SpiderResult spiderResult){
        if(spiderStrategy.isWorkWithFinalProcess()) {
            if (spiderProcessors != null) {
                for (SpiderProcessor spiderProcessor : spiderProcessors) {
                    if (spiderProcessor.getIdentity().equals(spiderStrategy.getClassify())) {
                        spiderProcessor.finalProcess(spiderResult, spiderStrategy);
                    }
                }
            }
        }
    }


    private List<String> searchAndStoreNextPathUrl(Page page, SpiderPath nowSpiderPth, SpiderUrlInfo nowSpiderUrlInfo) {

        SpiderPath preSpiderPath = spiderAnalyse.getPreSpiderPath(spiderStrategy, nowSpiderPth.getDepth());
        

        String[] preNextUrlRegex = preSpiderPath == null ? null:preSpiderPath.getNextPathRegexs();


        JsoupExpress[] areaForNextPathJsoupExpresses = nowSpiderPth.getAreaForNextPathJsoupExpresses();

        List<Object[]> areas = Lists.newArrayList();

        //第一步，先找出指定的区域，如果没有指定，默认全区域
        if (areaForNextPathJsoupExpresses != null && areaForNextPathJsoupExpresses.length > 0) {
            for (JsoupExpress _jsoupExpress : areaForNextPathJsoupExpresses) {
                areas.add(utilFetchJsoup(_jsoupExpress, page.getHtml().getDocument().toString()));
            }
        } else
            areas.add(new String[]{page.getHtml().toString()});


        String[] nextUrlRegexs = nowSpiderPth.getNextPathRegexs();

        Map<String, JsoupExpress> perNextPathInfoCaptures = nowSpiderPth.getPerNextUrlInfoCaptures();

        Set<String> nextUrlSets = Sets.newHashSet();

        Set<String> brotherUrlSets = Sets.newHashSet();

        //第2步，在指定的区域内寻找下一个url

        if (nextUrlRegexs != null && nextUrlRegexs.length > 0) {
            for (Object[] area : areas) {
                if (area != null) {
                    for (Object o_area : area) {
                    	String _area = (String) o_area;
                        String[][] descriptionAndLinks = spiderAnalyse.fetchUrlAndLinkDescription(_area,nowSpiderPth.isRss());
                        if (descriptionAndLinks != null) {
                            for (String[] descriptionAndLink : descriptionAndLinks) {
                                String url = descriptionAndLink[1];
                                //检查是不是一下层
                                if (utilRegexCheck(nextUrlRegexs, url)) {
                                    SpiderUrlInfo spiderUrlInfo = null;
                                    if (!nextUrlSets.contains(url)) {
                                        nextUrlSets.add(url);
                                        //第2.1步，找到了url，保存spiderUrl
                                         spiderUrlInfo = spiderEvent.saveNextSpiderUrl(nowSpiderUrlInfo, descriptionAndLink);

                                        //第2.2步，保存以这条url相关的 额外信息到现在这层的 spiderUrl
                                        if (perNextPathInfoCaptures != null) {
                                            Map<String, Object[]> pathCaptureInfos = utilFetchJsoup(perNextPathInfoCaptures, _area);
                                            nowSpiderUrlInfo.getNextCaptureInfos().put(url, pathCaptureInfos);
                                        }
                                        if(isDebug) {
                                            fullTestDelRecord.getSpiderUrlInfos().add(spiderUrlInfo);
                                            break;
                                        }
                                    }
                                }
                                //检查是不是需不需要找同层
                                if (nowSpiderPth.isCaptureSameLevel()) {

                                    if (utilRegexCheck(preNextUrlRegex, url)) {

                                        if (!brotherUrlSets.contains(url)) {
                                            brotherUrlSets.add(url);
                                            SpiderUrlInfo spiderUrlInfo = spiderEvent.saveBrotherSpiderUrl(nowSpiderUrlInfo, descriptionAndLink);
                                            if(isDebug) 
                                                fullTestDelRecord.getSpiderUrlInfos().add(spiderUrlInfo);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            //第三步，如果还有下一层，就把这一层的信息保存起来,如果没有下一层了，那保存也没用
            if(nextUrlRegexs!=null && nextUrlRegexs.length>0) {
                Map<String, JsoupExpress> webInfoCaptures = nowSpiderPth.getWebInfoCaptures();

                Map<String, Object[]> webCaptureInfos = utilFetchJsoup(webInfoCaptures, page.getHtml().toString());

                nowSpiderUrlInfo.setCaptureInfos(webCaptureInfos);
            }
        }

        if (nowSpiderPth.isTarget()) {
            storeResult(page,nowSpiderPth,nowSpiderUrlInfo);
            nowSpiderUrlInfo.setTarget(true);
        }

        Set<String> allNextUrls = Sets.newHashSet();
        allNextUrls.addAll(nextUrlSets);
        allNextUrls.addAll(brotherUrlSets);

        List<String> urls = Lists.newArrayList();
        urls.addAll(allNextUrls);

        spiderEvent.saveOrUpdate(nowSpiderUrlInfo);

        if(isDebug){
            urls.clear();
            urls.addAll(nextUrlSets);
            if(urls!=null && urls.size()>0)
              urls =  urls.subList(0,1);
        }

        return urls;
    }


    private boolean utilRegexCheck(String[] regulars, String input) {
        if(regulars == null || regulars.length ==0)
            return false;

        for (String regular : regulars) {
            if (RegexUtil.check(input, regular))
                return true;
        }
        return false;
    }




    private Object[] utilFetchJsoup(JsoupExpress jsoupExpress, String html) {
        String[] value = spiderAnalyse.fetchJsoup(jsoupExpress, html);
        return doJsoupResultProcess(value, jsoupExpress);
    }


    private Map<String, Object[]> utilFetchJsoup(Map<String, JsoupExpress> webInfoCaptures, String html) {

        if(webInfoCaptures == null)
            return null;

        Map<String,Object[]> webCaptureInfos = Maps.newHashMap();
        Set<String> keys = webInfoCaptures.keySet();

        for(String key : keys){
            JsoupExpress jsoupExpress = webInfoCaptures.get(key);
            Object[] jsoupResult = utilFetchJsoup(jsoupExpress,html);

            webCaptureInfos.put(key,jsoupResult);
        }
        return webCaptureInfos;
    }


    private Map<String,Object[]> doResultProcess(Map<String,Object[]> webCaptureInfos, String spiderUrlId) {
        if(spiderProcessors !=null ){
            for(SpiderProcessor spiderProcessor : spiderProcessors){
                if(spiderProcessor.getIdentity().equals(spiderStrategy.getClassify())){
                    return spiderProcessor.resultProcess(webCaptureInfos,spiderUrlId);
                }
            }
        }
        return webCaptureInfos;
    }


    private Object[] doJsoupResultProcess(String[] jsoupResult,JsoupExpress jsoupExpress){
        if(spiderProcessors !=null ){
            for(SpiderProcessor spiderProcessor : spiderProcessors){
                if(spiderProcessor.getIdentity().equals(spiderStrategy.getClassify())){
                    return spiderProcessor.jsoupProcess(jsoupResult,jsoupExpress);
                }
            }
        }
        return jsoupResult;
    }

    @Override
    public Site getSite() {
        return site;
    }
}