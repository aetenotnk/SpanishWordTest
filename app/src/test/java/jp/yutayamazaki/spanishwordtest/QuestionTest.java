package jp.yutayamazaki.spanishwordtest;


import org.junit.Assert;
import org.junit.Test;

import jp.yutayamazaki.spanishwordtest.bean.Word;
import jp.yutayamazaki.spanishwordtest.bean.WordType;
import jp.yutayamazaki.spanishwordtest.manager.Question;
import jp.yutayamazaki.spanishwordtest.manager.WordTestManager;

public class QuestionTest {
    /**
     * (...)を空欄にできるかテスト
     */
    @Test
    public void blindSpanishExample() {
        Word word = new Word(
                "Hay que ...",
                "・・・しないといけない",
                "(Hay que) lavarse las manos antes de comer.",
                "食事の前に手を洗わないといけません。",
                new WordType("v", "動詞"));
        Question question = new Question(word, 1);

        Assert.assertEquals("(    ) lavarse las manos antes de comer.",
                question.getBlindSpanishExample());
    }

    /**
     * 正しい答えを判定できるかテスト
     */
    @Test
    public void evaluateOK() {
        Word word = new Word(
                "Hay que ...",
                "・・・しないといけない",
                "(Hay que) lavarse las manos antes de comer.",
                "食事の前に手を洗わないといけません。",
                new WordType("v", "動詞"));
        Question question = new Question(word, 1);

        Assert.assertEquals(WordTestManager.Grade.OK,
                question.evaluateAnswer("Hay que"));
    }

    /**
     * 間違った答えを判定できるかテスト
     */
    @Test
    public void evaluateNotOK() {
        Word word = new Word(
                "Hay que ...",
                "・・・しないといけない",
                "(Hay que) lavarse las manos antes de comer.",
                "食事の前に手を洗わないといけません。",
                new WordType("v", "動詞"));
        Question question = new Question(word, 1);

        Assert.assertEquals(WordTestManager.Grade.NOT_OK,
                question.evaluateAnswer("abc"));
    }
}
