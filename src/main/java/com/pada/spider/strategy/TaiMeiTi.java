package com.pada.spider.strategy;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pada.common.ReflectUtil;
import com.pada.mydao.bean.Wp_postmeta;
import com.pada.mydao.bean.Wp_posts;
import com.pada.mydao.bean.Wp_usermeta;
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
import com.pada.spider.tool.SpiderStarter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javassist.bytecode.analysis.Analyzer;


/**
 * Created by sinceow on 2014/11/1.
 */
@Component
public class TaiMeiTi implements SpiderProcessor {

	final String classify = "taimeiti";

	String domain = "http://www.tmtpost.com/";

	String startUrl = "http://www.tmtpost.com/";

	@Autowired
	private SpiderEvent spiderEvent;

	@Autowired
	private SpiderStarter spiderStarter;
	@Autowired
	private SpiderAnalyse spiderAnalyse;
	
	@SpiderKeyInject(clazz=Wp_posts.class, mappingValue = "post_date")
	private String post_time = "post_time";

    @SpiderKeyInject(clazz=Wp_posts.class, mappingValue = "post_title" )
    private String post_title = "post_title";


    @SpiderKeyInject(clazz=Wp_posts.class,mappingValue = "post_excerpt" )
    private String post_excerpt = "post_excerpt";

    @SpiderKeyInject(clazz=Wp_posts.class,mappingValue = "post_content" )
    private String post_content = "post_content";

    @SpiderKeyInject(clazz=Wp_postmeta.class, mappingKey = "meta_key", mappingValue = "meta_value", sign = 0)
    private String post_mata_comment_count = "post_mata_comment_count";



    @Autowired
    IInsertPost iInsertPost;

	public void init() throws NoSuchFieldException {

		SpiderStrategy spiderStrategy = spiderEvent
				.getSpiderStrategyByClassify(classify);

		if (spiderStrategy == null)
			spiderStrategy = new SpiderStrategy(classify);

		SpiderPath[] spiderPaths = new SpiderPath[2];

		SpiderPath spiderPath1 = new SpiderPath();
		spiderPath1.setDepth(1);
		spiderPath1.setAreaForNextPathJsoupExpresses(new JsoupExpress[] { new JsoupExpress(
						"li.article", null, null, true) });
		spiderPath1.setNextPathRegexs(new String[] { "http://www.tmtpost.com/\\d+.html" });
		Map<String, JsoupExpress> infos = Maps.newHashMap();
		infos.put(post_title, new JsoupExpress("a.tit", null, null, false));

		spiderPath1.setPerNextUrlInfoCaptures(infos);

		SpiderPath spiderPath2 = new SpiderPath();
		spiderPath2.setDepth(2);
		spiderPath2.setTarget(true);
		Map<String, JsoupExpress> captures = Maps.newHashMap();
		captures.put(post_time, new JsoupExpress("#pubtime_baidu a",
				null, null, false));
		/*
		 * captures.put("author", new JsoupExpress("#author_baidu a", null,
		 * null, false));
		 */
		captures.put(post_excerpt, new JsoupExpress("div.point", null, null,
				false));

		captures.put("tags", new JsoupExpress("div.tags-top a.tag", null, null, false));

		captures.put(post_content, new JsoupExpress("div.paragraph", null,
				null, true));
		captures.put(post_mata_comment_count, new JsoupExpress(
				"span.comment  a", null, null, false));

		spiderPath2.setWebInfoCaptures(captures);

		spiderPaths[0] = spiderPath1;
		spiderPaths[1] = spiderPath2;

		spiderStrategy.setDomain(domain).setClassify(classify)
				.setStartUrl(startUrl).setSpiderPaths(spiderPaths);

		spiderEvent.saveOrUpdate(spiderStrategy);

	}


	@Override
	public String getIdentity() {
		return classify;
	}

	@Override
	public Map<String, Object[]> resultProcess(Map<String, Object[]> spiderResult, String spiderUrlId) {
		Map<String,Object[]> infos = spiderAnalyse.analyseOldNextCaptureInfos(spiderUrlId, 1);
        spiderResult.put(post_title, infos.get(post_title));
		return spiderResult;
	}

	@Override
	public Object[] jsoupProcess(Object[] jsoupResult, JsoupExpress jsoupExpress) {
		if(spiderAnalyse.isEqu(jsoupExpress, new JsoupExpress("#pubtime_baidu a",
				null, null, false))){
			try {
				Object[] objects = new Object[jsoupResult.length];
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				int i = 0;
				for(Object object : jsoupResult){
					String val = String.valueOf(object);
					System.out.println(val);
					objects[i] = sdf.parse(val);
					i++;
				}
				return objects;
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}else if(spiderAnalyse.isEqu(jsoupExpress,new JsoupExpress("div.paragraph", null, null, true))){
            Object[] objects = new Object[jsoupResult.length];
            int i = 0;
            for(Object object: jsoupResult){
                Document document = Jsoup.parse((String) object);
                Elements elements = document.body().select("a");
                for(Element element: elements){
                    element.replaceWith(new TextNode(element.text(), ""));
                }
                objects[i] = document.toString();
            }
            return objects;
        }
		return jsoupResult;
	}

    @Override
    public void finalProcess(SpiderResult spiderResult, SpiderStrategy spiderStrategy) {
        if(!spiderResult.isRead()){
            List<Wp_posts> wp_posts = (List<Wp_posts>) spiderAnalyse.entityMapping(Wp_posts.class, spiderResult, this);
            List<Wp_postmeta> wp_postmetas = (List<Wp_postmeta>) spiderAnalyse.entityMapping(Wp_postmeta.class, spiderResult, this);
            wp_posts.get(0).setGuid(spiderResult.getUrl());
            iInsertPost.insertPost(wp_posts.get(0),null, null, spiderAnalyse.generateDefaultTerms(Lists.newArrayList(spiderResult.getSpiderResult().get("tags"))),null,null);
            spiderResult.setRead(true);
            spiderEvent.saveOrUpdate(spiderResult);
        }
    }

}
