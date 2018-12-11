package jp.yutayamazaki.spanishwordtest.bean;

/**
 * TestTitleのコレクションクラス
 * ※ スングルトン
 */
public class TestTitleCollection extends BeanCollection<TestTitle> {
    private static TestTitleCollection instance = null;

    /**
     * インスタンスを取得する
     * @return インスタンス
     */
    public static synchronized TestTitleCollection getInstance(){
        if(instance == null){
            instance = new TestTitleCollection();
        }

        return instance;
    }

    /**
     * インスタンスを破棄する
     */
    public void destroy(){
        instance = null;
    }

    @Override
    protected TestTitle createBean(String[] row) {
        return new TestTitle(Integer.parseInt(row[0]), row[1], row[2], row[3]);
    }
}
