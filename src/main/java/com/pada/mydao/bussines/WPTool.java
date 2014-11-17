package com.pada.mydao.bussines;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Maps;
import com.pada.mydao.bean.*;
import com.pada.spider.tool.SpiderAnalyse;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.pada.mydao.db.BaseDAO;

public interface WPTool {


    /**
     * 把post加到分类中去，不管是tag还是category
     * @param postId
     * @param wtt
     * @param session
     */
    public void addPost2Category(int postId, Wp_term_taxonomy wtt, Session session);


    /**
     * 分类数加一
     * @param wtt
     * @param session
     */
    public void categoryPlusOne(Wp_term_taxonomy wtt, Session session);

    /**
     *找出这个post是不是和这个分类已经挂钩，如果没有，返回null
     * @param postId
     * @param term_taxonomyId
     * @return null:mean not relation
     */
    public Wp_term_relationships sql_getTermRelationships(int postId,
                                                         int term_taxonomyId);

    /**
     * 加postmeta到post里去
     * @param postId
     * @param wp_postmetas
     * @param session
     */
    public void addPostMeta(int postId, List<Wp_postmeta> wp_postmetas, Session session);

    /**
     * 加usermeta 到user里去
     * @param userId
     * @param wp_usermetas
     * @param session
     */
    public void addUserMetas(int userId, List<Wp_usermeta> wp_usermetas, Session session);


    public void addCommentMetas(int commentId,List<Wp_commentmeta> wp_commentmetas,Session session);


    /**
     * 找post_meta
     * @param postId
     * @return
     */
    public List<Wp_postmeta> sql_getPostmetas(int postId);

    /**
     * 找user_meta
     * @param userId
     * @return
     */
    public List<Wp_usermeta> sql_geUsermetas(int userId);

    /**
     * 根据guid找post
     * @param guid
     * @return
     */
    public Wp_posts sql_getPostByGuid(String guid);

    /**
     * 根据post_name找post，作用是确定post_name是唯一
     * @param postName
     * @return
     */
    public Wp_posts sql_getPostByPostName(String postName);

    /**
     * 找子分类的名字，还没有涉及到分类学
     * @return
     * @throws UnsupportedEncodingException
     * @throws InterruptedException
     */
    public Wp_terms sql_getTermsBySlug(String slug) throws UnsupportedEncodingException, InterruptedException;

    /**
     * 根据分类学，找具体分类学下的子分类
     * @param termId
     * @param taxonomy
     * @return
     * @throws InterruptedException
     */
    public Wp_term_taxonomy sql_getTermTaxonomy(int termId, String taxonomy) throws InterruptedException;

    /**
     * 找分类下的子分类，如果没有，就创建新的
     * @param taxonomy
     * @param session
     * @return
     * @throws UnsupportedEncodingException
     * @throws InterruptedException
     */
    public Wp_term_taxonomy getTermTaxonmy(String catortagName,String catOrTagSlug, String taxonomy, Session session)
            throws UnsupportedEncodingException, InterruptedException;

    /**
     * 根据子分类名字和分类学，创建具体分类
     * @param termId
     * @param taxonomy
     * @param session
     * @return
     * @throws UnsupportedEncodingException
     */
    public Wp_term_taxonomy createTermTaxonmy(int termId, String taxonomy, Session session)
            throws UnsupportedEncodingException;

    /**
     * 转换post的一些属性。name、slug转义
     * @param wp_posts
     * @throws UnsupportedEncodingException
     */
    public void transformPost(Wp_posts wp_posts) throws UnsupportedEncodingException;


    /**
     * 根据nice name找用户
     * @param name
     * @return
     */
    public Wp_users sql_getUserByLoginName(String name);

    /**
     * 保存用户
     * @param user login_name not null
     * @param session
     * @return
     */
    public Wp_users saveUser(Wp_users user, Session session);

    public List<Wp_comments> sql_getCommentsByPostId(int postId);

}
