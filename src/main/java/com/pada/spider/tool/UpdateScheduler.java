package com.pada.spider.tool;

import com.pada.spider.strategy.Piaohua;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Created by eqyun on 2014/11/8.
 */
@EnableScheduling
public class UpdateScheduler {
    @Autowired
    public Piaohua piaohua;
    @Autowired
    public SpiderStarter spiderStarter;


    @Scheduled(fixedRate = 1000 * 60 * 60 * 2)
    public void updatePiaoHua() throws Exception {
        piaohua.init();
        spiderStarter.startCrawler(piaohua.getIdentity(), false);
    }
}
