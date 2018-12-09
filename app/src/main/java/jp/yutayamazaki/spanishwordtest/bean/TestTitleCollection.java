package jp.yutayamazaki.spanishwordtest.bean;

public class TestTitleCollection extends BeanCollection<TestTitle> {
    private static TestTitleCollection instance = null;

    public static synchronized TestTitleCollection getInstance(){
        if(instance == null){
            instance = new TestTitleCollection();
        }

        return instance;
    }

    public void destroy(){
        instance = null;
    }

    @Override
    protected TestTitle createBean(String[] row) {
        return new TestTitle(Integer.parseInt(row[0]), row[1], row[2]);
    }
}
