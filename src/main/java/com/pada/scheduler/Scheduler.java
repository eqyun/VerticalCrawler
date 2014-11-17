package com.pada.scheduler;

import com.pada.spider.strategy.wealthdigg.Bloomberg;
import com.pada.spider.strategy.wealthdigg.MarketWatch;
import com.pada.spider.tool.SpiderStarter;
import com.pada.twitter.TwitterEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Created by eqyun on 2014/11/12.
 */
@EnableScheduling
public class Scheduler {

    @Autowired
    SpiderStarter spiderStarter ;

    @Autowired
    private TwitterEvent twitterEvent;
    @Autowired
    private MarketWatch marketWatch;
    @Autowired
    private Bloomberg bloomberg;


    @Scheduled(fixedRate = 1000*60*60*24)
    public void updateSpider() throws Exception {
        marketWatch.init();
        bloomberg.init();
        System.out.println("I am update spider .........");
        spiderStarter.startCrawler(marketWatch.getIdentity(),false);
        spiderStarter.startCrawler(bloomberg.getIdentity(),false);
    }

    @Scheduled(fixedRate = 1000 * 60 *60)
    public void updateTwitter() throws  Exception{
        System.out.println("I am update twitter ...........");
        twitterEvent.pushTwitter2WP();
    }
}
