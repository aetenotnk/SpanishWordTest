package jp.yutayamazaki.spanishwordtest.manager;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import jp.yutayamazaki.spanishwordtest.bean.TestTitle;
import jp.yutayamazaki.spanishwordtest.bean.Word;
import jp.yutayamazaki.spanishwordtest.bean.WordCollection;

public class WordTestManager implements Serializable {
    private TestTitle testTitle;
    private List<Word> words;
    private List<Question> questions;
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
        this.questions = new LinkedList<>();
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

    public String getTitle(){
        return testTitle.getTitle();
    }

    public Question getCurrentWord(){
        return questions.get(currentTestCount);
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
        return currentTestCount == (questions.size() - 1);
    }

    public int getCurrentTestCount(){
        return currentTestCount;
    }

    /**
     * 問題をすべて得る
     * @return 問題のリストをコピーしたもの
     */
    public List<Question> getQuestions() {
        return new LinkedList<>(questions);
    }

    /**
     * 正解した問題をすべて得る
     * @return 正解した問題のリスト
     */
    public List<Question> getCorrectQuestions() {
        return getFilteredQuestionsByGrade(Grade.OK);
    }

    /**
     * 間違えた問題をすべて得る
     * @return 間違えた問題のリスト
     */
    public List<Question> getMistakeQuestions() {
        return getFilteredQuestionsByGrade(Grade.NOT_OK);
    }

    /**
     * テストのスコアを取得する
     * @return テストのスコア
     */
    public float getScore(){
        float score = 0;

        for(int i = 0; i < questions.size(); i++){
            score += questions.get(i).evaluateAnswer(answers.get(i)).score;
        }

        return score;
    }

    /**
     * テストの満点のときのスコアを取得する
     * @return テストの満点の時のスコア
     */
    public float getMaxScore(){
        // すべてOKの時のスコアを返す
        return questions.size() * Grade.OK.getScore();
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

    public Grade getEvaluate(int questionIndex){
        return questions.get(questionIndex).evaluateAnswer(answers.get(questionIndex));
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

        questions.clear();

        // 抽出した単語からランダムに出題する問題を設定
        Collections.shuffle(filteredWords);
        for(int i = 0;i < testCount;i++){
            Word word = filteredWords.get(i);
            int id = i + 1;

            questions.add(new Question(word, id));
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

    private List<Question> getFilteredQuestionsByGrade(Grade grade) {
        List<Question> res = new LinkedList<>();

        for(int i = 0;i < questions.size();i++) {
            Question question = questions.get(i);
            String answer = answers.get(i);

            if(question.evaluateAnswer(answer) == grade) {
                res.add(question);
            }
        }

        return res;
    }
}
