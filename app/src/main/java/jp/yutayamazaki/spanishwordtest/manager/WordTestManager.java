package jp.yutayamazaki.spanishwordtest.manager;

import android.util.Pair;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import jp.yutayamazaki.spanishwordtest.bean.TestTitle;
import jp.yutayamazaki.spanishwordtest.bean.Word;
import jp.yutayamazaki.spanishwordtest.bean.WordCollection;

public class WordTestManager implements Serializable {
    private static String BLANK = "(    )";
    private static String EXAMPLE_SPLIT = " / ";

    private TestTitle testTitle;
    private List<Word> words;
    private List<Word> questionWords;
    private int testCount;
    private int currentTestCount;
    private List<String> answers;

    /**
     * 各問題のの評価
     */
    public enum Grade{
        OK(1),
        NOT_OK(0);

        private float score;

        Grade(float score){
            this.score = score;
        }

        public float getScore() {
            return score;
        }

        @Override
        public String toString() {
            switch (this){
                case OK:
                    return "⭕";
                case NOT_OK:
                    return "❌";
            }

            return super.toString();
        }
    }

    public WordTestManager(TestTitle testTitle, WordCollection wordCollection){
        this.testTitle = testTitle;
        this.words = wordCollection.selectAll();
        this.questionWords = new LinkedList<>();
        this.answers = new LinkedList<>();
    }

    /**
     * 初期化
     * @param testCount 要求されたテスト数
     */
    public void init(int testCount){
        initTestCount(testCount);
        initQuestionWords();
        initAnswers();
    }

    /**
     * 現在の問題の解答を設定する
     * @param input ユーザーの解答
     */
    public void setCurrentAnswer(String input){
        answers.set(currentTestCount, input);
    }

    /**
     * 現在の問題のユーザーの解答を取得する
     * @return 現在の問題のユーザーの解答
     */
    public String getCurrentAnswer(){
        return answers.get(currentTestCount);
    }

    /**
     * ユーザーの解答を取得する
     * @param index 何番目の解答か
     * @return index番目の解答
     */
    public String getAnswer(int index){
        return answers.get(index);
    }

    public String getTitle(){
        return testTitle.getTitle();
    }

    public Word getCurrentWord(){
        return questionWords.get(currentTestCount);
    }

    /**
     * 問題に出した単語をすべて取得する
     * @return 問題のすべての単語とその問題のインデックスのペアリスト
     */
    public List<Pair<Word, Integer>> getQuestionWordPairs(){
        List<Pair<Word, Integer>> res = new LinkedList<>();

        for(int i = 0;i < questionWords.size();i++){
            res.add(new Pair<>(questionWords.get(i), i));
        }

        return res;
    }

    /**
     * 正解した単語をすべて取得する
     * @return 正解した単語とその問題のインデックスのペアリスト
     */
    public List<Pair<Word, Integer>> getCorrectWordPairs(){
        List<Pair<Word, Integer>> res = new LinkedList<>();

        for(int i = 0;i < questionWords.size();i++){
            Word word = questionWords.get(i);
            String answer = answers.get(i);

            if(evaluate(word, 0, answer) == Grade.OK){
                res.add(new Pair<>(word, i));
            }
        }

        return res;
    }

    /**
     * 間違えた単語をすべて取得する
     * @return 間違えた単語とその問題のインデックスのペアリスト
     */
    public List<Pair<Word, Integer>> getMistakeWordPairs(){
        List<Pair<Word, Integer>> res = new LinkedList<>();

        for(int i = 0;i < questionWords.size();i++){
            Word word = questionWords.get(i);
            String answer = answers.get(i);

            if(evaluate(word, 0, answer) == Grade.NOT_OK){
                res.add(new Pair<>(word, i));
            }
        }

        return res;
    }

    public void next(){
        currentTestCount++;
    }

    public void previous(){
        currentTestCount--;
    }

    public boolean isFirst(){
        return currentTestCount == 0;
    }

    public boolean isLast(){
        return currentTestCount == (questionWords.size() - 1);
    }

    public int getCurrentTestCount(){
        return currentTestCount;
    }

    public int getTestCount(){
        return testCount;
    }

    /**
     * テストのスコアを取得する
     * @return テストのスコア
     */
    public float getScore(){
        float score = 0;

        for(int i = 0;i < questionWords.size();i++){
            score += evaluate(questionWords.get(i), 0, answers.get(i)).getScore();
        }

        return score;
    }

    /**
     * テストの満点のときのスコアを取得する
     * @return テストの満点の時のスコア
     */
    public float getMaxScore(){
        // すべてOKの時のスコアを返す
        return questionWords.size() * Grade.OK.getScore();
    }

    /**
     * かっこ内を隠したスペイン語の例文を取得する
     * @param word 単語
     * @return かっこ内を隠したスペイン語の例文
     */
    public static String getBlindSpanishExample(Word word, int index){
        Pattern pattern = Pattern.compile("\\(.*\\)");
        Matcher matcher = pattern.matcher(getSpanishExample(word, index));

        return matcher.replaceAll(BLANK);
    }

    public static String getSpanishExample(Word word, int index){
        return word.getExampleSpanish().split(EXAMPLE_SPLIT)[index];
    }

    public static String getJapaneseExample(Word word, int index){
        return word.getExampleJapanese().split(EXAMPLE_SPLIT)[index];
    }

    public Grade getEvaluate(int questionIndex, int exampleIndex){
        return evaluate(questionWords.get(questionIndex), exampleIndex, answers.get(questionIndex));
    }

    /**
     * 解答を評価する
     * @param word 単語
     * @param index 例文のインデックス
     * @param answer ユーザーの解答
     * @return ユーザーの解答の評価
     */
    public static Grade evaluate(Word word, int index, String answer){
        String spanishExample = word.getExampleSpanish().split(EXAMPLE_SPLIT)[index];
        Pattern pattern = Pattern.compile("\\((.*)\\)");
        Matcher matcher = pattern.matcher(spanishExample);

        // データが不正で()がないかもしれないので
        // Matcher#findでパターンが見つかったかチェックする
        if(matcher.find() && answer.toLowerCase().equals(matcher.group(1).toLowerCase())){
            return Grade.OK;
        }

        return Grade.NOT_OK;
    }

    /**
     * テスト数の初期化
     * @param testCount 要求されたテスト数
     */
    private void initTestCount(int testCount){
        int validWordCount = getValidWordCount();

        // テスト数が有効な単語数を超えないよう設定
        this.testCount = testCount < validWordCount ? testCount : validWordCount;
        this.currentTestCount = 0;
    }

    /**
     * 出題する単語を設定する
     */
    private void initQuestionWords(){
        // 例文を含む単語だけ抽出
        List<Word> filteredWords = getValidWords();

        questionWords.clear();

        // 抽出した単語からランダムに出題する問題を設定
        Collections.shuffle(filteredWords);
        for(int i = 0;i < testCount;i++){
            questionWords.add(filteredWords.get(i));
        }
    }

    /**
     * 解答リストを初期化する
     */
    private void initAnswers(){
        answers.clear();

        // 解答リストを問題数分確保し空の文字列で埋める
        for(int i = 0;i < testCount;i++){
            answers.add("");
        }
    }

    /**
     * 問題として有効な単語の数を取得する
     * @return 問題として有効な単語の数
     */
    private int getValidWordCount(){
        return getValidWords().size();
    }

    /**
     * 問題として有効な単語を抽出する
     * @return 問題として有効な単語リスト
     */
    public List<Word> getValidWords(){
        return words.stream()
                .filter(w -> !w.getExampleSpanish().equals(""))
                .collect(Collectors.toList());
    }
}
