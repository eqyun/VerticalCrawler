package com.pada.spider.domain;

import com.pada.mongo.domain.BaseDomain;
import com.pada.spider.bean.SpiderPath;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "spider_strategy")

public class SpiderStrategy extends BaseDomain{

    @Indexed
    private String classify;

    private String[] startUrl;


    private SpiderPath[] spiderPaths;

    //默认开10条线程
    private int thread = 10;

    //默认编码
    private String encoding="utf-8";

    private int timeOut = 5000;

    private int retryTimes = 3;

    private int sleepTime = 1000;

    private String domain;

    private String proxyIP ;

    private int proxyPort;

    private String injectInfoClassName;


    private boolean workWithFinalProcess = true;


    public SpiderStrategy(String classify) {
        this.classify = classify;
    }

    public String[] getStartUrl() {
        return startUrl;
    }

    public SpiderStrategy setStartUrl(String... startUrl) {
        this.startUrl = startUrl;
        return this;
    }

    public String getInjectInfoClassName() {
        return injectInfoClassName;
    }

    public void setInjectInfoClassName(String injectInfoClassName) {
        this.injectInfoClassName = injectInfoClassName;
    }

    public String getClassify() {
        return classify;
    }


    public SpiderStrategy setClassify(String classify) {
        this.classify = classify;
        return this;
    }


    public SpiderPath[] getSpiderPaths() {
        return spiderPaths;
    }

    public SpiderStrategy setSpiderPaths(SpiderPath[] spiderPaths) {
        this.spiderPaths = spiderPaths;
        return this;
    }

    public int getThread() {
        return thread;
    }

    public SpiderStrategy setThread(int thread) {
        this.thread = thread;
        return this;
    }

    public boolean isWorkWithFinalProcess() {
        return workWithFinalProcess;
    }

    public SpiderStrategy setWorkWithFinalProcess(boolean workWithFinalProcess) {
        this.workWithFinalProcess = workWithFinalProcess;
        return this;
    }

    public String getEncoding() {
        return encoding;
    }

    public SpiderStrategy setEncoding(String encoding) {
        this.encoding = encoding;
        return this;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public SpiderStrategy setTimeOut(int timeOut) {
        this.timeOut = timeOut;
        return this;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public SpiderStrategy setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
        return this;
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public SpiderStrategy setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
        return this;
    }

    public String getDomain() {
        return domain;
    }

    public SpiderStrategy setDomain(String domain) {
        this.domain = domain;
        return this;
    }

    public String getProxyIP() {
        return proxyIP;
    }

    public SpiderStrategy setProxyIP(String proxyIP) {
        this.proxyIP = proxyIP;
        return this;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public SpiderStrategy setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
        return this;
    }



}
