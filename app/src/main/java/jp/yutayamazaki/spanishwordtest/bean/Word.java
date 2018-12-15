package jp.yutayamazaki.spanishwordtest.bean;

import java.io.Serializable;

public class Word implements Bean, Serializable {
    private String wordSpanish;
    private String wordJapanese;
    private String exampleSpanish;
    private String exampleJapanese;
    private WordType type;

    public enum WordType{
        VERB,
        ADJECTIVE,
        NOUN;

        public static WordType getEnum(String wordType){
            if(wordType.toLowerCase().equals("v")){
                return VERB;
            }
            if(wordType.toLowerCase().equals("adjective")){
                return ADJECTIVE;
            }
            if(wordType.toLowerCase().equals("noun")){
                return NOUN;
            }

            return null;
        }

        public static String getString(WordType wordType){
            if(wordType == VERB){
                return "v";
            }
            if(wordType == ADJECTIVE){
                return "adjective";
            }
            if(wordType == NOUN){
                return "noun";
            }

            return null;
        }
    }

    public Word(String wordSpanish, String wordJapanese,
                String exampleSpanish, String exampleJapanese,
                String wordType){
        this.wordSpanish = wordSpanish;
        this.wordJapanese = wordJapanese;
        this.exampleSpanish = exampleSpanish;
        this.exampleJapanese = exampleJapanese;
        this.type = WordType.getEnum(wordType);
    }

    @Override
    public Bean copy() {
        return new Word(this.wordSpanish, this.wordJapanese,
                this.exampleSpanish, this.exampleJapanese,
                WordType.getString(this.type));
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
