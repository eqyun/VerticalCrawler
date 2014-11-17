package com.pada.mydao.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.pada.mydao.configs.DatabaseConfig;

@Entity
@Table(name = DatabaseConfig.WP_PREX+"terms")
public class Wp_terms {



	private int term_group =0;
	private int term_id;
	private String name;
	private String slug;

    public Wp_terms() {
    }

    public Wp_terms(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }

    public int getTerm_group() {
		return term_group;
	}
	public void setTerm_group(int term_group) {
		this.term_group = term_group;
	}
	
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "term_id")
	public int getTerm_id() {
		return term_id;
	}
	public void setTerm_id(int term_id) {
		this.term_id = term_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSlug() {
		return slug;
	}
	public void setSlug(String slug) {
		this.slug = slug;
	}
}
