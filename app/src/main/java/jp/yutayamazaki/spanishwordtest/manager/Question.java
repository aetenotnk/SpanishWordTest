package jp.yutayamazaki.spanishwordtest.manager;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.yutayamazaki.spanishwordtest.bean.Word;

public class Question implements Serializable {
    private static Random random = new Random();
    private static String BLANK = "(    )";
    private static String SEPARATOR = " / ";

    private Word word;
    private int id;
    private List<String> spanishWords;
    private String japaneseWord;
    private String spanishExample;
    private String japaneseExample;

    public Question(Word word, int id) {
        this.word = word;
        this.id = id;

        setWords();
        setExamples();
    }

    public WordTestManager.Grade evaluateAnswer(String answer) {
        Pattern pattern = Pattern.compile("\\((.*)\\)");
        Matcher matcher = pattern.matcher(spanishExample);

        // データが不正で()がないかもしれないので
        // Matcher#findでパターンが見つかったかチェックする
        if(matcher.find() && answer.toLowerCase().equals(matcher.group(1).toLowerCase())){
            return WordTestManager.Grade.OK;
        }

        return WordTestManager.Grade.NOT_OK;
    }

    public String getBlindSpanishExample() {
        Pattern pattern = Pattern.compile("\\(.*\\)");
        Matcher matcher = pattern.matcher(spanishExample);

        return matcher.replaceAll(BLANK);
    }

    public Word getWord() {
        return word;
    }

    public int getId() {
        return id;
    }

    public List<String> getSpanishWords() {
        return spanishWords;
    }

    public String getWordJapanese() {
        return japaneseWord;
    }

    public String getSpanishExample() {
        return spanishExample;
    }

    public String getJapaneseExample() {
        return japaneseExample;
    }

    private void setWords() {
        this.spanishWords = Arrays.asList(word.getWordSpanish().split(SEPARATOR));
        this.japaneseWord = word.getWordJapanese();
    }

    private void setExamples() {
        String[] examplesSpanish = word.getExampleSpanish().split(SEPARATOR);
        String[] examplesJapanese = word.getExampleJapanese().split(SEPARATOR);
        int index = random.nextInt(examplesSpanish.length);

        spanishExample = examplesSpanish[index];
        japaneseExample = examplesJapanese[index];
    }
}
