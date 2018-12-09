package jp.yutayamazaki.spanishwordtest.bean;

import junit.framework.Test;

public class TestTitle implements Bean {
    private int id;
    private String title;
    private String caption;

    public TestTitle(int id, String title, String caption) {
        this.id = id;
        this.title = title;
        this.caption = caption;
    }

    public TestTitle(TestTitle other){
        this(other.id, other.title, other.caption);
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCaption() {
        return caption;
    }

    @Override
    public Bean copy() {
        return new TestTitle(this);
    }
}
