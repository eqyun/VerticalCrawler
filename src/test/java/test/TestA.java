package test;

import com.pada.common.FileOperator;
import com.pada.mydao.bean.Wp_posts;
import com.pada.spider.annotation.SpiderKeyInject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.springframework.web.util.HtmlUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestA {
    private String date = "post_date";

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    @Test
    public void testThreadId() throws Exception {
        String url = "http://quotes.morningstar.com/fund/esecx/f?t=";
        File resultFile = new File("C:\\Users\\eqyun\\Desktop\\work\\resultFund2.txt");
        String fund = FileOperator.getContent("C:\\Users\\eqyun\\Desktop\\work\\checkfund2.txt");
        String[] funds = fund.split("\n");
        for (String f : funds) {
            if (f.length() == 0)
                continue;
            String _url = url + f;
            if (_url.replaceAll(" ", "").length() == 0)
                continue;
            System.out.println(_url);
            try {
                Document document = Jsoup.connect(_url).get();
                String title = document.select("title").text();
                if (title.equals("Morningstar.com Error Page")) {
                    System.out.println(title);
                    FileOperator.write2File("notFound" + f, resultFile, false);
                }
            } catch (Exception e) {
                FileOperator.write2File("error " + f, resultFile, false);
                e.printStackTrace();
            }

        }

    }

    @Test
    public void test2() {
        String regex = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        Pattern pattern = Pattern.compile(regex);
        String html = FileOperator.getContent("D:\\marketwatchrss.txt");
        html = HtmlUtils.htmlUnescape(html);
        Matcher matcher = pattern.matcher(html);
        while (matcher.find()) {

            System.out.println(matcher.group());
        }
    }
}
