package jp.yutayamazaki.spanishwordtest;

import android.database.sqlite.SQLiteDatabase;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.io.File;
import java.util.List;

import jp.yutayamazaki.spanishwordtest.bean.Word;
import jp.yutayamazaki.spanishwordtest.bean.WordCollection;
import jp.yutayamazaki.spanishwordtest.dropbox.DropBox;

@RunWith(RobolectricTestRunner.class)
public class WordCollectionTest {
    private DropBox dropBox;
    private JSONObject jsonConfig;

    /**
     * 前処理
     * @throws Exception 設定ファイルが読み込めなければ例外を投げる
     */
    @Before
    public void setUp() throws Exception{
        // jsonファイルからDropBoxのトークンを取得
        jsonConfig = new JSONObject(TestUtil.readResourceFile("testconfig.json"));
        String token = jsonConfig.getJSONObject("dropbox").getString("token");
        String useragent = jsonConfig.getJSONObject("dropbox").getString("useragent");

        dropBox = new DropBox(token, useragent);
        MatcherAssert.assertThat(dropBox, CoreMatchers.is(CoreMatchers.notNullValue()));

        String tempPath = jsonConfig.getJSONObject("test").getString("tempdirectory");
        new File(System.getProperty("user.dir") + tempPath).mkdir();
    }

    /**
     * 各テストの後処理
     * tempフォルダの削除
     * @throws Exception 設定ファイルが読み込めなければ例外を投げる
     */
    @After
    public void tearDown() throws Exception{
        String tempPath = jsonConfig.getJSONObject("test").getString("tempdirectory");

        TestUtil.deleteFiles(new File(System.getProperty("user.dir") + tempPath));
    }

    /**
     * テーブルが作成できるかテスト
     */
    @Test
    public void createTable(){
        WordCollection WordCollection =
                new WordCollection(RuntimeEnvironment.application, "test");
        SQLiteDatabase db = WordCollection.getReadableDatabase();

        Assert.assertNotEquals(null, db);
    }

    /**
     * DropBoxからデータを取得してデータを追加できるかテスト
     * @throws Exception 設定ファイルが読み込めなければ例外を投げる
     */
    @Test
    public void loadDataFromDropBox() throws Exception{
        WordCollection wordCollection =
                new WordCollection(RuntimeEnvironment.application, "test");
        String tempPath = jsonConfig.getJSONObject("test").getString("tempdirectory");
        tempPath = System.getProperty("user.dir") + tempPath;

        wordCollection.loadBeansByDropBox(dropBox, "test/testquiz.csv", tempPath);

        List<Word> data = wordCollection.selectAll();

        Assert.assertEquals(1, data.size());

        Word word = data.get(0);

        Assert.assertEquals("Hay que ...", word.getWordSpanish());
        Assert.assertEquals("・・・しないといけない", word.getWordJapanese());
        Assert.assertEquals("(Hay que) lavarse las manos antes de comer.",
                word.getExampleSpanish());
        Assert.assertEquals("食事の前に手を洗わないといけません。",
                word.getExampleJapanese());
        Assert.assertEquals(Word.WordType.VERB, word.getType());
    }

    /**
     * データが追加できるかテスト
     */
    @Test
    public void insert(){
        WordCollection wordCollection =
                new WordCollection(RuntimeEnvironment.application, "test");
        Word word = new Word("deporte",
                "スポーツ",
                "¿Haces algún (deporte)",
                "君は何かスポーツしてる？",
                "noun");

        wordCollection.insertOrUpdate(word);

        List<Word> all = wordCollection.selectAll();

        Assert.assertEquals(1, all.size());
        Assert.assertEquals("deporte", all.get(0).getWordSpanish());
        Assert.assertEquals("スポーツ", all.get(0).getWordJapanese());
        Assert.assertEquals("¿Haces algún (deporte)", all.get(0).getExampleSpanish());
        Assert.assertEquals("君は何かスポーツしてる？", all.get(0).getExampleJapanese());
        Assert.assertEquals(Word.WordType.NOUN, all.get(0).getType());
    }

    /**
     * 同じデータを2回insertした時の挙動をテストする
     */
    @Test
    public void insertTwiceSameData(){
        WordCollection wordCollection =
                new WordCollection(RuntimeEnvironment.application, "test");
        Word word = new Word("deporte",
                "スポーツ",
                "¿Haces algún (deporte)",
                "君は何かスポーツしてる？",
                "noun");

        wordCollection.insertOrUpdate(word);
        wordCollection.insertOrUpdate(word);

        List<Word> all = wordCollection.selectAll();

        // 同じデータが2つ存在すること
        Assert.assertEquals(2, all.size());
    }

    /**
     * データを全削除できるかテスト
     */
    @Test
    public void deleteAll(){
        WordCollection wordCollection =
                new WordCollection(RuntimeEnvironment.application, "test");
        Word word = new Word("deporte",
                "スポーツ",
                "¿Haces algún (deporte)",
                "君は何かスポーツしてる？",
                "noun");

        for(int i = 0;i < 10;i++){
            wordCollection.insertOrUpdate(word);
        }

        List<Word> all = wordCollection.selectAll();

        Assert.assertEquals(10, all.size());

        wordCollection.deleteAll();
        all = wordCollection.selectAll();

        Assert.assertEquals(0, all.size());
    }
}
