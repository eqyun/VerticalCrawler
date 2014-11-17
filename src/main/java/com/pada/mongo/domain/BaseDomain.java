package com.pada.mongo.domain;

import org.springframework.data.annotation.Id;

public class BaseDomain {
    public BaseDomain() {
        super();
        this._class = this.getClass().getName();
        // TODO Auto-generated constructor stub
    }

    @Id
    private String id;

    private String _class;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String get_class() {
        return _class;
    }

    public void set_class(String _class) {
        this._class = _class;
    }


}
