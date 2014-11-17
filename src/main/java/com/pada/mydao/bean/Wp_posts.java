package com.pada.mydao.bean;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.pada.mydao.configs.DatabaseConfig;
@Entity
@Table(name = DatabaseConfig.WP_PREX+"posts")
public class Wp_posts {
	
	
	private int ID;
	
	private int comment_count = 0;
	
	private int post_author;
	private int post_parent = 0;
	private int menu_order;
	
	
	private Date post_date = new Date();
	private Date post_date_gmt= new Date();
	private Date post_modified = new Date();
	private Date post_modified_gmt = new Date();
	
	
	private String post_content ="";
	private String post_content_filtered = "";
	private String post_title = "";
	private String post_excerpt = "";
	private String to_ping = "";
	private String pinged = "";
	private String post_mime_type ="";
	private String post_status = "publish";
	private String comment_status = "open";
	private String ping_status = "open";
	private String post_password = "";
	private String post_type = "post";
	private String post_name = "";
	private String guid;
	
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "ID")
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public int getComment_count() {
		return comment_count;
	}
	public void setComment_count(int comment_count) {
		this.comment_count = comment_count;
	}
	public int getPost_author() {
		return post_author;
	}
	public void setPost_author(int post_author) {
		this.post_author = post_author;
	}
	public int getPost_parent() {
		return post_parent;
	}
	public void setPost_parent(int post_parent) {
		this.post_parent = post_parent;
	}
	public int getMenu_order() {
		return menu_order;
	}
	public void setMenu_order(int menu_order) {
		this.menu_order = menu_order;
	}
	public Date getPost_date() {
		return post_date;
	}
	public void setPost_date(Date post_date) {
		this.post_date = post_date;
	}
	public Date getPost_date_gmt() {
		return post_date_gmt;
	}
	public void setPost_date_gmt(Date post_date_gmt) {
		this.post_date_gmt = post_date_gmt;
	}
	public Date getPost_modified() {
		return post_modified;
	}
	public void setPost_modified(Date post_modified) {
		this.post_modified = post_modified;
	}
	public Date getPost_modified_gmt() {
		return post_modified_gmt;
	}
	public void setPost_modified_gmt(Date post_modified_gmt) {
		this.post_modified_gmt = post_modified_gmt;
	}
	public String getPost_content() {
		return post_content;
	}
	public void setPost_content(String post_content) {
		this.post_content = post_content;
	}
	public String getPost_content_filtered() {
		return post_content_filtered;
	}
	public void setPost_content_filtered(String post_content_filtered) {
		this.post_content_filtered = post_content_filtered;
	}
	public String getPost_title() {
		return post_title;
	}
	public void setPost_title(String post_title) {
		this.post_title = post_title;
	}
	public String getPost_excerpt() {
		return post_excerpt;
	}
	public void setPost_excerpt(String post_excerpt) {
		this.post_excerpt = post_excerpt;
	}
	public String getTo_ping() {
		return to_ping;
	}
	public void setTo_ping(String to_ping) {
		this.to_ping = to_ping;
	}
	public String getPinged() {
		return pinged;
	}
	public void setPinged(String pinged) {
		this.pinged = pinged;
	}
	public String getPost_mime_type() {
		return post_mime_type;
	}
	public void setPost_mime_type(String post_mime_type) {
		this.post_mime_type = post_mime_type;
	}
	public String getPost_status() {
		return post_status;
	}
	public void setPost_status(String post_status) {
		this.post_status = post_status;
	}
	public String getComment_status() {
		return comment_status;
	}
	public void setComment_status(String comment_status) {
		this.comment_status = comment_status;
	}
	public String getPing_status() {
		return ping_status;
	}
	public void setPing_status(String ping_status) {
		this.ping_status = ping_status;
	}
	public String getPost_password() {
		return post_password;
	}
	public void setPost_password(String post_password) {
		this.post_password = post_password;
	}
	public String getPost_type() {
		return post_type;
	}
	public void setPost_type(String post_type) {
		this.post_type = post_type;
	}
	public String getPost_name() {
		return post_name;
	}
	public void setPost_name(String post_name) {
		this.post_name = post_name;
	}
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}

}
