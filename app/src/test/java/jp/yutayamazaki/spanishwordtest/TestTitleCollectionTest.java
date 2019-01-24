package jp.yutayamazaki.spanishwordtest;

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
import java.util.stream.Collectors;

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
        TestTitle data = new TestTitle(1, "Test1", "caption1", "/dummy", 1);

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
        TestTitle data1 = new TestTitle(1, "Test1", "caption1", "/dummy", 1);
        TestTitle data2 = new TestTitle(1, "Test1_1", "caption1_1", "/dummy_1", 1);

        testTitleCollection.insertOrUpdate(data1);
        testTitleCollection.insertOrUpdate(data2);

        List<TestTitle> all = testTitleCollection.selectAll();

        Assert.assertEquals(1, all.size());
        Assert.assertEquals(1, all.get(0).getId());
        Assert.assertEquals("Test1_1", all.get(0).getTitle());
        Assert.assertEquals("caption1_1", all.get(0).getCaption());
        Assert.assertEquals("/dummy_1", all.get(0).getFilepath());
    }

    /**
     * データを削除のテスト
     */
    @Test
    public void deleteAll(){
        TestTitleCollection testTitleCollection =
                new TestTitleCollection(RuntimeEnvironment.application);
        TestTitle data1 = new TestTitle(1, "Test1", "caption1", "/dummy1", 1);
        TestTitle data2 = new TestTitle(2, "Test2", "caption2", "/dummy2", 1);

        testTitleCollection.insertOrUpdate(data1);
        testTitleCollection.insertOrUpdate(data2);

        List<TestTitle> all = testTitleCollection.selectAll();

        Assert.assertEquals(2, all.size());

        testTitleCollection.deleteAll();
        all = testTitleCollection.selectAll();

        Assert.assertEquals(0, all.size());
    }

    /**
     * 更新されたか、新しく追加された行が正しく取得できるかテスト
     * @throws Exception 設定ファイルが読み込めなければ例外を投げる
     */
    @Test
    public void getUpdatedOrNewTestTitle() throws Exception {
        TestTitleCollection testTitleCollection =
                new TestTitleCollection(RuntimeEnvironment.application);
        String tempPath = jsonConfig.getJSONObject("test").getString("tempdirectory");
        tempPath = System.getProperty("user.dir") + tempPath;

        testTitleCollection.loadBeansByDropBox(dropBox, "test/testlist_old.csv", tempPath);

        List<TestTitle> updatedOrNew =
                testTitleCollection.getUpdatedOrNewTestTitleByDropBox(
                        dropBox,
                        "test/testlist_new.csv",
                        tempPath);

        List<TestTitle> updatedTestTitles =
                updatedOrNew.stream()
                        .filter(testTitle -> testTitle.getId() == 2)
                        .collect(Collectors.toList());
        List<TestTitle> newTestTitles =
                updatedOrNew.stream()
                        .filter(testTitle -> testTitle.getId() == 3)
                        .collect(Collectors.toList());

        Assert.assertEquals(1, updatedTestTitles.size());
        Assert.assertEquals(1, newTestTitles.size());

        TestTitle updatedTestTitle = updatedTestTitles.get(0);

        Assert.assertEquals(2, updatedTestTitle.getVersion());
    }
}
