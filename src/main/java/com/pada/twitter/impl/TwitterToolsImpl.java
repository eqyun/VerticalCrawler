package com.pada.twitter.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pada.mydao.bean.*;
import com.pada.twitter.TwitterTools;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import twitter4j.*;
import twitter4j.conf.ConfigurationContext;

import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.Map;

/**
 * Created by eqyun on 2014/11/11.
 */
@Component
public class TwitterToolsImpl implements TwitterTools {

    private long since_id = -1;
    private int count = 100;

    private String comment_type = "twitter";

    Twitter twitter = TwitterFactory.getSingleton();

    Map<Long,Long> sinceCache = Maps.newHashMap();

    HttpClientConfiguration httpClientConfiguration = ConfigurationContext.getInstance().getHttpClientConfiguration();

    @Override
    @Deprecated
    public List<Status> pullNewTwitter() throws TwitterException {
        long _since_id = since_id;
        int page = 1;
        boolean isFirst = false;
        List<Status> result = Lists.newArrayList();
        for (; ; ) {
            Paging paging = _since_id == -1 ? new Paging(page, count) : new Paging(page, count, _since_id);
            List<Status> statuses = twitter.getHomeTimeline(paging);
            if (statuses != null) {
                for (Status status : statuses) {
                    if (!isFirst) {
                        if (status != null) {
                            isFirst = true;
                            since_id = status.getId();
                        }
                    }
                }
                result.addAll(statuses);
            }
            if (statuses == null || statuses.size() < count || paging.getSinceId() == -1) {
                break;
            }
            page++;
        }
        return result;
    }

    @Override
    public List<UserList> pullAllList() throws TwitterException {
        ResponseList<UserList> userLists = twitter.getUserLists(twitter.getScreenName());
        return userLists;
    }

    @Override
    public List<Status> pullNewTwitter(UserList userList) throws TwitterException {
        PagableResponseList<User> members = twitter.getUserListMembers(userList.getId(),-1);
        List<Status> result = Lists.newArrayList();
        for(User member : members){
            int page = 1;
            long memberId = member.getId();
            Long _since_id = sinceCache.get(memberId);
            boolean isFirst = false;
            for(;;){
                Paging paging = _since_id == null ? new Paging(page, count) : new Paging(page, count, _since_id);
                ResponseList<Status> statuses = twitter.getUserTimeline(member.getId());
                if(statuses!=null && statuses.size()>0 && !isFirst){
                    isFirst = true;
                    sinceCache.put(memberId,statuses.get(0).getId());
                }
                if(statuses!=null)
                    result.addAll(statuses);
                if (statuses == null || statuses.size() < count || paging.getSinceId() == -1) {
                    break;
                }
                page++;
            }

        }
        return result;
    }

    @Override
    public Wp_comments coverStatus2comment(Status status) {
        Wp_comments comment = new Wp_comments();

        comment.setComment_author_url(status.getUser().getURL());

        comment.setComment_content(status.getText());
        comment.setComment_type(comment_type);

        return comment;
    }

    @Override
    public List<Wp_posts> coverStatus2Posts(Status status) {

        URLEntity[] urlEntities = status.getURLEntities();

        if (urlEntities == null || urlEntities.length == 0)
            return null;

        List<Wp_posts> wp_posts = Lists.newArrayList();

        for (URLEntity urlEntity : urlEntities) {
            try {
                String shortUrl = urlEntity.getExpandedURL();
                String originalUrl = expandShortURL(shortUrl);
                Document document = Jsoup.connect(originalUrl).data("query", "Java").userAgent("Mozilla").cookie("auth", "token").timeout(6000).get();
                String title = document.select("title").text();
                Wp_posts post = new Wp_posts();
                post.setGuid(originalUrl);
                post.setPost_title(title);
                wp_posts.add(post);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }

        return wp_posts;
    }

    @Override
    public Wp_users coverStatus2Users(Status status) {
        Wp_users user = new Wp_users();
        String name = status.getUser().getName() + "@twitter";
        user.setUser_login(name);
        return user;
    }

    @Override
    public List<Wp_usermeta> coverStatus2UserMeta(Status status) {
        User user = status.getUser();

        List<Wp_usermeta> usermetas = Lists.newArrayList();

        Wp_usermeta usermeta = new Wp_usermeta();
        usermeta.setMeta_key("_avatar_");
        usermeta.setMeta_value(user.getOriginalProfileImageURL());


        Wp_usermeta usermeta2 = new Wp_usermeta();
        usermeta2.setMeta_key("_twitter_screen_name_");
        usermeta2.setMeta_value(user.getScreenName());

        usermetas.add(usermeta);
        usermetas.add(usermeta2);


        return usermetas;
    }

    @Override
    public boolean isDuplicate(Wp_comments comment, List<Wp_comments> comments) {

        if (comments == null || comments.size() == 0)
            return false;

        for (Wp_comments dbComment : comments) {

            //id相同
            //内容相同
            //type为"twitter"
            if (dbComment.getUser_id() == comment.getUser_id() && dbComment.getComment_content().equals(comment.getComment_content()) && dbComment.getComment_type().equals(comment_type)) {
                comment.setComment_ID(dbComment.getComment_ID());
                return true;
            }

        }


        return false;
    }

    @Override
    public List<Wp_commentmeta> coverStatus2CommentMeta(Status status) {
        Wp_commentmeta favourMeta = new Wp_commentmeta();
        favourMeta.setMeta_key("_twitter_favorite_count_");
        favourMeta.setMeta_value(String.valueOf(status.getFavoriteCount()));

        Wp_commentmeta reTwitterMeta = new Wp_commentmeta();
        reTwitterMeta.setMeta_key("_twitter_retweet_count_");
        reTwitterMeta.setMeta_value(String.valueOf(status.getRetweetCount()));

        return Lists.newArrayList(favourMeta, reTwitterMeta);
    }


    public String expandShortURL(String url) {
        try {
            SocketAddress socketAddress = new InetSocketAddress("127.0.0.1", 8580);
            Proxy proxy = new Proxy(Proxy.Type.HTTP, socketAddress);
            URL inputURL = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) inputURL.openConnection(proxy);
            urlConnection.getHeaderFields();
            return urlConnection.getURL().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return url;
        }
    }


}
