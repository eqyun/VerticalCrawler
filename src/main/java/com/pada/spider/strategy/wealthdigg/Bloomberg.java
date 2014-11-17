package com.pada.spider.strategy.wealthdigg;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pada.mydao.bean.Wp_postmeta;
import com.pada.mydao.bean.Wp_posts;
import com.pada.mydao.bean.Wp_users;
import com.pada.mydao.bussines.IInsertPost;
import com.pada.spider.annotation.SpiderKeyInject;
import com.pada.spider.bean.JsoupExpress;
import com.pada.spider.bean.SpiderPath;
import com.pada.spider.domain.SpiderResult;
import com.pada.spider.domain.SpiderStrategy;
import com.pada.spider.domain.SpiderUrlInfo;
import com.pada.spider.event.SpiderEvent;
import com.pada.spider.event.SpiderProcessor;
import com.pada.spider.tool.SpiderAnalyse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by sinceow on 2014/11/4.
 */
@Component
public class Bloomberg implements SpiderProcessor {


    final String classify = "bloomberg";

    String websiteName = "Bloomberg";

    String domain = "http://www.bloomberg.com/";

    String[] startUrl = new String[]{
            "http://www.bloomberg.com/news/stocks/",
            "http://www.bloomberg.com/news/commodities/",
            "http://www.bloomberg.com/news/currencies/",
            "http://www.bloomberg.com/news/bonds/",
            "http://www.bloomberg.com/news/municipal-bonds/",
            "http://www.bloomberg.com/news/energy-markets/",
            "http://www.bloomberg.com/news/funds/",
            "http://www.bloomberg.com/news/economy/",
            "http://www.bloomberg.com/news/energy/",
            "http://www.bloomberg.com/news/technology/",
            "http://www.bloomberg.com/news/real-estate/",
            "http://www.bloomberg.com/news/finance/",
            "http://www.bloomberg.com/news/health-care/",
            "http://www.bloomberg.com/news/transportation/",
            "http://www.bloomberg.com/news/insurance/",
            "http://www.bloomberg.com/news/retail/",
            "http://www.bloomberg.com/news/media/",
            "http://www.bloomberg.com/news/manufacturing/",
            "http://www.bloomberg.com/personal-finance/saving-and-investing/",
            "http://www.bloomberg.com/personal-finance/real-estate/",
            "http://www.bloomberg.com/personal-finance/retirement-planning/",
            "http://www.bloomberg.com/personal-finance/taxes/",
            "http://www.bloomberg.com/news/markets/",
            "http://www.bloomberg.com/markets-magazine/"
    };

    @Autowired
    SpiderEvent spiderEvent;

    @Autowired
    SpiderAnalyse spiderAnalyse;

    @Autowired
    IInsertPost iInsertPost;

   /* @SpiderKeyInject(clazz = Wp_postmeta.class, mappingKey = "meta_key", mappingValue = "meta_value", sign = 0)
    private String meta_score = "_meta_score";*/

    @SpiderKeyInject(clazz = Wp_postmeta.class, mappingKey = "meta_key", mappingValue = "meta_value", sign = 0, isNecessity = false)
    private String meta_thumbnail = "_meta_thumbnail";
    JsoupExpress metaThumbnailExpress = new JsoupExpress("meta[itemprop=image]", "content", null, false);

    @SpiderKeyInject(clazz = Wp_posts.class, mappingValue = "post_date")
    private String post_time = "post_time";
    JsoupExpress postTimeExpress = new JsoupExpress("meta[name=pubdate]", "content", null, false);

    @SpiderKeyInject(clazz = Wp_posts.class, mappingValue = "post_title")
    private String post_title = "post_title";
    JsoupExpress postTitleExpress = new JsoupExpress("meta[name=title]", "content", null, false);

    @SpiderKeyInject(clazz = Wp_posts.class, mappingValue = "post_excerpt")
    private String post_excerpt = "post_excerpt";
    JsoupExpress postExcerptExpress = new JsoupExpress("meta[name=description]", "content", null, false);

    @SpiderKeyInject(clazz = Wp_posts.class, mappingValue = "post_content")
    private String post_content = "post_content";
    JsoupExpress postContentExpress = new JsoupExpress("div.article_body", null, null, true, true);

    @SpiderKeyInject(clazz = Wp_users.class, mappingValue = "user_login")
    private String user_login = "user_login";
    JsoupExpress userLoginExpress = new JsoupExpress("span.author", null, null, false);

    private String post_tags = "tags";
    JsoupExpress tagsExpress = new JsoupExpress("meta[name=keywords]", "content", null, false);

    private String post_categories = "categories";

    String articleDetailRegex = "http://www.bloomberg.com/news/\\d{4}-\\d{2}-\\d{2}/.+.html";

    public void init() throws Exception {
        SpiderStrategy spiderStrategy = spiderEvent.getSpiderStrategyByClassify(classify);
        if (spiderStrategy == null) {
            spiderStrategy = new SpiderStrategy(classify);
        }

        spiderStrategy.setStartUrl(startUrl);


        SpiderPath spiderPath1 = new SpiderPath(1);
        spiderPath1.setRss(true);
        spiderPath1.setNextPathRegexs(new String[]{articleDetailRegex});


        SpiderPath spiderPath2 = new SpiderPath(2);
        spiderPath2.setTarget(true);

        Map<String, JsoupExpress> captures = Maps.newHashMap();
        captures.put(meta_thumbnail, metaThumbnailExpress);
        captures.put(post_content, postContentExpress);
        captures.put(user_login, userLoginExpress);
        captures.put(post_tags, tagsExpress);
        captures.put(post_time, postTimeExpress);
        captures.put(post_title, postTitleExpress);
        captures.put(post_excerpt, postExcerptExpress);
        spiderPath2.setWebInfoCaptures(captures);

        SpiderPath[] spiderPaths = new SpiderPath[]{spiderPath1, spiderPath2};

        spiderStrategy.setSpiderPaths(spiderPaths).setProxyIP("127.0.0.1").setProxyPort(8580);

        spiderEvent.saveOrUpdate(spiderStrategy);

    }

    @Override
    public Map<String, Object[]> resultProcess(Map<String, Object[]> spiderResult, String spiderUrlId) {
        SpiderUrlInfo spiderUrlInfo = spiderAnalyse.analyseOldSpiderUrlInfo(spiderUrlId, 1);
        String fatherUrl = spiderUrlInfo.getUrl();
        if (fatherUrl.endsWith("stocks/")) {
            spiderResult.put(post_categories, new String[]{"Stocks"});
        } else if (fatherUrl.endsWith("commodities/")) {
            spiderResult.put(post_categories, new String[]{"Commodities"});
        } else if (fatherUrl.endsWith("currencies/")) {
            spiderResult.put(post_categories, new String[]{"Currencies"});
        } else if (fatherUrl.endsWith("bonds/")) {
            spiderResult.put(post_categories, new String[]{"Bonds"});
        } else if (fatherUrl.endsWith("municipal-bonds/")) {
            spiderResult.put(post_categories, new String[]{"Municipal Bonds", "Bonds"});
        } else if (fatherUrl.endsWith("energy-markets/")) {
            spiderResult.put(post_categories, new String[]{"Economy", "Emerging Markets"});
        } else if (fatherUrl.endsWith("funds/")) {
            spiderResult.put(post_categories, new String[]{"Funds"});
        } else if (fatherUrl.endsWith("economy/")) {
            spiderResult.put(post_categories, new String[]{"Economy"});
        } else if (fatherUrl.endsWith("energy/")) {
            spiderResult.put(post_categories, new String[]{"Energy"});
        } else if (fatherUrl.endsWith("technology/")) {
            spiderResult.put(post_categories, new String[]{"Technology"});
        } else if (fatherUrl.endsWith("real-estate/")) {
            spiderResult.put(post_categories, new String[]{"Real Estate"});
        } else if (fatherUrl.endsWith("personal-finance/saving-and-investing/")) {
            spiderResult.put(post_categories, new String[]{"Personal Finance", "Savings & Investing"});
        } else if (fatherUrl.endsWith("personal-finance/real-estate/")) {
            spiderResult.put(post_categories, new String[]{"Personal Finance", "Real Estate"});
        } else if (fatherUrl.endsWith("personal-finance/retirement-planning/")) {
            spiderResult.put(post_categories, new String[]{"Personal Finance", "Retirement Planning"});
        } else if (fatherUrl.endsWith("personal-finance/real-estate/")) {
            spiderResult.put(post_categories, new String[]{"Personal Finance", "Real Estate"});
        } else if (fatherUrl.endsWith("personal-finance/taxes/")) {
            spiderResult.put(post_categories, new String[]{"Personal Finance", "Taxes"});
        } else if (fatherUrl.endsWith("markets-magazine/")) {
            spiderResult.put(post_categories, new String[]{"Markets"});
        } else if (fatherUrl.endsWith("news/markets/")) {
            spiderResult.put(post_categories, new String[]{"Markets"});
        } else if (fatherUrl.endsWith("finance/")) {
            spiderResult.put(post_categories, new String[]{"Finance"});
        } else if (fatherUrl.endsWith("health-care/")) {
            spiderResult.put(post_categories, new String[]{"Health Care"});
        } else if (fatherUrl.endsWith("transportation/")) {
            spiderResult.put(post_categories, new String[]{"Transportation"});
        } else if (fatherUrl.endsWith("insurance/")) {
            spiderResult.put(post_categories, new String[]{"Insurance"});
        } else if (fatherUrl.endsWith("retail/")) {
            spiderResult.put(post_categories, new String[]{"Retail"});
        } else if (fatherUrl.endsWith("media/")) {
            spiderResult.put(post_categories, new String[]{"Media"});
        } else if (fatherUrl.endsWith("manufacturing/")) {
            spiderResult.put(post_categories, new String[]{"Manufacturing"});
        }
        return spiderResult;
    }

    @Override
    public Object[] jsoupProcess(Object[] jsoupResult, JsoupExpress jsoupExpress) {
        if (spiderAnalyse.isEqu(jsoupExpress, postTimeExpress)) {
            try {
                Object[] objects = new Object[jsoupResult.length];
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.ENGLISH);
                for (int i = 0; i < jsoupResult.length; i++) {
                    String val = String.valueOf(jsoupResult[i]);
                    objects[i] = sdf.parse(val);
                }
                return objects;
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } else if (spiderAnalyse.isEqu(jsoupExpress, postContentExpress)) {
            Object[] objects = new Object[jsoupResult.length];
            int i = 0;
            for (Object object : jsoupResult) {
                Document document = Jsoup.parse((String) object);
                Elements elements = document.body().select("a");
                for (Element element : elements) {
                    element.replaceWith(new TextNode(element.text(), ""));
                }
                objects[i] = document.toString();
            }
            return objects;
        } else if (spiderAnalyse.isEqu(jsoupExpress, userLoginExpress)) {
            Object[] objects = new Object[jsoupResult.length];
            int i = 0;
            for (Object object : jsoupResult) {
                String username = (String) object;
                username = username.replace("By ", "");
                username = username.replace(" and", ",");
                objects[i] = username + "@" + websiteName;
            }
            return objects;
        }
        return jsoupResult;
    }

    @Override
    public void finalProcess(SpiderResult spiderResult, SpiderStrategy spiderStrategy) {
        if (!spiderResult.isRead()) {
            List<Wp_posts> wp_posts = (List<Wp_posts>) spiderAnalyse.entityMapping(Wp_posts.class, spiderResult, this);
            List<Wp_postmeta> wp_postmetas = (List<Wp_postmeta>) spiderAnalyse.entityMapping(Wp_postmeta.class, spiderResult, this);
            List<Wp_users> wp_userses = (List<Wp_users>) spiderAnalyse.entityMapping(Wp_users.class, spiderResult, this);
            wp_posts.get(0).setGuid(spiderResult.getUrl());

            Object[] tags = spiderResult.getSpiderResult().get(post_tags);
            String tagString = (String)tags[0];

            List<String> allTags = null;

            if(!tagString.equals("")){
                tagString += ("," + websiteName);
                allTags = Lists.newArrayList(tagString.split(","));
            }

            Object[] categories = spiderResult.getSpiderResult().get(post_categories);

            List<String> allCategories = Lists.newArrayList();
            for (int i = 0; i < categories.length; i++) {
                allCategories.add((String)categories[i]);
            }

            iInsertPost.insertPost(wp_posts.get(0), wp_postmetas, spiderAnalyse.generateDefaultTerms(allCategories), spiderAnalyse.generateDefaultTerms(allTags), wp_userses.get(0), null);            spiderResult.setRead(true);
            spiderResult.setRead(true);
            spiderEvent.saveOrUpdate(spiderResult);
        }
    }

    @Override
    public String getIdentity() {
        return classify;
    }
}
