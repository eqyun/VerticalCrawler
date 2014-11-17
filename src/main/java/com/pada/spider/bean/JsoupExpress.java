package com.pada.spider.bean;

/**
 * Created by eqyun on 2014/10/31.
 */
public class JsoupExpress {

    private String seekDom;
    private String selectAttr;
    private String filterDom;
    
    private boolean removeStyle = true;
    
    private boolean isHtml = false;
    /**
     * 
     * @param seekDom
     * @param selectAttr
     * @param filterDom
     * @param isHtml String for Html format or Text format
     */
    public JsoupExpress(String seekDom, String selectAttr, String filterDom,boolean isHtml) {
        this.seekDom = seekDom;
        this.selectAttr = selectAttr;
        this.filterDom = filterDom;
        this.isHtml = isHtml;
    }
    /**
     * 
     * @param seekDom
     * @param selectAttr
     * @param filterDom
     * @param isHtml 
     * @param removeStyle default is not remove style
     */
    public JsoupExpress(String seekDom, String selectAttr, String filterDom,boolean isHtml,boolean removeStyle) {
        this.seekDom = seekDom;
        this.selectAttr = selectAttr;
        this.filterDom = filterDom;
        this.isHtml = isHtml;
    }
    

    public JsoupExpress() {
    }

    public String getSeekDom() {
        return seekDom;
    }

    public void setSeekDom(String seekDom) {
        this.seekDom = seekDom;
    }

    public String getSelectAttr() {
        return selectAttr;
    }

    public void setSelectAttr(String selectAttr) {
        this.selectAttr = selectAttr;
    }

    public String getFilterDom() {
        return filterDom;
    }

    public void setFilterDom(String filterDom) {
        this.filterDom = filterDom;
    }

	public boolean isHtml() {
		return isHtml;
	}

	public void setHtml(boolean isHtml) {
		this.isHtml = isHtml;
	}

	public boolean isRemoveStyle() {
		return removeStyle;
	}

	public void setRemoveStyle(boolean removeStyle) {
		this.removeStyle = removeStyle;
	}
}
