package com.pada.spider.bean;

import java.util.Map;

/**
 * Created by eqyun on 2014/10/31.
 */
public class SpiderPath {

    public SpiderPath(int depth) {
        this.depth = depth;
    }

    public SpiderPath() {
    }

    private int depth;// depth

    private boolean isTarget = false;//是不是目标页面，是的话要进行结果保存


    private String[] nextPathRegexs;//next path regexps



    private boolean isCaptureSameLevel = false;//同等级的要不要扩展出去，主要是分页


    private JsoupExpress[] areaForNextPathJsoupExpresses;//哪些区间是我们想要的，并把这些区间对应的下一层url对应起来


    private Map<String,JsoupExpress> perNextUrlInfoCaptures;//提取上面的区间的具体信息


    private Map<String,JsoupExpress> webInfoCaptures;//对整个页面我们要捉取哪些信息


    private boolean isRss =false;

    public boolean isCaptureSameLevel() {
        return isCaptureSameLevel;
    }

    public void setCaptureSameLevel(boolean isCaptureSameLevel) {
        this.isCaptureSameLevel = isCaptureSameLevel;
    }

    public String[] getNextPathRegexs() {
        return nextPathRegexs;
    }

    public void setNextPathRegexs(String[] nextPathRegexs) {
        this.nextPathRegexs = nextPathRegexs;
    }

    public Map<String, JsoupExpress> getWebInfoCaptures() {
        return webInfoCaptures;
    }

    public void setWebInfoCaptures(Map<String, JsoupExpress> webInfoCaptures) {
        this.webInfoCaptures = webInfoCaptures;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public boolean isTarget() {
        return isTarget;
    }

    public void setTarget(boolean isTarget) {
        this.isTarget = isTarget;
    }


    public JsoupExpress[] getAreaForNextPathJsoupExpresses() {
        return areaForNextPathJsoupExpresses;
    }

    public void setAreaForNextPathJsoupExpresses(JsoupExpress[] areaForNextPathJsoupExpresses) {
        this.areaForNextPathJsoupExpresses = areaForNextPathJsoupExpresses;
    }

    public Map<String, JsoupExpress> getPerNextUrlInfoCaptures() {
        return perNextUrlInfoCaptures;
    }

    public void setPerNextUrlInfoCaptures(Map<String, JsoupExpress> perNextUrlInfoCaptures) {
        this.perNextUrlInfoCaptures = perNextUrlInfoCaptures;
    }

    public boolean isRss() {
        return isRss;
    }

    public void setRss(boolean isRss) {
        this.isRss = isRss;
    }
}
