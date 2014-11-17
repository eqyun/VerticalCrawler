package com.pada.spider.tool.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pada.common.FileOperator;
import com.pada.mydao.bean.Wp_terms;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pada.common.ArrUtil;
import com.pada.common.ReflectUtil;
import com.pada.spider.annotation.SpiderKeyInject;
import com.pada.spider.bean.JsoupExpress;
import com.pada.spider.bean.SpiderPath;
import com.pada.spider.domain.SpiderResult;
import com.pada.spider.domain.SpiderStrategy;
import com.pada.spider.domain.SpiderUrlInfo;
import com.pada.spider.event.SpiderEvent;
import com.pada.spider.event.SpiderProcessor;
import com.pada.spider.tool.SpiderAnalyse;
import org.springframework.web.util.HtmlUtils;

/**
 * Created by eqyun on 2014/10/31.
 */
@Component
public class SpiderAnalyseImpl implements SpiderAnalyse {
    @Autowired
    private SpiderEvent spiderEvent;

    @Override
    public SpiderPath getSpiderPath(SpiderStrategy spiderStrategy, int depth) {
        SpiderPath[] spiderPaths = spiderStrategy.getSpiderPaths();
        for (SpiderPath spiderPath : spiderPaths) {
            if (spiderPath.getDepth() == depth)
                return spiderPath;
        }
        return null;
    }

    @Override
    public SpiderPath getNextSpiderPath(SpiderStrategy spiderStrategy,
                                        int nowDepth) {
        SpiderPath[] spiderPaths = spiderStrategy.getSpiderPaths();
        for (SpiderPath spiderPath : spiderPaths) {
            if (spiderPath.getDepth() == nowDepth + 1)
                return spiderPath;
        }
        return null;
    }

    @Override
    public SpiderPath getPreSpiderPath(SpiderStrategy spiderStrategy,
                                       int nowDepth) {
        SpiderPath[] spiderPaths = spiderStrategy.getSpiderPaths();
        for (SpiderPath spiderPath : spiderPaths) {
            if (spiderPath.getDepth() == nowDepth - 1)
                return spiderPath;
        }
        return null;
    }

    @Override
    public String[] fetchJsoup(JsoupExpress jsoupExpress, Document body_copy) {

        String seekDom = jsoupExpress.getSeekDom();
        String filterDom = jsoupExpress.getFilterDom();
        String selectAttr = jsoupExpress.getSelectAttr();
        if (filterDom != null)
            body_copy.select(filterDom).remove();

        if (jsoupExpress.isRemoveStyle()) {
            body_copy.select("*").removeAttr("style");
            body_copy.select("*").removeAttr("onclick");
            body_copy.select("*").removeAttr("onfocus");
        }

        Elements elements = body_copy.select(seekDom);

        List<String> values = Lists.newArrayList();


        for (Element element : elements) {
            if (selectAttr != null) {
                String value = element.attr(selectAttr);
                values.add(value);
            } else {
                if (jsoupExpress.isHtml())
                    values.add(element.html());
                else
                    values.add(element.text());
            }
        }
        return values.toArray(new String[values.size()]);
    }

    @Override
    public String[] fetchJsoup(JsoupExpress jsoupExpress, String html) {
        Document document = Jsoup.parse(html);
        return this.fetchJsoup(jsoupExpress, document);
    }

    @Override
    public List<String> fetchUrl(String html) {

        JsoupExpress jsoupExpress = new JsoupExpress("a", "href", null, true);

        String[] urls = fetchJsoup(jsoupExpress, html);
        return Lists.newArrayList(urls);
    }

    @Override
    public String[][] fetchUrlAndLinkDescription(String html, boolean isRss) {
        if (!isRss) {
            Document document = Jsoup.parse(html);
            Elements elements = document.select("a");

            String[][] descriptionAndLinks = new String[elements.size()][2];

            int i = 0;

            for (Element element : elements) {
                descriptionAndLinks[i][0] = element.text();
                descriptionAndLinks[i][1] = element.attr("href");
                i++;
            }

            return descriptionAndLinks;
        } else {
            List<String[]> _links = Lists.newArrayList();
            String regex = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
            Pattern pattern = Pattern.compile(regex);
            html = HtmlUtils.htmlUnescape(html);
            Matcher matcher = pattern.matcher(html);
            while (matcher.find()) {
                String[] _link = new String[]{"", matcher.group()};
                _links.add(_link);
            }
            String[][] descriptionAndLinks = new String[_links.size()][2];
            for (int i = 0; i < _links.size(); i++) {
                descriptionAndLinks[i] = _links.get(i);
            }
            return descriptionAndLinks;
        }
    }

    @Override
    public List<SpiderUrlInfo> getSpiderUrlInfoOldFamily(
            SpiderUrlInfo spiderUrlInfo) {
        List<SpiderUrlInfo> spiderUrlInfos = Lists.newArrayList();
        String spiderUriInfoParentId = spiderUrlInfo.getParentId();
        for (; ; ) {

            if (spiderUriInfoParentId.equals("0"))
                break;
            SpiderUrlInfo parentUrlInfo = spiderEvent
                    .getSpiderUrlInfoById(spiderUriInfoParentId);

            if (parentUrlInfo == null)
                break;
            spiderUrlInfos.add(parentUrlInfo);

            spiderUriInfoParentId = parentUrlInfo.getParentId();

        }

        return spiderUrlInfos;
    }

    @Override
    public boolean isEqu(JsoupExpress je1, JsoupExpress je2) {
        String[] js1 = new String[]{je1.getFilterDom(), je1.getSeekDom(),
                je1.getSelectAttr(), String.valueOf(je1.isHtml()),
                String.valueOf(je1.isRemoveStyle())};
        String[] js2 = new String[]{je2.getFilterDom(), je2.getSeekDom(),
                je2.getSelectAttr(), String.valueOf(je2.isHtml()),
                String.valueOf(je2.isRemoveStyle())};
        return ArrUtil.isEqu(js1, js2);
    }

    @Override
    public List<?> entityMapping(Class<?> entityType,
                                 SpiderResult spiderResult, SpiderProcessor spiderProcessorStrategy) {

        try {

            Map<Integer, Object> objectCache = Maps.newHashMap();

            Map<String, Object[]> spiderResults = spiderResult
                    .getSpiderResult();

            Field[] fields = spiderProcessorStrategy.getClass().getDeclaredFields();


            for (Field field : fields) {
                Annotation annotation = field
                        .getAnnotation(SpiderKeyInject.class);
                if (annotation != null) {

                    Class<?> clazz = ((SpiderKeyInject) annotation).clazz();
                    int sign = ((SpiderKeyInject) annotation).sign();

                    if (clazz.equals(entityType)) {

                        if (!objectCache.containsKey(sign)) {
                            objectCache.put(sign, Class.forName(entityType.getName()).newInstance());
                        }
                        Object object = objectCache.get(sign);


                        String key = ReflectUtil.getFieldValue(spiderProcessorStrategy,
                                field.getName());

                        Object value = null;

                        if ((spiderResults.get(key) == null || spiderResults.get(key).length == 0)) {
                            if (((SpiderKeyInject) annotation).isNecessity()) {
                                throw new Exception(key + " is not capture in the url:" + spiderResult.getUrl());
                            } else
                                value = null;
                        } else
                            value = spiderResults.get(key)[0];

                        String keyMapping = ((SpiderKeyInject) annotation).mappingKey();
                        String valueMapping = ((SpiderKeyInject) annotation).mappingValue();

                        if (keyMapping.length() > 0) {
                            ReflectUtil.setFieldValue(object, keyMapping, key);
                        }
                        if (valueMapping.length() > 0) {
                            ReflectUtil.setFieldValue(object, valueMapping, value);
                        }
                    }
                }
            }
            return Lists.newArrayList(objectCache.values());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    @Override
    public Map<String, Object[]> analyseOldNextCaptureInfos(String spiderUrlInfoId, int howOld) {

        SpiderUrlInfo spiderUrlInfo = spiderEvent.getSpiderUrlInfoById(spiderUrlInfoId);

        List<SpiderUrlInfo> oldFamily = getSpiderUrlInfoOldFamily(spiderUrlInfo);

        if (oldFamily != null && oldFamily.size() > 0) {

            int size = howOld - 1;

            SpiderUrlInfo spInfo = oldFamily.get(size);
            Map<String, Map<String, Object[]>> result = spInfo.getNextCaptureInfos();

            return result.get(spiderUrlInfo.getUrl());

        }
        return null;
    }

    @Override
    public Map<String, Object[]> analyseOldWebCaptureInfos(
            String spiderUrlInfoId, int howOld) {
        SpiderUrlInfo spiderUrlInfo = spiderEvent.getSpiderUrlInfoById(spiderUrlInfoId);

        List<SpiderUrlInfo> oldFamily = getSpiderUrlInfoOldFamily(spiderUrlInfo);

        if (oldFamily != null && oldFamily.size() > 0) {
            int size = howOld - 1;

            SpiderUrlInfo spInfo = oldFamily.get(size);
            Map<String, Object[]> result = spInfo.getCaptureInfos();
            return result;
        }
        return null;
    }

    @Override
    public SpiderUrlInfo analyseOldSpiderUrlInfo(String spiderUrlInfoId, int howOld) {
        SpiderUrlInfo spiderUrlInfo = spiderEvent.getSpiderUrlInfoById(spiderUrlInfoId);

        List<SpiderUrlInfo> oldFamily = getSpiderUrlInfoOldFamily(spiderUrlInfo);

        if (oldFamily != null && oldFamily.size() > 0) {
            int size = howOld - 1;

            SpiderUrlInfo spInfo = oldFamily.get(size);
            return spInfo;
        }
        return null;
    }

    @Override
    public void completeImageUrl(Document document, String domain) {
        Elements elements = document.select("img");
        if (elements != null && elements.size() > 0)
            for (Element element : elements) {
                String src = element.attr("src");
                if (src.startsWith("/")) {
                    element.attr("src", domain + src);
                }
            }
    }

    @Override
    public List<Wp_terms> generateDefaultTerms(Collection catOrTags) {
        if (catOrTags == null)
            return null;
        List<Wp_terms> wp_terms = Lists.newArrayList();
        for (Object catOrTag : catOrTags) {
            wp_terms.add(new Wp_terms((String) catOrTag, null));
        }
        return wp_terms;
    }

}
