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
    private static String QUESTION_HAS_EXAMPLES = ".かっこ内を埋めなさい。";
    private static String QUESTION_NO_EXAMPLES = ".次の意味の単語を答えなさい。";

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

    public WordTestManager.Grade evaluateAnswer(List<String> answers) {
        return hasExamples() ? evaluateAnswerHasExamples(answers) : evaluateAnswerNoExample(answers);
    }

    public String getQuestionText() {
        return hasExamples() ? QUESTION_HAS_EXAMPLES : QUESTION_NO_EXAMPLES;
    }

    public String getQuestionSpanishText() {
        return hasExamples() ? getBlindSpanishExample() : "";
    }

    public String getQuestionJapaneseText() {
        return hasExamples() ? japaneseExample : japaneseWord;
    }

    public Word getWord() {
        return word;
    }

    public int getId() {
        return id;
    }

    public String getSpanishExample() {
        return spanishExample;
    }

    public boolean hasExamples() {
        return !spanishExample.equals("");
    }

    public int getAnswerCount() {
        Pattern pattern = Pattern.compile("\\(.*?\\)");
        Matcher matcher = pattern.matcher(spanishExample);

        return matcher.groupCount();
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

    private String getBlindSpanishExample() {
        Pattern pattern = Pattern.compile("\\(.*?\\)");
        Matcher matcher = pattern.matcher(spanishExample);

        return matcher.replaceAll(BLANK);
    }

    /**
     * 例文があるときの答えを評価する
     * @param answers ユーザの答え
     * @return 評価
     */
    private WordTestManager.Grade evaluateAnswerHasExamples(List<String> answers) {
        Pattern pattern = Pattern.compile("\\((.*?)\\)");
        Matcher matcher = pattern.matcher(spanishExample);

        int i = 0;
        while(matcher.find()) {
            if(!answers.get(i).toLowerCase().equals(matcher.group(1).toLowerCase())) {
                return WordTestManager.Grade.NOT_OK;
            }
            i++;
        }

        return WordTestManager.Grade.OK;
    }

    private WordTestManager.Grade evaluateAnswerNoExample(List<String> answers) {
        Pattern pattern1 = Pattern.compile("[^a-zA-ZáéíñóúüÁÉÍÑÓÚÜ\\s]");
        Pattern pattern2 = Pattern.compile("[^a-zA-ZáéíñóúüÁÉÍÑÓÚÜ()\\s]");
        Pattern kakkoPattern = Pattern.compile("\\((.*)\\)");
        String answer = answers.get(0);

        for(String word : spanishWords) {
            // 単語から記号と前後のスペースを削除
            String ignoreSign  = pattern1.matcher(word).replaceAll("").trim();

            if(ignoreSign.toLowerCase().equals(answer.toLowerCase())) {
                return WordTestManager.Grade.OK;
            }

            ignoreSign = pattern2.matcher(word).replaceAll("").trim();
            String correct = kakkoPattern.matcher(ignoreSign).replaceAll("").trim();

            if(correct.toLowerCase().equals(answer.toLowerCase())) {
                return WordTestManager.Grade.OK;
            }
        }

        return WordTestManager.Grade.NOT_OK;
    }
}
