package com.pada.twitter;

import com.pada.mydao.bean.*;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.UserList;

import java.io.IOException;
import java.util.List;

/**
 * Created by eqyun on 2014/11/11.
 */
public interface TwitterTools {
    @Deprecated
    public List<Status> pullNewTwitter() throws TwitterException;

    public List<UserList> pullAllList() throws TwitterException;


    public List<Status> pullNewTwitter(UserList userList) throws TwitterException;


    /**
     * comment type : twitter
     * @param status
     * @return
     */
    public Wp_comments coverStatus2comment(Status status);//twitter

    /**
     * user:admin
     * guid:url
     * @param status
     * @return
     */
    public List<Wp_posts> coverStatus2Posts(Status status) throws IOException;//Gurus

    /**
     * name:xx_xx@twitter
     * @param status
     * @return
     */
    public Wp_users coverStatus2Users(Status status);

    /**
     * come_from : https://twitter.com/
     * twitter_avatar
     * twitter_screen_name
     * @param status
     * @return
     */
    public List<Wp_usermeta> coverStatus2UserMeta(Status status);

    /**
     * 测试有没有重复
     * @param comment
     * @param comments
     * @return
     */
    public boolean isDuplicate(Wp_comments comment,List<Wp_comments> comments);

    /**
     * favours
     * @param status
     * @return
     */
    public List<Wp_commentmeta> coverStatus2CommentMeta(Status status);


}
