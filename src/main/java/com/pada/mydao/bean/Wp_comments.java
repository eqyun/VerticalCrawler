package com.pada.mydao.bean;

import com.pada.mydao.configs.DatabaseConfig;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by eqyun on 2014/11/11.
 */
@Entity
@Table(name = DatabaseConfig.WP_PREX+"comments")
public class Wp_comments {

    private int comment_ID;

    private int comment_post_ID;

    private String comment_author;

    private String comment_author_email = "";

    private String comment_author_url = "";
    private String comment_author_IP = "";

    private Date comment_date = new Date();


    private Date comment_date_gmt = new Date();

    private String comment_content;

    private int comment_karma = 0;

    private String comment_approved = "1";
    private String comment_agent = "";

    private String comment_type = "";

    private int comment_parent = 0;

    private int user_id = 0;


    public Wp_comments() {
    }

    public Wp_comments(int comment_post_ID, String comment_author, String comment_content, String comment_type, int user_id) {
        this.comment_post_ID = comment_post_ID;
        this.comment_author = comment_author;
        this.comment_content = comment_content;
        this.comment_type = comment_type;
        this.user_id = user_id;
    }

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    @Column(name = "comment_ID")
    public int getComment_ID() {
        return comment_ID;
    }

    public void setComment_ID(int comment_ID) {
        this.comment_ID = comment_ID;
    }

    public int getComment_post_ID() {
        return comment_post_ID;
    }

    public void setComment_post_ID(int comment_post_ID) {
        this.comment_post_ID = comment_post_ID;
    }

    public String getComment_author() {
        return comment_author;
    }

    public void setComment_author(String comment_author) {
        this.comment_author = comment_author;
    }

    public String getComment_author_email() {
        return comment_author_email;
    }

    public void setComment_author_email(String comment_author_email) {
        this.comment_author_email = comment_author_email;
    }

    public String getComment_author_url() {
        return comment_author_url;
    }

    public void setComment_author_url(String comment_author_url) {
        this.comment_author_url = comment_author_url;
    }

    public String getComment_author_IP() {
        return comment_author_IP;
    }

    public void setComment_author_IP(String comment_author_IP) {
        this.comment_author_IP = comment_author_IP;
    }

    public Date getComment_date() {
        return comment_date;
    }

    public void setComment_date(Date comment_date) {
        this.comment_date = comment_date;
    }

    public Date getComment_date_gmt() {
        return comment_date_gmt;
    }

    public void setComment_date_gmt(Date comment_date_gmt) {
        this.comment_date_gmt = comment_date_gmt;
    }

    public String getComment_content() {
        return comment_content;
    }

    public void setComment_content(String comment_content) {
        this.comment_content = comment_content;
    }

    public int getComment_karma() {
        return comment_karma;
    }

    public void setComment_karma(int comment_karma) {
        this.comment_karma = comment_karma;
    }

    public String getComment_approved() {
        return comment_approved;
    }

    public void setComment_approved(String comment_approved) {
        this.comment_approved = comment_approved;
    }

    public String getComment_agent() {
        return comment_agent;
    }

    public void setComment_agent(String comment_agent) {
        this.comment_agent = comment_agent;
    }

    public String getComment_type() {
        return comment_type;
    }

    public void setComment_type(String comment_type) {
        this.comment_type = comment_type;
    }

    public int getComment_parent() {
        return comment_parent;
    }

    public void setComment_parent(int comment_parent) {
        this.comment_parent = comment_parent;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
