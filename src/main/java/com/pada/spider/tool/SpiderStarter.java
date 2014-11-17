package com.pada.spider.tool;

import com.pada.spider.bean.FullTestDelRecord;
import com.pada.spider.domain.SpiderResult;
import com.pada.spider.domain.SpiderStrategy;
import com.pada.spider.domain.SpiderUrlInfo;
import com.pada.spider.event.PageProcessorEvent;
import com.pada.spider.event.SpiderEvent;
import com.pada.spider.event.SpiderProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import us.codecraft.webmagic.Spider;

import java.util.*;

@Component
public class SpiderStarter {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    private SpiderEvent spiderEvent;

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    List<SpiderProcessor> spiderProcessors = new ArrayList<>();
    
    @Autowired
    private SpiderAnalyse spiderAnalyse;


    FullTestDelRecord fullTestDelRecord = new FullTestDelRecord();

    public void startCrawler(String classify, boolean isDebug) throws Exception {
        Map<String, SpiderProcessor> m_spiderProcessor = applicationContext.getBeansOfType(SpiderProcessor.class);

        if (m_spiderProcessor != null) {
            spiderProcessors.addAll(m_spiderProcessor.values());
        }

        if (classify == null) {
            List<SpiderStrategy> spiderStrategies = spiderEvent.getSpiderStrategies();
            for (SpiderStrategy spiderStrategy : spiderStrategies) {
                startSpider(spiderStrategy, isDebug);
            }

        } else {
            SpiderStrategy spiderStrategy = spiderEvent.getSpiderStrategyByClassify(classify);
            startSpider(spiderStrategy, isDebug);
        }

    }


    private void startSpider(SpiderStrategy spiderStrategy, boolean isDebug) throws Exception {

        if(spiderProcessors!=null){
            for(SpiderProcessor spiderProcessor : spiderProcessors){
                if(spiderProcessor.getIdentity().equals(spiderStrategy.getClassify())){
                    spiderProcessor.init();

                }
            }
        }

        String[] startUrl = spiderStrategy.getStartUrl();
        if (isDebug) {
            Spider.create(
                    new PageProcessorEvent(spiderEvent, spiderStrategy, spiderProcessors,spiderAnalyse,fullTestDelRecord)).addUrl(startUrl).thread(1).run();

            List<SpiderResult> result = fullTestDelRecord.getSpiderResults();
            
            
         
            for(SpiderResult spiderResult : result){
            	SpiderUrlInfo spiderUrlInfo = spiderEvent.getSpiderUrlByUrl(spiderResult.getUrl(), spiderResult.getSpiderStrategyId());
            	List<SpiderUrlInfo> spiderUrlInfos = spiderAnalyse.getSpiderUrlInfoOldFamily(spiderUrlInfo);
                int i = spiderUrlInfos.size();
                System.out.println("family:");
                
                for(SpiderUrlInfo _sInfo : spiderUrlInfos){
                    i--;
                    System.out.println("  "+i+":"+_sInfo.getLinkDescription()+":"+_sInfo.getUrl());
                    Map<String, Map<String, Object[]>> infos = _sInfo.getNextCaptureInfos();
                    if(infos!=null)
                    	System.out.println(" "+infos.keySet());
                }
                
                System.out.println("result:");
                Map<String,Object[]> mapResult = spiderResult.getSpiderResult();
                Set<String> keys = mapResult.keySet();
                for(String key : keys){
                     System.out.println(" "+key+":"+Arrays.asList(mapResult.get(key)).toString());
                }
                System.out.println("---------");
            }

            delete(fullTestDelRecord);

        } else {
            Spider.create(
                    new PageProcessorEvent(spiderEvent, spiderStrategy, spiderProcessors,spiderAnalyse)).addUrl(startUrl).thread(spiderStrategy.getThread()).run();
        }
        if(!spiderStrategy.isWorkWithFinalProcess())
            doFinalProcess(spiderStrategy);
    }


    private void doFinalProcess(SpiderStrategy spiderStrategy){

        int count = 0;
        int perNum = 1000;
        SpiderProcessor targetProcessor = null;

        if(spiderProcessors !=null ){
            for(SpiderProcessor spiderProcessor : spiderProcessors){
                if(spiderProcessor.getIdentity().equals(spiderStrategy.getClassify())){
                    targetProcessor = spiderProcessor;
                }
            }
        }
        for(;;) {

            Page<SpiderResult> spiderResultPage = spiderEvent.getSpiderResultPageByStrategyId(spiderStrategy.getId(), new PageRequest(count, perNum));

            if (spiderResultPage != null && spiderResultPage.getContent().size() > 0) {

                List<SpiderResult> spiderResults = spiderResultPage.getContent();

                for(SpiderResult spiderResult : spiderResults){

                    if(targetProcessor !=null ){
                        targetProcessor.finalProcess(spiderResult,spiderStrategy);
                    }
                }

            }
            if(spiderResultPage == null || spiderResultPage.getContent().size()<perNum)
                break;
            count++;
        }

    }

    private void delete(FullTestDelRecord fullTestDelRecord){
        if(fullTestDelRecord == null)
            return;
        List<?> result = fullTestDelRecord.getSpiderResults();
        for(Object object : result){
            spiderEvent.delete(object);
        }

        List<?> infos = fullTestDelRecord.getSpiderUrlInfos();
        for(Object object : infos){
            spiderEvent.delete(object);
        }
    }


}
