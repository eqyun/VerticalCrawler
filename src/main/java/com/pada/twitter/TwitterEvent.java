package com.pada.twitter;

import java.util.List;


import com.pada.mydao.bean.*;
import com.pada.mydao.bussines.WPTool;
import com.pada.mydao.db.BaseDAO;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.UserList;

@Service
public class TwitterEvent {

    @Autowired
    TwitterTools twitterTools;

    @Autowired
    WPTool wpTool;

    @Autowired
    BaseDAO db;

    public void pushTwitter2WP() throws TwitterException {


        List<UserList> lists = twitterTools.pullAllList();

        if(lists!=null){
            for(UserList userList : lists){
                List<Status> statuses = twitterTools.pullNewTwitter(userList);
                utilSaveStatus(statuses,userList.getName());
            }
        }
    }

    public void utilSaveStatus(List<Status> statuses,String category){
        Session session = null;
        Transaction transaction = null;
        if (statuses != null) {
            for (Status status : statuses) {
                try {
                    session = db.getSession();
                    transaction = session.getTransaction();
                    List<Wp_posts> wp_posts = twitterTools.coverStatus2Posts(status);
                    if (wp_posts == null || wp_posts.size() == 0)//if not post,continue
                        continue;


                    Wp_users wp_user = twitterTools.coverStatus2Users(status);
                    //1 User
                    //1.1 检测系统有没有这个用户
                    Wp_users db_wp_user = wpTool.sql_getUserByLoginName(wp_user.getUser_login());

                    if (db_wp_user == null)
                        db_wp_user = wpTool.saveUser(wp_user, session);

                    //1.2 把用户的meta加进去
                    List<Wp_usermeta> usermetas = twitterTools.coverStatus2UserMeta(status);
                    wpTool.addUserMetas(db_wp_user.getID(), usermetas, session);
                    //2 Post
                    //2.2 找出post

                    for (Wp_posts post : wp_posts) {
                        Wp_posts db_post = wpTool.sql_getPostByGuid(post.getGuid());
                        //2.3 如果没有找到，执照默认的存进数据库
                        if (db_post == null) {
                            post.setPost_author(1);
                            wpTool.transformPost(post);
                            db.saveOrUpdate(session, post);
                            db_post = post;
                        }
                        //2.4 这个post加category
                        Wp_term_taxonomy wp_term_taxonomy = wpTool.getTermTaxonmy("guru",null, "category", session);
                        Wp_term_taxonomy wp_term_taxonomy_tag = wpTool.getTermTaxonmy(category,null,"post_tag",session);

                        wpTool.addPost2Category(db_post.getID(), wp_term_taxonomy, session);
                        wpTool.addPost2Category(db_post.getID(),wp_term_taxonomy_tag,session);
                        //3 comment
                        Wp_comments comment = twitterTools.coverStatus2comment(status);

                        comment.setComment_post_ID(db_post.getID());
                        comment.setUser_id(db_wp_user.getID());
                        comment.setComment_author(db_wp_user.getUser_login());

                        List<Wp_comments> db_comments = wpTool.sql_getCommentsByPostId(db_post.getID());
                        //3.1检测重复，如无则插入,若有，也要把IDset进来后面用
                        if (!twitterTools.isDuplicate(comment, db_comments)) {
                            db.saveOrUpdate(session, comment);
                        }
                        //3.2 加comment meta
                        List<Wp_commentmeta> toDbCommentMetas = twitterTools.coverStatus2CommentMeta(status);

                        wpTool.addCommentMetas(comment.getComment_ID(), toDbCommentMetas, session);
                    }
                    transaction.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (transaction != null)
                        transaction.rollback();
                } finally {
                    if (session != null)
                        db.closeSession(session);
                }
            }
        }
    }


}
