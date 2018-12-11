package jp.yutayamazaki.spanishwordtest;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import jp.yutayamazaki.spanishwordtest.file.CSVLoader;

public class CSVLoaderTest {
    private JSONObject jsonConfig;

    /**
     * 各テストの前処理
     * 設定ファイルの読み込み
     * @throws Exception 設定ファイルが読み込めなければ例外を投げる
     */
    @Before
    public void setUp() throws Exception {
        // jsonファイルから設定を読み込み
        jsonConfig = new JSONObject(TestUtil.readResourceFile("testconfig.json"));
    }

    /**
     * 各テストの後処理
     */
    @After
    public void tearDown(){

    }

    /**
     * 元のデータにカンマを含まないCSVのテスト
     * @throws Exception ファイルが読み込めなければ例外を投げる
     */
    @Test
    public void simpleCSV() throws Exception{
        String resourcePath =
                jsonConfig.getJSONObject("test").getString("resourcesdirectory");
        String userDir = System.getProperty("user.dir");
        List<String[]> result = CSVLoader.load(
                new File(userDir + resourcePath + "/csv/simple.csv"));

        Assert.assertEquals(2, result.size());
        Assert.assertEquals(2, result.get(0).length);
        Assert.assertEquals(2, result.get(1).length);
        Assert.assertArrayEquals(new String[]{"1", "abcde"}, result.get(0));
        Assert.assertArrayEquals(new String[]{"2", "日本語"}, result.get(1));
    }

    /**
     * カンマを含むデータを読み込めるかテスト
     * @throws Exception ファイルが読み込めなければ例外を投げる
     */
    @Test
    public void commaCSV() throws Exception{
        String resourcePath =
                jsonConfig.getJSONObject("test").getString("resourcesdirectory");
        String userDir = System.getProperty("user.dir");
        List<String[]> result = CSVLoader.load(
                new File(userDir + resourcePath + "/csv/comma.csv"));

        Assert.assertEquals(2, result.size());
        Assert.assertEquals(2, result.get(0).length);
        Assert.assertArrayEquals(
                new String[]{"Does this file include comma?", "Yes, it does."},
                result.get(0));
        Assert.assertArrayEquals(
                new String[]{"This data have 2 commas.", "a,b,c"},
                result.get(1));
    }

    /**
     * 空のファイルを読み込めるかテスト
     * @throws Exception ファイルが読み込めなければ例外を投げる
     */
    @Test
    public void emptyCSV() throws Exception{
        String resourcePath =
                jsonConfig.getJSONObject("test").getString("resourcesdirectory");
        String userDir = System.getProperty("user.dir");
        List<String[]> result = CSVLoader.load(
                new File(userDir + resourcePath + "/csv/empty.csv"));

        Assert.assertEquals(0, result.size());
    }
}
