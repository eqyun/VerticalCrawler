package com.pada.mydao.bussines.imple;

import java.util.List;

//import com.pada.spider.tool.SpiderSynchronizer;
import com.pada.mydao.bean.*;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pada.mydao.bussines.IInsertPost;
import com.pada.mydao.bussines.WPTool;
import com.pada.mydao.db.BaseDAO;

@Service
public class InsertPostImpl implements IInsertPost {
	@Autowired
	BaseDAO db ;
	@Autowired
	WPTool wpTool;

 /*   @Autowired
    SpiderSynchronizer synchronizer;*/

	


	
	/*@Override
	public int insertPost(Wp_posts wp_posts, List<Wp_postmeta> wp_postmetas,
			Object[] categorys) {
		String title = wp_posts.getPost_title();
		String guid = wp_posts.getGuid();
		String cotent = wp_posts.getPost_content();
		String excerpt =wp_posts.getPost_excerpt();
		
		try {
			title = wpTool.reduceTitle(title);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
		Session session = null;
		Transaction transaction = null;
		try {
			session = db.getSession();
			transaction = session.getTransaction();
			
			Wp_posts wp_post = wpTool.sql_getPostByGuid(wp_posts.getGuid());
			if (wp_post == null){
				wp_post = wp_posts;
			}else{
				wp_posts.setID(wp_post.getID());
				wp_post = wp_posts;
			}
			
			if(wp_post.getPost_name()==null || wp_post.getPost_name().trim().length()==0){
				wp_post.setPost_name(wpTool.transformPost(title));
			}
			wp_post.setPost_title(title);
			wp_post.setGuid(guid);
			wp_post.setPost_content(cotent);
			wp_post.setPost_excerpt(excerpt);
			wp_post.setPost_author(1);
			
			db.saveOrUpdate(session,wp_post);// 保存
			if(categorys!=null){
				for (Object _category : categorys) {
	                String category = (String)_category;
					Wp_term_taxonomy wtt = wpTool.getTermTaxonmy(category,session);// 加category
					wpTool.addPost2Category(wp_post.getID(), wtt,session);// 把post加到category
				}
			}
			wpTool.addPostMeta(wp_post.getID(),wp_postmetas,session);
			transaction.commit();
			System.out.println("insert "+wp_post.getID()+" done:"+new String(title.getBytes(), "UTF-8"));
			return wp_post.getID();
		} catch (Exception e1) {
			e1.printStackTrace();
			if(transaction!=null)
				transaction.rollback();
			throw new RuntimeException(e1);
		}finally{
			if(session!=null)
			db.closeSession(session);
		}
		
	}*/

	@Override
	public int insertPost(Wp_posts wp_posts, List<Wp_postmeta> wp_postmetas,
                          List<Wp_terms> categories,List<Wp_terms> tags, Wp_users wp_users,
			List<Wp_usermeta> usermetas) {


        long start = System.currentTimeMillis();

		Session session = null;
		Transaction transaction = null;
		try {
			session = db.getSession();
			transaction = session.getTransaction();

            long start0 = System.currentTimeMillis();
			
			//先找出数据库中的post根据guid
			Wp_posts db_post = wpTool.sql_getPostByGuid(wp_posts.getGuid());

            //System.out.println("get post time:"+(System.currentTimeMillis()-start));
            start = System.currentTimeMillis();

            if(db_post!=null){
				wp_posts.setID(db_post.getID());
			}

			//对post进行转换
			wpTool.transformPost(wp_posts);

           // System.out.println("cover time:"+(System.currentTimeMillis()-start));

            if(wp_users==null){
				wp_users = new Wp_users();
				wp_users.setUser_login("admin");
                wp_users.setUser_nicename("admin");
			}

            start = System.currentTimeMillis();
			
			Wp_users dbUser = wpTool.sql_getUserByLoginName(wp_users.getUser_login());

            //System.out.println("get user time:"+(System.currentTimeMillis()-start));
            if(dbUser == null){
                String niceName = wp_users.getUser_nicename();
                if(wp_users.getUser_login().length()==0)
                    wp_users.setUser_login(niceName);
                if(wp_users.getDisplay_name().length() == 0)
                    wp_users.setDisplay_name(niceName);

				dbUser = wpTool.saveUser(wp_users,session);
			}
			//关联user
			wp_posts.setPost_author(dbUser.getID());
			//保存进post
			db.saveOrUpdate(session,wp_posts);
			
			
			if(categories!=null){
                long start2 = System.currentTimeMillis();
                for (Wp_terms _category : categories) {
	                String name  = _category.getName();
                    String slug = _category.getSlug();
					Wp_term_taxonomy wtt = wpTool.getTermTaxonmy(name,slug,"category",session);// 加category
					wpTool.addPost2Category(wp_posts.getID(), wtt,session);// 把post加到category
				}
               // System.out.println("add category time"+(System.currentTimeMillis()-start2));

			}
			if(tags!=null){
                long start3 = System.currentTimeMillis();
				for (Wp_terms _tag : tags) {
                    String name  = _tag.getName();
                    String slug = _tag.getSlug();
					Wp_term_taxonomy wtt = wpTool.getTermTaxonmy(name,slug,"post_tag",session);// 加tag
					wpTool.addPost2Category(wp_posts.getID(), wtt,session);// 把post加到tag
				}
                //System.out.println("add tag time"+(System.currentTimeMillis()-start3));
			}

            long start4 = System.currentTimeMillis();
			wpTool.addPostMeta(wp_posts.getID(),wp_postmetas,session);//加post meta
           // System.out.println("add postmeta time"+(System.currentTimeMillis()-start4));
            long start5 = System.currentTimeMillis();
			wpTool.addUserMetas(dbUser.getID(), usermetas, session);
            //System.out.println("add usemeta time"+(System.currentTimeMillis()-start5));
			long start6 = System.currentTimeMillis();
			transaction.commit();
			System.out.println(wp_posts.getID()+ " spent time:"+(System.currentTimeMillis()-start0)+":"+new String(wp_posts.getPost_title().getBytes(), "UTF-8"));
           // System.out.println("commit time:"+(System.currentTimeMillis()-start6));

            System.out.println("all time:"+(System.currentTimeMillis()-start0));
            return wp_posts.getID();
			
			
		} catch (Exception e1) {
			e1.printStackTrace();
			if(transaction!=null)
				transaction.rollback();
			throw new RuntimeException(e1);
		}finally{
			if(session!=null)
			db.closeSession(session);
		}
		
	}


}
