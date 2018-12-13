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
import java.lang.reflect.Field;
import java.util.List;

import jp.yutayamazaki.spanishwordtest.bean.TestTitle;
import jp.yutayamazaki.spanishwordtest.bean.TestTitleCollection;
import jp.yutayamazaki.spanishwordtest.dropbox.DropBox;

@RunWith(RobolectricTestRunner.class)
public class TestTitleCollectionTest {
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
     * DBが作成できるかテスト
     */
    @Test
    public void createTable(){
        TestTitleCollection testTitleCollection =
                new TestTitleCollection(RuntimeEnvironment.application);
        SQLiteDatabase db = testTitleCollection.getReadableDatabase();

        Assert.assertNotEquals(null, db);
    }

    /**
     * DBを更新した時のテスト
     * @throws Exception TestTitleCollection.DB_FIELDにアクセスできないと例外を投げる
     */
    @Test
    public void upgradeTable() throws Exception {
        TestTitleCollection testTitleCollection =
                new TestTitleCollection(RuntimeEnvironment.application);
        Class c = testTitleCollection.getClass();
        // プライベートなstaticフィールドに無理やりアクセス
        Field versionField = c.getDeclaredField("DB_VERSION");
        versionField.setAccessible(true);
        // バージョンを1に設定
        versionField.setInt(testTitleCollection, 1);
        // getWritableDatabaseを呼んで,
        // バージョンを更新してnewをした時にonUpgradeが呼ばれるようにする
        testTitleCollection.getWritableDatabase().close();
        // バージョンを更新
        versionField.setInt(testTitleCollection, 2);
        SQLiteDatabase db =
                new TestTitleCollection(RuntimeEnvironment.application).getWritableDatabase();

        Assert.assertNotEquals(null, db);
    }

    @Test
    public void loadDataFromDropBox() throws Exception{
        TestTitleCollection testTitleCollection =
                new TestTitleCollection(RuntimeEnvironment.application);
        String tempPath = jsonConfig.getJSONObject("test").getString("tempdirectory");
        tempPath = System.getProperty("user.dir") + tempPath;

        testTitleCollection.loadBeansByDropBox(dropBox, "test/testlist.csv", tempPath);

        List<TestTitle> data = testTitleCollection.selectAll();

        Assert.assertEquals(1, data.size());

        TestTitle testTitle = data.get(0);

        Assert.assertEquals(1, testTitle.getId());
        Assert.assertEquals("スペイン語単語テスト2018/09/23", testTitle.getTitle());
        Assert.assertEquals("P8 - 11", testTitle.getCaption());
        Assert.assertEquals("quiz/quiz20180923.csv", testTitle.getFilepath());
    }

    /**
     * データ追加のテスト
     */
    @Test
    public void insert(){
        TestTitleCollection testTitleCollection =
                new TestTitleCollection(RuntimeEnvironment.application);
        TestTitle data = new TestTitle(1, "Test1", "caption1", "/dummy");

        testTitleCollection.insertOrUpdate(data);

        List<TestTitle> all = testTitleCollection.selectAll();

        Assert.assertEquals(1, all.size());
        Assert.assertEquals(1, all.get(0).getId());
        Assert.assertEquals("Test1", all.get(0).getTitle());
        Assert.assertEquals("caption1", all.get(0).getCaption());
        Assert.assertEquals("/dummy", all.get(0).getFilepath());
    }

    /**
     * データ更新した時のテスト
     */
    @Test
    public void update(){
        TestTitleCollection testTitleCollection =
                new TestTitleCollection(RuntimeEnvironment.application);
        TestTitle data1 = new TestTitle(1, "Test1", "caption1", "/dummy");
        TestTitle data2 = new TestTitle(1, "Test1_1", "caption1_1", "/dummy_1");

        testTitleCollection.insertOrUpdate(data1);
        testTitleCollection.insertOrUpdate(data2);

        List<TestTitle> all = testTitleCollection.selectAll();

        Assert.assertEquals(1, all.size());
        Assert.assertEquals(1, all.get(0).getId());
        Assert.assertEquals("Test1_1", all.get(0).getTitle());
        Assert.assertEquals("caption1_1", all.get(0).getCaption());
        Assert.assertEquals("/dummy_1", all.get(0).getFilepath());
    }
}
