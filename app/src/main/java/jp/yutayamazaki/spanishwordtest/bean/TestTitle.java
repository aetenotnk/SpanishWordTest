package jp.yutayamazaki.spanishwordtest.bean;

import java.io.Serializable;

public class TestTitle extends Bean implements Serializable {
    private int id;
    private String title;
    private String caption;
    private String filepath;

    public TestTitle(int id, String title, String caption, String filepath) {
        this.id = id;
        this.title = title;
        this.caption = caption;
        this.filepath = filepath;
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

    public String getFilepath() {
        return filepath;
    }
}
