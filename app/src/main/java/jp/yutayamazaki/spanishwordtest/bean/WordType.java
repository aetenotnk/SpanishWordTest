package jp.yutayamazaki.spanishwordtest.bean;

public class WordType extends Bean {
    private String wordTypeString;
    private String wordTypeDisplay;

    public WordType(String wordTypeString, String wordTypeDisplay){
        this.wordTypeString = wordTypeString;
        this.wordTypeDisplay = wordTypeDisplay;
    }

    /**
     * コンストラクタ
     * @param row CSVファイルの行
     */
    public WordType(String... row){
        this(row[0], row[1]);
    }

    public String getWordTypeString() {
        return wordTypeString;
    }

    public String getWordTypeDisplay() {
        return wordTypeDisplay;
    }
}
