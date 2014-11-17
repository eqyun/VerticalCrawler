package com.pada.douban.v2.bean;

import com.google.api.client.util.Key;

/**
 * 
 * @author Sean Guo <seanguo85@qq.com>
 *
 */
public class DoubanDirectorObj implements IDoubanObject {
  
  @Key
  private DoubanImageObj avatars;
  
  @Key
  private String alt;
  
  @Key
  private String id;
  
  @Key
  private String name;

  @Override
  public String getObjName() {
    return "DoubanDirectorObj";
  }

  public DoubanImageObj getAvatars() {
    return avatars;
  }

  public String getAlt() {
    return alt;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "DoubanDirectorObj [avatars=" + avatars + ", alt=" + alt + ", id=" + id + ", name=" + name + "]";
  }
}
