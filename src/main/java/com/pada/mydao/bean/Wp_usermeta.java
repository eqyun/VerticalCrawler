package com.pada.mydao.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.pada.mydao.configs.DatabaseConfig;

@Entity
@Table(name = DatabaseConfig.WP_PREX+"usermeta")
public class Wp_usermeta {
	
	private int umeta_id;
	private int user_id;
	private String meta_key;
	private String meta_value;
	

	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "umeta_id")
	public int getUmeta_id() {
		return umeta_id;
	}
	public void setUmeta_id(int umeta_id) {
		this.umeta_id = umeta_id;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
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
