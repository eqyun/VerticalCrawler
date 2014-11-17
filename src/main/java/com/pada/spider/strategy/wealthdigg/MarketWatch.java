package com.pada.spider.strategy.wealthdigg;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pada.mydao.bean.Wp_postmeta;
import com.pada.mydao.bean.Wp_posts;
import com.pada.mydao.bean.Wp_terms;
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
public class MarketWatch implements SpiderProcessor {


    final String classify = "marketwatch";

    String websiteName = "MarketWatch";

    String domain = "http://www.marketwatch.com/";

    String[] startUrl = new String[]{

            "http://feeds.marketwatch.com/marketwatch/topstories/",
            "http://feeds.marketwatch.com/marketwatch/realtimeheadlines/",
            "http://feeds.marketwatch.com/marketwatch/marketpulse/",
            "http://feeds.marketwatch.com/marketwatch/bulletins",
            "http://feeds.marketwatch.com/marketwatch/pf/",
            "http://feeds.marketwatch.com/marketwatch/StockstoWatch/",
            "http://feeds.marketwatch.com/marketwatch/internet/",
            "http://feeds.marketwatch.com/marketwatch/mutualfunds/",
            "http://feeds.marketwatch.com/marketwatch/software/",
            "http://feeds.marketwatch.com/marketwatch/financial/",
            "http://feeds.marketwatch.com/marketwatch/commentary/",
            "http://feeds.marketwatch.com/marketwatch/newslettersandresearch/",
            "http://feeds.marketwatch.com/marketwatch/Autoreviews/"
    };

    @Autowired
    SpiderEvent spiderEvent;

    @Autowired
    SpiderAnalyse spiderAnalyse;

    @Autowired
    IInsertPost iInsertPost;

   /* @SpiderKeyInject(clazz = Wp_postmeta.class, mappingKey = "meta_key", mappingValue = "meta_value", sign = 0)
    private String meta_score = "_meta_score";*/

    @SpiderKeyInject(clazz = Wp_postmeta.class, mappingKey = "meta_key", mappingValue = "meta_value", sign = 0)
    private String meta_thumbnail = "_meta_thumbnail";
    JsoupExpress metaThumbnailExpress = new JsoupExpress("meta[property=og:image]", "content", null, false);

    @SpiderKeyInject(clazz = Wp_postmeta.class, mappingKey = "meta_key", mappingValue = "meta_value", sign = 0)
    private String meta_related = "_meta_related_post";
    JsoupExpress metaRelatedExpress = new JsoupExpress("#related-articles ul li a", "href", null, false);

    @SpiderKeyInject(clazz = Wp_posts.class, mappingValue = "post_date")
    private String post_time = "post_time";
    JsoupExpress postTimeExpress = new JsoupExpress("pubdate", null, "span", false);

    @SpiderKeyInject(clazz = Wp_posts.class, mappingValue = "post_title")
    private String post_title = "post_title";
    JsoupExpress postTitleExpress = new JsoupExpress("title", null, null, false);

    @SpiderKeyInject(clazz = Wp_posts.class, mappingValue = "post_excerpt")
    private String post_excerpt = "post_excerpt";
    JsoupExpress postExcerptExpress = new JsoupExpress("meta[name=description]", "content", null, false);

    @SpiderKeyInject(clazz = Wp_posts.class, mappingValue = "post_content")
    private String post_content = "post_content";
    JsoupExpress postContentExpress = new JsoupExpress("#article-body", null, "#article-pagination", true, true);

    @SpiderKeyInject(clazz = Wp_users.class, mappingValue = "user_login")
    private String user_login = "user_login";
    JsoupExpress userLoginExpress = new JsoupExpress("meta[name=author]", "content", null, false);

    private String post_tags = "tags";
    JsoupExpress tagsExpress = new JsoupExpress("#article-instruments  ul li a", null, null, false);

    private String post_categories = "categories";

    String articleDetailRegex = "http://www.marketwatch.com/news/story.asp.+";
    JsoupExpress articleBlockExpress = new JsoupExpress("item", null, null, true);


    public void init() throws Exception {
        SpiderStrategy spiderStrategy = spiderEvent.getSpiderStrategyByClassify(classify);
        if (spiderStrategy == null) {
            spiderStrategy = new SpiderStrategy(classify);
        }

        spiderStrategy.setStartUrl(startUrl);


        SpiderPath spiderPath1 = new SpiderPath(1);
        spiderPath1.setRss(true);
        spiderPath1.setNextPathRegexs(new String[]{articleDetailRegex});

        spiderPath1.setAreaForNextPathJsoupExpresses(new JsoupExpress[]{articleBlockExpress});


        Map<String, JsoupExpress> infos = Maps.newHashMap();
        infos.put(post_title, postTitleExpress);
        infos.put(post_time, postTimeExpress);

        spiderPath1.setPerNextUrlInfoCaptures(infos);


        SpiderPath spiderPath2 = new SpiderPath(2);
        spiderPath2.setTarget(true);

        Map<String, JsoupExpress> captures = Maps.newHashMap();
        captures.put(meta_thumbnail, metaThumbnailExpress);
        captures.put(post_content, postContentExpress);
        captures.put(user_login, userLoginExpress);
        captures.put(post_tags, tagsExpress);
        captures.put(post_excerpt, postExcerptExpress);
        captures.put(meta_related, metaRelatedExpress);
        spiderPath2.setWebInfoCaptures(captures);

        SpiderPath[] spiderPaths = new SpiderPath[]{spiderPath1, spiderPath2};

        spiderStrategy.setSpiderPaths(spiderPaths).setProxyIP("127.0.0.1").setProxyPort(8580);

        spiderEvent.saveOrUpdate(spiderStrategy);

    }

    @Override
    public Map<String, Object[]> resultProcess(Map<String, Object[]> spiderResult, String spiderUrlId) {
        SpiderUrlInfo spiderUrlInfo = spiderAnalyse.analyseOldSpiderUrlInfo(spiderUrlId, 1);
        String fatherUrl = spiderUrlInfo.getUrl();
        if(fatherUrl.contains("topstories")){
            spiderResult.put(post_categories, new String[]{"Top"});
        }else if(fatherUrl.contains("realtimeheadlines")){
            spiderResult.put(post_categories, new String[]{"Headline"});
        }else if(fatherUrl.contains("marketpulse")){
            spiderResult.put(post_categories, new String[]{"Markets"});
        }else if(fatherUrl.contains("bulletins")){
            spiderResult.put(post_categories, new String[]{"Headline"});
        }else if(fatherUrl.contains("pf")){
            spiderResult.put(post_categories, new String[]{"Personal Finance"});
        }else if(fatherUrl.contains("StockstoWatch")){
            spiderResult.put(post_categories, new String[]{"Stocks"});
        }else if(fatherUrl.contains("internet")){
            spiderResult.put(post_categories, new String[]{"Stocks"});
        }else if(fatherUrl.contains("mutualfunds")){
            spiderResult.put(post_categories, new String[]{"Mutual Funds", "Funds"});
        }else if(fatherUrl.contains("software")){
            spiderResult.put(post_categories, new String[]{"Business"});
        }else if(fatherUrl.contains("financial")){
            spiderResult.put(post_categories, new String[]{"Business", "Banking"});
        }else if(fatherUrl.contains("commentary")){
            spiderResult.put(post_categories, new String[]{"Markets"});
        }else if(fatherUrl.contains("newslettersandresearch")){
            spiderResult.put(post_categories, new String[]{"Markets"});
        }else if(fatherUrl.contains("Autoreviews")){
            spiderResult.put(post_categories, new String[]{"Markets"});
        }
        Map<String, Object[]> infos = spiderAnalyse.analyseOldNextCaptureInfos(spiderUrlId, 1);
        spiderResult.put(post_title, infos.get(post_title));
        spiderResult.put(post_time, infos.get(post_time));
        return spiderResult;
    }

    @Override
    public Object[] jsoupProcess(Object[] jsoupResult, JsoupExpress jsoupExpress) {
        if (spiderAnalyse.isEqu(jsoupExpress, postTimeExpress)) {
            try {
                Object[] objects = new Object[jsoupResult.length];
                SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH);
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
                objects[i] = (String) object + "@" + websiteName;
            }
            return objects;
        }else if(spiderAnalyse.isEqu(jsoupExpress, metaRelatedExpress)){
            Object[] objects = new Object[1];
            String url = "";
            int i = 0;
            for (Object object : jsoupResult) {
                if(url.length() > 0){
                    url += "::";
                }
                url += (String) object;
            }
            objects[0] = url;
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

            List<Object> objects = Lists.newArrayList(tags);
            objects.add(websiteName);

            List<String> allTags = Lists.newArrayList();
            for (int i = 0; i < objects.size(); i++) {
                allTags.add(((String) objects.get(i)).trim());
            }

            Object[] categories = spiderResult.getSpiderResult().get(post_categories);
            List<String> allCategories = Lists.newArrayList();

            for (int i = 0; i < categories.length; i++) {
                allCategories.add(((String) categories[i]).trim());
            }

            iInsertPost.insertPost(wp_posts.get(0), wp_postmetas, spiderAnalyse.generateDefaultTerms(Lists.newArrayList(allCategories)), spiderAnalyse.generateDefaultTerms(Lists.newArrayList(allTags)), wp_userses.get(0), null);            spiderResult.setRead(true);
            spiderResult.setRead(true);
            spiderEvent.saveOrUpdate(spiderResult);
        }
    }

    @Override
    public String getIdentity() {
        return classify;
    }
}
