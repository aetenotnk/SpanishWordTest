package jp.yutayamazaki.spanishwordtest.bean;

import java.io.Serializable;

public class Word extends Bean implements Serializable {
    private String wordSpanish;
    private String wordJapanese;
    private String exampleSpanish;
    private String exampleJapanese;
    private WordType type;

    public enum WordType{
        VERB, // 動詞
        ADJECTIVE, // 形容詞
        NOUN, // 名詞
        PREPOSITION, // 前置詞
        ADVERB; // 副詞

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
            if(wordType.toLowerCase().equals("preposition")){
                return PREPOSITION;
            }
            if(wordType.toLowerCase().equals("adverb")){
                return ADVERB;
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
            if(wordType == PREPOSITION){
                return "preposition";
            }
            if(wordType == ADVERB){
                return "adverb";
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
