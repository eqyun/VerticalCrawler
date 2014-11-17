package com.pada.spider.annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SpiderKeyInject {
	
	public Class<?> clazz();
	
	public String mappingValue();
	public String mappingKey() default "";

    public boolean isNecessity() default true;
	
	public int sign() default 0;//如果相同中的类，要实例2个，那这个变量表示第几个
	
}
