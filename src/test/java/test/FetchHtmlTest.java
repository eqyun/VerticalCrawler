package test;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.pada.spider.tool.SpiderAnalyse;
import org.jsoup.nodes.Document;
import org.junit.Test;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import com.pada.common.ArrUtil;
import com.pada.common.RegexUtil;
import com.pada.spider.bean.JsoupExpress;
import com.pada.spider.tool.impl.SpiderAnalyseImpl;

public class FetchHtmlTest implements PageProcessor {

	private Site site = Site.me().setRetryTimes(3).setSleepTime(100);

    SpiderAnalyse spiderAnalyse = new SpiderAnalyseImpl();


	public FetchHtmlTest() {
		super();
	}

	@Override
	public void process(Page page) {
		/*
		 * page.addTargetRequests(page.getHtml().links().regex(
		 * "(https://github\\.com/\\w+/\\w+)").all()); page.putField("author",
		 * page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString());
		 * page.putField("name",
		 * page.getHtml().xpath("//h1[@class='entry-title public']/strong/a/text()"
		 * ).toString());
		 */
		// System.out.println(page.getHtml());
		Document body = page.getHtml().getDocument();

        JsoupExpress title = new JsoupExpress("html body div#main div#im div#nml div#show h3",null,null,false);

        JsoupExpress content = new JsoupExpress("#showinfo img:first","src",null,false);


        fetch(content,body);
		// fetch(title, body);
		// fetch(description,body);

	}


	private void fetch(JsoupExpress jsoupExpress, Document body) {
		String[] infos = spiderAnalyse.fetchJsoup(jsoupExpress,body);
        System.out.println(Lists.newArrayList(infos));
    }
	
	

	@Override
	public Site getSite() {
		return site;
	}
	@Test
	public void main() throws UnsupportedEncodingException {

		// System.out.println(RegexUtil.check("http://www.dy2018.com/2/index_2.html",
		// "http://www.dy2018.com/\\d+/index_*\\d*.html"));

		Spider.create(new FetchHtmlTest())
				.addUrl("http://www.piaohua.com/html/kehuan/2012/0618/24677.html")
				.thread(1).run();
	}
	
	

}