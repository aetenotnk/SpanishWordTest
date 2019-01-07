package jp.yutayamazaki.spanishwordtest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.lang.reflect.Field;
import java.util.List;

import jp.yutayamazaki.spanishwordtest.bean.TestTitle;
import jp.yutayamazaki.spanishwordtest.bean.Word;
import jp.yutayamazaki.spanishwordtest.bean.WordCollection;
import jp.yutayamazaki.spanishwordtest.bean.WordType;
import jp.yutayamazaki.spanishwordtest.manager.WordTestManager;

@RunWith(RobolectricTestRunner.class)
public class WordTestManagerTest {
    private TestTitle testTitle;
    private WordCollection wordCollection;

    @Before
    public void setUp(){
        TestUtil.setWordType();

        wordCollection = new WordCollection(RuntimeEnvironment.application, 1);

        testTitle =
                new TestTitle(1, "Test1", "Caption1", "/dummy.csv");

        wordCollection.insertOrUpdate(
                new Word(
                        "Hay que ...",
                        "・・・しないといけない",
                        "(Hay que) lavarse las manos antes de comer.",
                        "食事の前に手を洗わないといけません。",
                        new WordType("v", "動詞")));
        wordCollection.insertOrUpdate(
                new Word(
                        "¿Puedo ...?",
                        "私は・・・してもいいですか？",
                        "¿(Puedo) pasar?",
                        "食事の前に手を洗わないといけません。",
                        new WordType("v", "動詞")));
        wordCollection.insertOrUpdate(
                new Word(
                        "caliente",
                        "熱い",
                        "",
                        "",
                        new WordType("v", "動詞")));
    }

    @After
    public void tearDown(){

    }

    /**
     * 初期化テスト
     * @throws Exception WordTestManager#questionWordsにアクセスできないと例外を投げる
     */
    @SuppressWarnings("unchecked")
    @Test
    public void initTest() throws Exception{
        WordTestManager manager = new WordTestManager(testTitle, wordCollection);

        manager.init(2);

        Field wordsField = manager.getClass().getDeclaredField("questionWords");
        wordsField.setAccessible(true);
        List<Word> words = (List<Word>)wordsField.get(manager);

        // 単語が2つ設定できているかチェック
        Assert.assertEquals(2, words.size());

        // 設定した単語に例文を含むものだけかチェック
        for(Word word : words){
            Assert.assertNotEquals("", word.getExampleSpanish());
        }
    }

    /**
     * WordTestManager#initの引数に有効な単語数より大きいときのテスト
     * @throws Exception WordTestManager#questionWordsにアクセスできないと例外を投げる
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testCountTest() throws Exception {
        WordTestManager manager = new WordTestManager(testTitle, wordCollection);

        // テスト数が有効な単語数より多い
        manager.init(Integer.MAX_VALUE);

        Field wordsField = manager.getClass().getDeclaredField("questionWords");
        wordsField.setAccessible(true);
        List<Word> words = (List<Word>) wordsField.get(manager);

        // 有効な単語すべてが問題になっていること
        Assert.assertEquals(2, words.size());
    }

    /**
     * WordTestManager#getBlindSpanishExampleに
     * 単語とインデックスを指定してかっこ内を空欄にした文字列が取得できるかテスト
     */
    @Test
    public void blindSpanishExample(){
        Word word1 = new Word(
                "Hay que ...",
                "・・・しないといけない",
                "(Hay que) lavarse las manos antes de comer.",
                "食事の前に手を洗わないといけません。",
                new WordType("v", "動詞"));

        Assert.assertEquals("(    ) lavarse las manos antes de comer.",
                WordTestManager.getBlindSpanishExample(word1, 0));

        Word word2 = new Word(
                "rojo / roja",
                "赤い",
                "Con un poco de whisky se puso (rojo). / El samáforo está (rojo).",
                "彼は少しのウイスキーで赤くなった。 / 信号は赤です。",
                new WordType("adjective", "形容詞"));

        Assert.assertEquals("El samáforo está (    ).",
                WordTestManager.getBlindSpanishExample(word2, 1));
    }

    @Test
    public void getSpanishExample(){
        Word word = new Word(
                "rojo / roja",
                "赤い",
                "Con un poco de whisky se puso (rojo). / El samáforo está (rojo).",
                "彼は少しのウイスキーで赤くなった。 / 信号は赤です。",
                new WordType("adjective", "形容詞"));

        Assert.assertEquals("Con un poco de whisky se puso (rojo).",
                WordTestManager.getSpanishExample(word, 0));
        Assert.assertEquals("El samáforo está (rojo).",
                WordTestManager.getSpanishExample(word, 1));
    }

    @Test
    public void getJapaneseExample(){
        Word word = new Word(
                "rojo / roja",
                "赤い",
                "Con un poco de whisky se puso (rojo). / El samáforo está (rojo).",
                "彼は少しのウイスキーで赤くなった。 / 信号は赤です。",
                new WordType("adjective", "形容詞"));

        Assert.assertEquals("彼は少しのウイスキーで赤くなった。",
                WordTestManager.getJapaneseExample(word, 0));
        Assert.assertEquals("信号は赤です。",
                WordTestManager.getJapaneseExample(word, 1));
    }

    /**
     * 評価メソッドが正しい解答でOKを返すかテスト
     */
    @Test
    public void evaluateOK(){
        Word word = new Word(
                "rojo / roja",
                "赤い",
                "Con un poco de whisky se puso (rojo). / El samáforo está (rojo).",
                "彼は少しのウイスキーで赤くなった。 / 信号は赤です。",
                new WordType("adjective", "形容詞"));

        Assert.assertEquals(WordTestManager.Grade.OK,
                WordTestManager.evaluate(word, 0, "rojo"));
        Assert.assertEquals(WordTestManager.Grade.OK,
                WordTestManager.evaluate(word, 1, "rojo"));
    }

    /**
     * 例文が1つの単語でOKの評価ができるかテスト
     */
    @Test
    public void evaluateOneExampleOK(){
        Word word = new Word(
                "Hay que ...",
                "・・・しないといけない",
                "(Hay que) lavarse las manos antes de comer.",
                "食事の前に手を洗わないといけません。",
                new WordType("v", "動詞"));

        Assert.assertEquals(WordTestManager.Grade.OK,
                WordTestManager.evaluate(word, 0, "Hay que"));
    }

    /**
     * 例文が1つの単語でNOT_OKの評価ができるかテスト
     */
    @Test
    public void evaluateOneExampleNotOK(){
        Word word = new Word(
                "Hay que ...",
                "・・・しないといけない",
                "(Hay que) lavarse las manos antes de comer.",
                "食事の前に手を洗わないといけません。",
                new WordType("v", "動詞"));

        Assert.assertEquals(WordTestManager.Grade.NOT_OK,
                WordTestManager.evaluate(word, 0, ""));
    }

    /**
     * 例文に(...)のパターンがない場合のテスト
     */
    @Test
    public void evaluateMissingExample(){
        Word word = new Word(
                "Hay que ...",
                "・・・しないといけない",
                "Hay que lavarse las manos antes de comer.",
                "食事の前に手を洗わないといけません。",
                new WordType("v", "動詞"));

        Assert.assertEquals(WordTestManager.Grade.NOT_OK,
                WordTestManager.evaluate(word, 0, "Hay que"));
    }

    /**
     * 評価メソッドが誤った解答でNOT_OKを返すかテスト
     */
    @Test
    public void evaluateNotOK(){
        Word word = new Word(
                "rojo / roja",
                "赤い",
                "Con un poco de whisky se puso (rojo). / El samáforo está (rojo).",
                "彼は少しのウイスキーで赤くなった。 / 信号は赤です。",
                new WordType("adjective", "形容詞"));

        Assert.assertEquals(WordTestManager.Grade.NOT_OK,
                WordTestManager.evaluate(word, 0, "roja"));
        Assert.assertEquals(WordTestManager.Grade.NOT_OK,
                WordTestManager.evaluate(word, 1, "roja"));
    }
}
