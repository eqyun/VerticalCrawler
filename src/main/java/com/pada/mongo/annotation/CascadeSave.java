package com.pada.mongo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by sinceow on 2014/8/28.
 * <p/>
 * 用于MongoDB @DBRef 的级联存储
 * <p/>
 * {@link //maciejwalkowiak.pl/blog/2012/04/30/spring-data-mongodb-cascade-save-on-dbref-objects/}
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface CascadeSave {

}
