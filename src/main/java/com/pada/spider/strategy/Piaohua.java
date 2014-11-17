package com.pada.spider.strategy;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pada.common.ArrUtil;
import com.pada.common.RegexUtil;
import com.pada.mydao.bean.Wp_postmeta;
import com.pada.mydao.bean.Wp_posts;
import com.pada.mydao.bean.Wp_terms;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by eqyun on 2014/11/1.
 */
@Component
public class Piaohua implements SpiderProcessor{


    final String classify ="piaohua";

    String domain = "http://www.piaohua.com/";

    String startUrl = "http://www.piaohua.com/html/zuixindianying.html";

    @Autowired
    private SpiderEvent spiderEvent;

    @Autowired
    private SpiderStarter spiderStarter;

    @SpiderKeyInject(clazz=Wp_posts.class,mappingValue = "post_title" )
    private String title = "title";

    @SpiderKeyInject(clazz=Wp_posts.class,mappingValue = "post_content" )
    private String content = "content";

    @SpiderKeyInject(clazz= Wp_postmeta.class,mappingKey = "meta_key",mappingValue = "meta_value",sign = 0)
    private String downLinks = "_meta_download_link_";

    @SpiderKeyInject(clazz= Wp_postmeta.class,mappingKey = "meta_key",mappingValue = "meta_value",sign = 1)
    private String postThumbnail = "_meta_post_thumbnail";

    @Autowired
    private SpiderAnalyse spiderAnalyse;

    private String categories ="category";
    
    @Autowired
    private IInsertPost insertPost;


    private JsoupExpress linkJsoupExpress = new JsoupExpress("html body div#main div#im div#nml div#show div#showinfo table tbody tr td a","href",null,false);

    private JsoupExpress postThumbnailExpress = new JsoupExpress("html body div#main div#im div#nml div#show div#showdesc img.img","src",null,false);



    public void init(){

        SpiderStrategy spiderStrategy =  spiderEvent.getSpiderStrategyByClassify(classify);

        if(spiderStrategy == null)
            spiderStrategy = new SpiderStrategy(classify);





       /* SpiderPath spiderPath1 = new SpiderPath(1);
        spiderPath1.setAreaForNextPathJsoupExpresses(new JsoupExpress[]{new JsoupExpress("div#menu",null,null,true)});
        spiderPath1.setNextPathRegexs(new String[]{"http://www.piaohua.com/html/[^/]+/index.html"});



        SpiderPath spiderPath2 = new SpiderPath(2);
        spiderPath2.setAreaForNextPathJsoupExpresses(new JsoupExpress[]{new JsoupExpress("div#nml div.tk",null,null,true)});
        spiderPath2.setNextPathRegexs(new String[]{"http://www.piaohua.com/html/[^/]+/list_\\d+.html"});
*/


        SpiderPath spiderPath1 = new SpiderPath(1);
        spiderPath1.setCaptureSameLevel(true);
        spiderPath1.setAreaForNextPathJsoupExpresses(new JsoupExpress[]{new JsoupExpress("div#im",null,null,true)});
        spiderPath1.setNextPathRegexs(new String[]{"http://www.piaohua.com/html/[^/]+/\\d+/\\d+/\\d+.html"});


        SpiderPath spiderPath2 = new SpiderPath();
        spiderPath2.setDepth(2);
        spiderPath2.setTarget(true);
        Map<String,JsoupExpress> webInfoCaptures = Maps.newHashMap();
        webInfoCaptures.put(title,new JsoupExpress("div#main div#im div#nml div#show h3",null,null,false));
        webInfoCaptures.put(content,new JsoupExpress("div#showinfo",null,"strong,table",true, true));
        webInfoCaptures.put(postThumbnail, postThumbnailExpress);

        webInfoCaptures.put(downLinks,linkJsoupExpress);

        spiderPath2.setWebInfoCaptures(webInfoCaptures);



        SpiderPath[] spiderPaths = new SpiderPath[]{spiderPath1,spiderPath2};

        spiderStrategy.setDomain(domain).setStartUrl(startUrl).setSpiderPaths(spiderPaths);

        spiderEvent.saveOrUpdate(spiderStrategy);

    }




    @Override
    public Map<String, Object[]> resultProcess(Map<String, Object[]> spiderResult, String spiderUrlId) {

        //SpiderUrlInfo spiderUrlInfo = spiderAnalyse.analyseOldSpiderUrlInfo(spiderUrlId,2);
        SpiderUrlInfo spiderUrlInfo = spiderEvent.getSpiderUrlInfoById(spiderUrlId);

        String url = spiderUrlInfo.getUrl();
        String regex = "http://www.piaohua.com/html/([^/]+)/\\d+/\\d+/\\d+.html";
        try {
            List<String> finds = RegexUtil.searchMatch(url,regex,1);
            if(finds == null || finds.size()==0)
                return null;
            String _category = finds.get(0);
            spiderResult.put(categories,new Object[]{_category});
        }catch (Exception e){
            return null;
        }
        return spiderResult;
    }

    @Override
    public Object[] jsoupProcess(Object[] jsoupResult, JsoupExpress jsoupExpress) {

        if(spiderAnalyse.isEqu(jsoupExpress,linkJsoupExpress)){
            if(jsoupResult != null) {
                StringBuffer stringBuffer = new StringBuffer("");
                for(Object _result : jsoupResult){
                    if(stringBuffer.length()>0) stringBuffer.append("::");
                    stringBuffer.append((String)_result);

                }
                if(stringBuffer.toString().trim().length() == 0){
                    return null;
                }
                return new Object[]{stringBuffer.toString()};
            }
        }else if(spiderAnalyse.isEqu(jsoupExpress,postThumbnailExpress)){
            if(jsoupResult != null) {
                for(Object _result : jsoupResult){
                    String url = (String)_result;
                    if(url.contains("defaultpic")){
                        return null;
                    }
                }
            }
        }

        return jsoupResult;
    }

    @Override
    public String getIdentity() {
        return classify;
    }




	@Override
	public void finalProcess(SpiderResult spiderResult,
			SpiderStrategy spiderStrategy) {
		if(!spiderResult.isRead()){
			List<Wp_posts> wp_posts = (List<Wp_posts>) spiderAnalyse.entityMapping(Wp_posts.class, spiderResult, this);

            wp_posts.get(0).setGuid(spiderResult.getUrl());

            List<Wp_postmeta> wp_postmetas = (List<Wp_postmeta>) spiderAnalyse.entityMapping(Wp_postmeta.class,spiderResult,this);

            Map<String,Object[]> result = spiderResult.getSpiderResult();

            Object[] categoryObjects = result.get(categories);
            List<Wp_terms> categories = Lists.newArrayList();
            if(categoryObjects!=null) {
                for (Object cat : categoryObjects) {

                    String _category = (String)cat;
                    Wp_terms category = null;
                    switch (_category){
                        case "dongzuo":category = new Wp_terms("动作片","dongzuo");
                            break;
                        case "xiju":category = new Wp_terms("喜剧片","xiju");
                            break;
                        case "aiqing":category = new Wp_terms("爱情片","aiqing");
                            break;
                        case "kehuan": category = new Wp_terms("科幻片","kehuan");
                            break;
                        case "juqing": category = new Wp_terms("剧情片","juqing");
                            break;
                        case "xuannian":category = new Wp_terms("悬疑片","xuanyi");
                            break;
                        case "wenyi": category = new Wp_terms("文艺片","wenyi");
                            break;
                        case "zhanzheng": category = new Wp_terms("战争片","zhanzheng");
                            break;
                        case "kongbu" : category =new Wp_terms("恐怖片","kongbu");
                            break;
                        case "zainan": category = new Wp_terms("灾难片","zainan");
                            break;
                        case "lianxuju" : category =  new Wp_terms("连续剧","lianxuju");
                            break;
                        case "dongman" : category = new Wp_terms("动漫","dongman");
                            break;
                        case "huaijiu":category = new Wp_terms("怀旧片","huaijiu");
                            break;
                        case "zongyijiemu": category = new Wp_terms("综艺片","zongyi");
                            break;
                        default:category=null;
                    }

                    if(category!=null){
                        categories.add(category);
                    }
                }
            }

            insertPost.insertPost(wp_posts.get(0),wp_postmetas,categories,null,null,null);
			spiderResult.setRead(true);
			spiderEvent.saveOrUpdate(spiderResult);
		}
	}
}
