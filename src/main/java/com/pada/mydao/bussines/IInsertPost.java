package com.pada.mydao.bussines;

import java.util.List;

import com.pada.mydao.bean.*;


public interface IInsertPost {

  //  public int insertPost(Wp_posts wp_posts, List<Wp_postmeta> wp_postmetas, Object[] categorys);

    /**
     *
     * @param wp_posts
     * @param wp_postmetas
     * @param categories
     * @param tags
     * @param wp_users nicename is necessary
     * @param usermetas
     * @return
     */
    public int insertPost(Wp_posts wp_posts, List<Wp_postmeta> wp_postmetas, List<Wp_terms> categories, List<Wp_terms> tags, Wp_users wp_users, List<Wp_usermeta> usermetas);

    //public int insertPost(Wp_posts wp_posts,List<Wp_postmeta> wp_postmetas,Wp_users user,List<Wp_usermeta> usermetas);

}
