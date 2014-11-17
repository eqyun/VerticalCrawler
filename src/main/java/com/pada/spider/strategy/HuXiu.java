package com.pada.spider.strategy;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pada.common.ArrUtil;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * Created by sinceow on 2014/11/4.
 */
@Component
public class HuXiu implements SpiderProcessor {


    final String classify = "huxiu";

    String websiteName = "虎嗅";

    String domain = "http://www.huxiu.com/";

    String startUrl = "http://www.huxiu.com/focus/";

    @Autowired
    SpiderEvent spiderEvent;

    @Autowired
    SpiderAnalyse spiderAnalyse;

    @Autowired
    IInsertPost iInsertPost;


    @SpiderKeyInject(clazz = Wp_postmeta.class, mappingKey = "meta_key", mappingValue = "meta_value", sign = 0)
    private String meta_post_thumbnail = "_meta_post_thumbnail";
    JsoupExpress postThumbnailExpress = new JsoupExpress("a.a-img img", "src", null, false);

    @SpiderKeyInject(clazz = Wp_postmeta.class, mappingKey = "meta_key", mappingValue = "meta_value", sign = 1)
    private String meta_comment_count = "_meta_comment_count";
    JsoupExpress commentCountExpress = new JsoupExpress("div.neirong-other a.pinglun-num em.pl-num", null, null, false);


   /* @SpiderKeyInject(clazz = Wp_postmeta.class, mappingKey = "meta_key", mappingValue = "meta_value", sign = 0)
    private String meta_score = "_meta_score";*/

    @SpiderKeyInject(clazz = Wp_posts.class, mappingValue = "post_date")
    private String post_time = "post_time";
    JsoupExpress postTimeExpress = new JsoupExpress("#pubtime_baidu", null, null, false);

    @SpiderKeyInject(clazz = Wp_posts.class, mappingValue = "post_title")
    private String post_title = "post_title";
    JsoupExpress postTitleExpress = new JsoupExpress("div.neirong h1", null, null, false);

    @SpiderKeyInject(clazz = Wp_posts.class, mappingValue = "post_excerpt")
    private String post_excerpt = "post_excerpt";
    JsoupExpress postExcerptExpress = new JsoupExpress("div.article-box-ctt div.article-summary", null, null, false);

    @SpiderKeyInject(clazz = Wp_posts.class, mappingValue = "post_content")
    private String post_content = "post_content";
    JsoupExpress postContentExpress = new JsoupExpress("#neirong_box table tr td", null, "div.neirong-shouquan, a.askAuthor, a.add-collect", true, true);

    @SpiderKeyInject(clazz = Wp_users.class, mappingValue = "user_login")
    private String user_login = "user_login";
    JsoupExpress userLoginExpress = new JsoupExpress("div.neirong div.neirong-other a#author_baidu", null, null, false);

    private String post_tags = "tags";
    JsoupExpress tagsExpress = new JsoupExpress("div.neirong-other span.tags a", null, null, false);

    String articleDetailRegex = "http://www.huxiu.com/article/\\d+/1.html";
    JsoupExpress articleBlockExpress = new JsoupExpress("div.article-list  div.article-box", null, null, true);

    String paginationRegex = "http://www.huxiu.com/focus/\\d+.html";
    JsoupExpress paginationExpress = new JsoupExpress("div.pages", null, null, true);


    public void init() throws NoSuchFieldException {
        SpiderStrategy spiderStrategy = spiderEvent.getSpiderStrategyByClassify(classify);
        if (spiderStrategy == null) {
            spiderStrategy = new SpiderStrategy(classify);
        }

        spiderStrategy.setStartUrl(startUrl);


        SpiderPath spiderPath1 = new SpiderPath(1);
        spiderPath1.setNextPathRegexs(new String[]{paginationRegex});


        SpiderPath spiderPath2 = new SpiderPath(2);
        spiderPath2.setAreaForNextPathJsoupExpresses(new JsoupExpress[]{articleBlockExpress, paginationExpress});

        spiderPath2.setCaptureSameLevel(true);
        spiderPath2.setNextPathRegexs(new String[]{articleDetailRegex});

        Map<String, JsoupExpress> infos = Maps.newHashMap();
        infos.put(meta_post_thumbnail, postThumbnailExpress);
        infos.put(post_excerpt, postExcerptExpress);
        spiderPath2.setPerNextUrlInfoCaptures(infos);


        SpiderPath spiderPath3 = new SpiderPath(3);
        spiderPath3.setTarget(true);

        Map<String, JsoupExpress> captures = Maps.newHashMap();

        captures.put(post_title, postTitleExpress);
        captures.put(post_content, postContentExpress);
        captures.put(post_time, postTimeExpress);
        captures.put(user_login, userLoginExpress);
        captures.put(post_tags, tagsExpress);
        captures.put(meta_comment_count, commentCountExpress);
        spiderPath3.setWebInfoCaptures(captures);

        SpiderPath[] spiderPaths = new SpiderPath[]{spiderPath1, spiderPath2, spiderPath3};
        spiderStrategy.setSpiderPaths(spiderPaths);

        spiderEvent.saveOrUpdate(spiderStrategy);

    }

    @Override
    public Map<String, Object[]> resultProcess(Map<String, Object[]> spiderResult, String spiderUrlId) {
        Map<String, Object[]> infos = spiderAnalyse.analyseOldNextCaptureInfos(spiderUrlId, 1);
        spiderResult.put(post_excerpt, infos.get(post_excerpt));
        spiderResult.put(meta_post_thumbnail, infos.get(meta_post_thumbnail));
        return spiderResult;
    }

    @Override
    public Object[] jsoupProcess(Object[] jsoupResult, JsoupExpress jsoupExpress) {
        if (spiderAnalyse.isEqu(jsoupExpress, postTimeExpress)) {
            try {
                Object[] objects = new Object[jsoupResult.length];
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                for (int i = 0; i < jsoupResult.length; i++) {
                    String val = String.valueOf(jsoupResult[i]);
                    System.out.println(val);
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


            iInsertPost.insertPost(wp_posts.get(0), wp_postmetas, null, spiderAnalyse.generateDefaultTerms(objects), wp_userses.get(0), null);
            spiderResult.setRead(true);
            spiderEvent.saveOrUpdate(spiderResult);
        }
    }

    @Override
    public String getIdentity() {
        return classify;
    }
}
