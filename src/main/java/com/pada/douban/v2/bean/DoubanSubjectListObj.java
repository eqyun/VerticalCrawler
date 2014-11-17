package com.pada.douban.v2.bean;

import com.google.api.client.util.Key;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Sean Guo <seanguo85@qq.com>
 *
 */
public class DoubanSubjectListObj implements IDoubanObject {
  
  @Key
  private int count;
  
  @Key
  private int start;
  
  @Key
  private int total;
  
  @Key
  private List<DoubanSubjectObj> subjects = new ArrayList<DoubanSubjectObj>();
  
  @Key
  private String title;

  @Override
  public String getObjName() {
    return "DoubanSubjectList";
  }

  public int getCount() {
    return count;
  }

  public int getStart() {
    return start;
  }

  public int getTotal() {
    return total;
  }

  public List<DoubanSubjectObj> getSubjects() {
    return subjects;
  }

  public String getTitle() {
    return title;
  }
}
