package com.pada.mydao.bussines.imple;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pada.mydao.bean.*;
import com.pada.mydao.bussines.WPTool;
import com.pada.mydao.db.BaseDAO;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by eqyun on 2014/11/11.
 */
@Component
public class WPToolImpl implements WPTool{

    @Autowired
    BaseDAO db ;

   /* @Autowired
    SpiderSynchronizer synchronizer;*/

    public Map<String,Wp_terms> termCache = Maps.newHashMap();

    public Map<String,Wp_term_taxonomy> taxonomyMap = Maps.newHashMap();

    public void addPost2Category(int postId, Wp_term_taxonomy wtt,Session session) {
        Wp_term_relationships wrs = sql_getTermRelationships(postId,
                wtt.getTerm_taxonomy_id());
        if (wrs == null) {
            wrs = new Wp_term_relationships();
            wrs.setObject_id(postId);
            wrs.setTerm_taxonomy_id(wtt.getTerm_taxonomy_id());
            if(session == null)
                db.save(wrs);
            else
                db.save(session,wrs);
            categoryPlusOne(wtt,session);// category加1
        }
    }

    public String reduceName(String name) throws UnsupportedEncodingException {
        String slug = name;
        int i =0;
        while (URLEncoder.encode(slug, "utf-8").length()>200) {
            slug = name.substring(0, name.length()-(1+i++));
        }

        return slug;
    }

    public void categoryPlusOne(Wp_term_taxonomy wtt,Session session) {
        wtt.setCount(wtt.getCount() + 1);
        if(session == null)
            db.update(wtt);
        else
            db.update(session,wtt);
    }

    public Wp_term_relationships sql_getTermRelationships(int postId,
                                                         int term_taxonomyId) {
        DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Wp_term_relationships.class);
        detachedCriteria.add(Restrictions.eq("object_id", postId));
        detachedCriteria.add(Restrictions.eq("term_taxonomy_id", term_taxonomyId));
        List<Wp_term_relationships> relationships = (List<Wp_term_relationships>) db.findByDetachedCriteria(detachedCriteria);
				/*(List<Wp_term_relationships>) db
				.createQuery(
						"SELECT * FROM wp_term_relationships where object_id="
								+ postId + " and term_taxonomy_id="
								+ term_taxonomyId, Wp_term_relationships.class);*/
        if (relationships != null && relationships.size() > 0)
            return relationships.get(0);
        return null;
    }


    public void addPostMeta(int postId,List<Wp_postmeta> wp_postmetas,Session session) {
        if(wp_postmetas == null)
            return;

        List<Wp_postmeta> postmetas = sql_getPostmetas(postId);
        if (postmetas == null) {
            postmetas = new ArrayList<Wp_postmeta>();
        }

        List<Wp_postmeta> toDbMeta = new ArrayList<Wp_postmeta>();

        for (Wp_postmeta _mets : wp_postmetas) {
            String key = _mets.getMeta_key().toLowerCase();
            String value = _mets.getMeta_value();
            Wp_postmeta wp_postmeta = new Wp_postmeta();
            wp_postmeta.setMeta_key(key);
            wp_postmeta.setMeta_value(value);
            wp_postmeta.setPost_id(postId);
            toDbMeta.add(wp_postmeta);
        }

        for (Wp_postmeta wm : postmetas) {
            for (Wp_postmeta wp_postmeta : toDbMeta) {
                if (wm.getMeta_key().equals(wp_postmeta.getMeta_key())) {
                    wp_postmeta.setMeta_id(wm.getMeta_id());
                }
            }
        }

        for (Wp_postmeta wp_postmeta : toDbMeta) {
            if(session == null)
                db.saveOrUpdate(wp_postmeta);
            else
                db.saveOrUpdate(session,wp_postmeta);
        }
    }


    public void addUserMetas(int userId,List<Wp_usermeta> wp_usermetas,Session session){

        if(wp_usermetas == null)
            return;
        List<Wp_usermeta> dbUsermetas = sql_geUsermetas(userId);

        if(dbUsermetas == null)
            dbUsermetas = Lists.newArrayList();


        for (Wp_usermeta _mets : wp_usermetas) {
            _mets.setUser_id(userId);
        }

        for (Wp_usermeta wm : dbUsermetas) {
            for (Wp_usermeta wp_usermeta : wp_usermetas) {
                if (wm.getMeta_key().equals(wp_usermeta.getMeta_key())) {
                    wp_usermeta.setUmeta_id(wm.getUmeta_id());
                }
            }
        }

        for (Wp_usermeta wp_usermeta : wp_usermetas) {
            if(session == null)
                db.saveOrUpdate(wp_usermeta);
            else
                db.saveOrUpdate(session,wp_usermeta);
        }

    }

    @Override
    public void addCommentMetas(int commentId, List<Wp_commentmeta> wp_commentMetas, Session session) {

        if(wp_commentMetas == null)
            return;

        List<Wp_commentmeta> db_commentmetas = sql_getCommentmetas(commentId);
        if(db_commentmetas == null)
            db_commentmetas = Lists.newArrayList();

        for(Wp_commentmeta wp_commentmeta : wp_commentMetas){
            wp_commentmeta.setComment_id(commentId);
            String toDbkey = wp_commentmeta.getMeta_key();
            for(Wp_commentmeta dbmeta : db_commentmetas){
                if(dbmeta!=null){
                    if(dbmeta.getMeta_key().equals(toDbkey)){
                        wp_commentmeta.setMeta_id(dbmeta.getMeta_id());
                    }
                }
            }
            if(session == null)
                db.saveOrUpdate(wp_commentmeta);
            else
                db.saveOrUpdate(session,wp_commentmeta);
        }
    }

    public List<Wp_postmeta> sql_getPostmetas(int postId) {
        DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Wp_postmeta.class);
        detachedCriteria.add(Restrictions.eq("post_id", postId));

        List<Wp_postmeta> posts = (List<Wp_postmeta>) db.findByDetachedCriteria(detachedCriteria);

        return posts;
    }

    public List<Wp_usermeta> sql_geUsermetas(int userId) {
        DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Wp_usermeta.class);
        detachedCriteria.add(Restrictions.eq("user_id", userId));

        List<Wp_usermeta> usermetas = (List<Wp_usermeta>) db.findByDetachedCriteria(detachedCriteria);

        return usermetas;
    }


    public List<Wp_commentmeta> sql_getCommentmetas(int commentId){
        DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Wp_commentmeta.class);

        detachedCriteria.add(Restrictions.eq("comment_id",commentId));

        return (List<Wp_commentmeta>) db.findByDetachedCriteria(detachedCriteria);

    }

    public Wp_posts sql_getPostByGuid(String guid) {
        DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Wp_posts.class);
        detachedCriteria.add(Restrictions.eq("guid", guid));
        List<Wp_posts> posts = (List<Wp_posts>) db.findByDetachedCriteria(detachedCriteria);
				/* (List<Wp_posts>) db.createQuery(
				"SELECT * FROM wp_posts where guid=\"" + guid+"\"", Wp_posts.class);*/
        if (posts != null && posts.size() > 0)
            return posts.get(0);
        else
            return null;
    }

    public Wp_posts sql_getPostByPostName(String postName) {
        DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Wp_posts.class);
        detachedCriteria.add(Restrictions.eq("post_name", postName));
        List<Wp_posts> posts = (List<Wp_posts>) db.findByDetachedCriteria(detachedCriteria);
				/* (List<Wp_posts>) db.createQuery(
				"SELECT * FROM wp_posts where guid=\"" + guid+"\"", Wp_posts.class);*/
        if (posts != null && posts.size() > 0)
            return posts.get(0);
        else
            return null;
    }

    public Wp_terms sql_getTermsBySlug(String slug) throws UnsupportedEncodingException, InterruptedException {
       /* String key = "wp_terms_"+category;
        synchronizer.checkSynchronize(key);*/

        DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Wp_terms.class);
        detachedCriteria.add(Restrictions.eq("slug", java.net.URLEncoder.encode(slug, "utf-8")));
        List<Wp_terms> terms = (List<Wp_terms>) db.findByDetachedCriteria(detachedCriteria);
        if (terms != null && terms.size() > 0) {
            return terms.get(0);
        }
        return null;
    }

    public Wp_term_taxonomy sql_getTermTaxonomy(int termId,String taxonomy) throws InterruptedException {


      /*  String key = "wp_term_taxonomy_"+termId+"_"+taxonomy;
        synchronizer.checkSynchronize(key);*/

        DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Wp_term_taxonomy.class);
        detachedCriteria.add(Restrictions.eq("taxonomy", taxonomy));
        detachedCriteria.add(Restrictions.eq("term_id", termId));
        List<Wp_term_taxonomy> wp_term_taxonomies = (List<Wp_term_taxonomy>) db.findByDetachedCriteria(detachedCriteria);

        if (wp_term_taxonomies != null && wp_term_taxonomies.size() > 0) {
            return wp_term_taxonomies.get(0);
        }
        return null;
    }

    public Wp_term_taxonomy getTermTaxonmy(String catortagName,String catOrTagSlug,String taxonomy , Session session)
            throws UnsupportedEncodingException, InterruptedException {

        if(catOrTagSlug == null || catOrTagSlug.length() == 0)
            catOrTagSlug = catortagName;

        catortagName = catortagName.trim();
        catOrTagSlug = catOrTagSlug.trim();


        Wp_terms wt = sql_getTermsBySlug(catOrTagSlug);
        //这个地方不用加事务
        if (wt == null) {
            wt = new Wp_terms();


            wt.setName(catortagName);
            wt.setSlug(java.net.URLEncoder.encode(catOrTagSlug, "utf-8"));
            wt.setTerm_group(0);
           /* if (session != null) {
                db.saveOrUpdate(session, wt);
               *//* String key = "wp_terms_"+catortag;
                synchronizer.put(Thread.currentThread().getId(),key,"");*//*
            }
            else*/
            db.saveOrUpdate(wt);
        }

        Wp_term_taxonomy wp_term_taxonomy = sql_getTermTaxonomy(wt.getTerm_id(),taxonomy);
        if (wp_term_taxonomy == null) {
            wp_term_taxonomy = createTermTaxonmy(wt.getTerm_id(),taxonomy,session);
        }
        return wp_term_taxonomy;

    }

    public Wp_term_taxonomy createTermTaxonmy(int termId,String taxonomy,Session session)
            throws UnsupportedEncodingException {
        Wp_term_taxonomy wp_term_taxonomy = new Wp_term_taxonomy();
        wp_term_taxonomy.setTaxonomy(taxonomy);
        wp_term_taxonomy.setTerm_id(termId);
        if(session==null)
            db.save(wp_term_taxonomy);
        else {
            db.save(session, wp_term_taxonomy);
           /* String key = "wp_term_taxonomy_"+termId+"_"+taxonomy;
            synchronizer.put(Thread.currentThread().getId(),key,"");*/
        }
        return wp_term_taxonomy;
    }

    public void transformPost(Wp_posts wp_posts) throws UnsupportedEncodingException{

        //wp_posts.setPost_title(reduceTitle(wp_posts.getPost_title()));
        String name = wp_posts.getPost_name();
        if(name==null || name.length()==0) {
            name = wp_posts.getPost_title();
        }

        name = delHTMLTag(name);
        name = name.replaceAll(" ", "-");
        name = name.replaceAll("/", "-");
        name = name.replaceAll("\\.", "-");
        name = reduceName(name);
        int i = 0;
        String slug = URLEncoder.encode(name, "utf-8");
        while (checkPostNameExit(slug,wp_posts.getID())) {
            slug = URLEncoder.encode(name+"-"+i++, "utf-8");
        }
        wp_posts.setPost_name(slug);
    }

    public boolean checkPostNameExit(String postName,int notId){
        Wp_posts wp_posts = sql_getPostByPostName(postName);
        if(wp_posts == null)
            return false;
        if(wp_posts.getID() == notId)
            return false;
        return true;
    }

    public String delHTMLTag(String htmlStr){
        String regEx_script="<script[^>]*?>[\\s\\S]*?<\\/script>"; //定义script的正则表达式
        String regEx_style="<style[^>]*?>[\\s\\S]*?<\\/style>"; //定义style的正则表达式
        String regEx_html="<[^>]+>"; //定义HTML标签的正则表达式

        Pattern p_script=Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE);
        Matcher m_script=p_script.matcher(htmlStr);
        htmlStr=m_script.replaceAll(""); //过滤script标签

        Pattern p_style=Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE);
        Matcher m_style=p_style.matcher(htmlStr);
        htmlStr=m_style.replaceAll(""); //过滤style标签

        Pattern p_html=Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE);
        Matcher m_html=p_html.matcher(htmlStr);
        htmlStr=m_html.replaceAll(""); //过滤html标签

        return htmlStr.trim(); //返回文本字符串
    }


    public Wp_users sql_getUserByLoginName(String name){
        DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Wp_users.class);
        detachedCriteria.add(Restrictions.eq("user_login", name));
        List<Wp_users> users = (List<Wp_users>) db.findByDetachedCriteria(detachedCriteria);
					/* (List<Wp_posts>) db.createQuery(
					"SELECT * FROM wp_posts where guid=\"" + guid+"\"", Wp_posts.class);*/
        if (users != null && users.size() > 0)
            return users.get(0);
        else
            return null;
    }

    public Wp_users saveUser(Wp_users user,Session session){


        if(user.getDisplay_name() == null || user.getDisplay_name().length() ==0){
            user.setDisplay_name(user.getUser_login());
        }
        if(user.getUser_nicename() == null || user.getUser_nicename().length() ==0){
            user.setUser_nicename(user.getUser_login().replaceAll(" ","-"));
        }

        db.saveOrUpdate(session,user);
        return user;
    }

    @Override
    public List<Wp_comments> sql_getCommentsByPostId(int postId) {
        DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Wp_comments.class);
        detachedCriteria.add(Restrictions.eq("comment_post_ID",postId));
        return (List<Wp_comments>) db.findByDetachedCriteria(detachedCriteria);
    }
}
