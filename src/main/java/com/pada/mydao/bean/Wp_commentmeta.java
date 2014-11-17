package com.pada.mydao.bean;

import com.pada.mydao.configs.DatabaseConfig;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by eqyun on 2014/11/11.
 */
@Entity
@Table(name = DatabaseConfig.WP_PREX+"commentmeta")
public class Wp_commentmeta {

    private int meta_id;
    private int comment_id;
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

    public int getComment_id() {
        return comment_id;
    }

    public void setComment_id(int comment_id) {
        this.comment_id = comment_id;
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
