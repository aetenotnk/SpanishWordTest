package jp.yutayamazaki.spanishwordtest.bean;


public class Word extends Bean {
    private String wordSpanish;
    private String wordJapanese;
    private String exampleSpanish;
    private String exampleJapanese;
    private WordType type;

    public Word(String wordSpanish, String wordJapanese,
                String exampleSpanish, String exampleJapanese,
                WordType wordType){
        this.wordSpanish = wordSpanish;
        this.wordJapanese = wordJapanese;
        this.exampleSpanish = exampleSpanish;
        this.exampleJapanese = exampleJapanese;
        this.type = wordType;
    }

    public String getWordSpanish() {
        return wordSpanish;
    }

    public String getWordJapanese() {
        return wordJapanese;
    }

    public String getExampleSpanish() {
        return exampleSpanish;
    }

    public String getExampleJapanese() {
        return exampleJapanese;
    }

    public WordType getType() {
        return type;
    }
}
