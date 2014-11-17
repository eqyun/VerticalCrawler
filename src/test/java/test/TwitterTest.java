package test;

import org.junit.Test;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.conf.ConfigurationContext;

import java.io.IOException;
import java.net.*;
import java.util.List;

/**
 * Created by eqyun on 2014/11/10.
 */
public class TwitterTest {

    private long since_id = -1;
    private int count = 200;

    Twitter twitter = TwitterFactory.getSingleton();
    @Test
    public void getTimeLine() throws Exception {
        int page = 1;
        boolean isFirst = false;
        for(;;){
            Paging paging = new Paging(page, count,since_id);

            List<Status> statuses = twitter.getHomeTimeline(paging);

            if(statuses!=null){
                for(Status status: statuses){
                    if(!isFirst){
                        if(status!=null){
                            isFirst = true;
                            since_id = status.getId();
                        }
                    }
                }
            }

            if(statuses == null || statuses.size()<200 || paging.getSinceId() == -1){
                break;
            }
            page++;
        }
    }
    @Test
    public void getTimeLine2() throws Exception {
        Twitter twitter = TwitterFactory.getSingleton();
      /*  ResponseList<Status> statuses = twitter.getHomeTimeline();
        for(Status status : statuses){
            System.out.println(status.getUser().getId());
            PagableResponseList<UserList> finance_users = twitter.getUserListMemberships(status.getUser().getId(),-1,true);
            for(UserList user : finance_users){
                System.out.println("---"+user.getName());
               *//* PagableResponseList<UserList>  _lists = twitter.getUserListMemberships(user.getId(),-1,true);
                for(UserList _u : _lists){
                    System.out.println(_u.getName());
                }*//*
            }
        }*/
        ResponseList<UserList> userLists = twitter.getUserLists(twitter.getScreenName());
        for(UserList u : userLists){
            PagableResponseList<User> finance_users = twitter.getUserListMembers(u.getId(),-1);
            for(User user : finance_users){
                System.out.println("---"+user.getName());
                ResponseList<Status> statuses = twitter.getUserTimeline(user.getId());
                for(Status status : statuses){
                    System.out.println(status.getUser().getName()+"--"+status.getText());
                }
               /* PagableResponseList<UserList>  _lists = twitter.getUserListMemberships(user.getId(),-1,true);
                for(UserList _u : _lists){
                    System.out.println(_u.getName());
                }*/
            }
        }
       /* Paging page = new Paging(1, 3);
        List<Status> statuses = twitter.getHomeTimeline(page);

        for (Status status : statuses) {
            System.out.println(status.getURLEntities()[0].getDisplayURL());
            System.out.println(status.getURLEntities()[0].getExpandedURL());
            //System.out.println(status.getId());
           *//* String userName = status.getUser().getName();
            String content = status.getText();
            System.out.println(userName + ":" + content);
            System.out.println(status.getURLEntities()[0].getURL());
            System.out.println(expandShortURL(status.getURLEntities()[0].getURL()));*//*
        }*/

    }
    public String expandShortURL(String url) throws IOException {
        SocketAddress socketAddress = new InetSocketAddress("127.0.0.1",8580);
        Proxy proxy = new Proxy(Proxy.Type.HTTP,socketAddress);
        URL inputURL = new URL(url);
        HttpURLConnection urlConnection = (HttpURLConnection) inputURL.openConnection(proxy);
        urlConnection.getHeaderFields();
        return urlConnection.getURL().toString();
    }

@Test
    public void test() throws  Exception{
        StatusListener listener = new StatusListener(){
            @Override
            public void onStatus(Status status) {
                    System.out.println(status.getUser().getName() + " : " + status.getText());
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {

            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {

            }

            @Override
            public void onStallWarning(StallWarning warning) {

            }

            @Override
            public void onException(Exception ex) {
            }
        };
        TwitterStream twitterStream = new TwitterStreamFactory(TwitterFactory.getSingleton().getConfiguration()).getInstance();
        twitterStream.addListener(listener);
        // sample() method internally creates a thread which manipulates TwitterStream and calls these adequate listener methods continuously.

    synchronized (this){
        wait();
    }
    }
    @Test
    public void testHttp(){
        System.out.println(ConfigurationContext.getInstance().getHttpClientConfiguration().getHttpProxyHost());
    }
}
