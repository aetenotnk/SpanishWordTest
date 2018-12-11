package jp.yutayamazaki.spanishwordtest.bean;

public class TestTitle implements Bean {
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

    public TestTitle(TestTitle other){
        this(other.id, other.title, other.caption, other.filepath);
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
