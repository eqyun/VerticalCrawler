package com.pada.mydao.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.pada.mydao.configs.DatabaseConfig;

@Entity
@Table(name = DatabaseConfig.WP_PREX+"postmeta")
public class Wp_postmeta {
	
	private int meta_id;
	private int post_id;
	
	private String meta_key;
	private String meta_value;
	
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "meta_id")
	public int getMeta_id() {
		return meta_id;
	}
	public void setMeta_id(int meta_id) {
		this.meta_id = meta_id;
	}
	public int getPost_id() {
		return post_id;
	}
	public void setPost_id(int post_id) {
		this.post_id = post_id;
	}
	public String getMeta_key() {
		return meta_key;
	}
	public void setMeta_key(String meta_key) {
		this.meta_key = meta_key;
	}
	public String getMeta_value() {
		return meta_value;
	}
	public void setMeta_value(String meta_value) {
		this.meta_value = meta_value;
	}

}
