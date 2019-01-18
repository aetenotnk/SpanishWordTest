package jp.yutayamazaki.spanishwordtest;


import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

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
                question.getQuestionSpanishText());
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

    /**
     * 2つの例文を持つ単語データで2つとも問題として設定されるかテスト
     */
    @Test
    public void randomTowSpanishExamples() {
        final int LOOP_COUNT = 100;

        Word word = new Word(
                "Spanish Word",
                "Japanese Word",
                "Example1 / Example2",
                "例文1 / 例文2",
                new WordType("v", "動詞")
        );
        List<String> examples = new LinkedList<>();

        for(int i = 0;i < LOOP_COUNT;i++) {
            Question question = new Question(word, i);

            examples.add(question.getSpanishExample());
        }

        Assert.assertTrue(examples.contains("Example1"));
        Assert.assertTrue(examples.contains("Example2"));
    }

    /**
     * 3つの例文を持つ単語データで2つとも問題として設定されるかテスト
     */
    @Test
    public void randomThreeSpanishExamples() {
        final int LOOP_COUNT = 150;

        Word word = new Word(
                "Spanish Word",
                "Japanese Word",
                "Example1 / Example2 / Example3",
                "例文1 / 例文2 / 例文3",
                new WordType("v", "動詞")
        );
        List<String> examples = new LinkedList<>();

        for(int i = 0;i < LOOP_COUNT;i++) {
            Question question = new Question(word, i);

            examples.add(question.getSpanishExample());
        }

        Assert.assertTrue(examples.contains("Example1"));
        Assert.assertTrue(examples.contains("Example2"));
        Assert.assertTrue(examples.contains("Example3"));
    }
}
