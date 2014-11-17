package com.pada;
import com.pada.mongo.configuration.MongoDBIdConfiguration;
import com.pada.mydao.bean.Wp_posts;
import com.pada.mydao.bean.Wp_users;
import com.pada.mydao.bussines.IInsertPost;
import com.pada.mydao.configs.DatabaseConfig;
import com.pada.scheduler.Scheduler;
import com.pada.spider.strategy.HuXiu;
import com.pada.spider.strategy.Piaohua;
import com.pada.spider.strategy.TaiMeiTi;
import com.pada.spider.tool.SpiderStarter;

import com.pada.spider.tool.UpdateScheduler;
import com.pada.twitter.TwitterEvent;
import com.pada.twitter.TwitterTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by eqyun on 2014/11/1.
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.pada")
public class VerticalCrawlerApplication implements CommandLineRunner {

    @Autowired
    private SpiderStarter spiderStarter;
    @Autowired
    private Piaohua piaohua;
    @Autowired
    private TaiMeiTi taiMeiTi;
    @Autowired
    private HuXiu huXiu;

    @Autowired
    private IInsertPost iInsertPost;
    @Autowired
    private TwitterEvent twitterEvent;



    public static void main(String[] args) throws Exception {
        SpringApplication.run(new Class[]{VerticalCrawlerApplication.class,MongoDBIdConfiguration.class,DatabaseConfig.class, Scheduler.class}, args);
    }

    @Override
    public void run(String... args) throws Exception {
       // twitterEvent.pushTwitter2WP();

      /*  Wp_posts wp_posts = new Wp_posts();
        wp_posts.setPost_title("test5");
        wp_posts.setGuid("http://test.com");
        Wp_users wp_users= new Wp_users();
        wp_users.setUser_nicename("hello");
        wp_users.setUser_login("hello");

        Object[] categories = new Object[]{"test category"};
        Object[] tags = new Object[]{"test tag"};



        iInsertPost.insertPost(wp_posts,null,categories,tags,wp_users,null);*/

      /*  avNovel.init();

        spiderStarter.startCrawler(avNovel.getIdentity(), false);*/
    	/*piaohua.init();

        spiderStarter.startCrawler(piaohua.getIdentity(), false);*/
    }
}
