package com.pada.mydao.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.pada.mydao.configs.DatabaseConfig;
@Entity
@Table(name = DatabaseConfig.WP_PREX+"term_taxonomy")
public class Wp_term_taxonomy {
	
	private int term_taxonomy_id;
	private int count = 0;
	private int term_id;
	private int parent;
	
	private String description ="";
	private String taxonomy;
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "term_taxonomy_id")
	public int getTerm_taxonomy_id() {
		return term_taxonomy_id;
	}
	public void setTerm_taxonomy_id(int term_taxonomy_id) {
		this.term_taxonomy_id = term_taxonomy_id;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getTerm_id() {
		return term_id;
	}
	public void setTerm_id(int term_id) {
		this.term_id = term_id;
	}
	public int getParent() {
		return parent;
	}
	public void setParent(int parent) {
		this.parent = parent;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTaxonomy() {
		return taxonomy;
	}
	public void setTaxonomy(String taxonomy) {
		this.taxonomy = taxonomy;
	}
	
	


}
