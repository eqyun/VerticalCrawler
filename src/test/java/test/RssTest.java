package test;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by eqyun on 2014/11/6.
 */
public class RssTest {
    @Test
    public void testReadRss() throws IOException {
      //  String url = "http://news.iheima.com/index.php?m=content&c=rss&rssid=10";
        String url = "http://feed.williamlong.info/";
        Document document = Jsoup.connect(url).data("query", "Java").userAgent("Mozilla").cookie("auth", "token").timeout(6000).get();

            Elements guids = document.select("guid");
        if(guids!=null)
            for(Element guid : guids){
                System.out.println(guid.text());
            }
    }
}
