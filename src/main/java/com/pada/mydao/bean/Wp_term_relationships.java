package com.pada.mydao.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.pada.mydao.configs.DatabaseConfig;

@Entity
@Table(name = DatabaseConfig.WP_PREX+"term_relationships")
public class Wp_term_relationships implements java.io.Serializable{
	private int object_id;
	private int term_taxonomy_id;
	private int term_order;

	@Id
	@Column(name = "object_id")
	public int getObject_id() {
		return object_id;
	}

	public void setObject_id(int object_id) {
		this.object_id = object_id;
	}

	@Id
	@Column(name = "term_taxonomy_id")
	public int getTerm_taxonomy_id() {
		return term_taxonomy_id;
	}

	public void setTerm_taxonomy_id(int term_taxonomy_id) {
		this.term_taxonomy_id = term_taxonomy_id;
	}

	public int getTerm_order() {
		return term_order;
	}

	public void setTerm_order(int term_order) {
		this.term_order = term_order;
	}

}
