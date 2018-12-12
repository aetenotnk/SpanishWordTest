package jp.yutayamazaki.spanishwordtest;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import jp.yutayamazaki.spanishwordtest.dropbox.DropBox;

public class DropBoxTest {
    private DropBox dropBox;
    private JSONObject jsonConfig;

    /**
     * 各テストの前処理
     * DropBoxインスタンスを作成しておく
     * tempフォルダを作成しておく
     * @throws Exception 設定ファイルが読み込めなければ例外を投げる
     */
    @Before
    public void setUp() throws Exception {
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
     * DropBpxに繋げられるかテスト
     */
    @Test
    public void canConnectToDropBox(){
        // ユーザ名が取得できなければトークンが誤っている
        Assert.assertThat(dropBox.getCurrentUserName(),
                CoreMatchers.is(CoreMatchers.notNullValue()));
    }

    /**
     * DropBoxからテキストファイルを取得できるかテスト
     * @exception Exception ローカルのファイルが読み取れなければ例外を投げる
     */
    @Test
    public void downloadTextFile() throws Exception {
        String tempPath = jsonConfig.getJSONObject("test").getString("tempdirectory");
        String savePath =
                dropBox.downloadFile("test/test.txt", System.getProperty("user.dir") + tempPath);
        String downloadText = TestUtil.readResourceFile(new File(savePath));
        String baseText = TestUtil.readResourceFile("testbase.txt");

        Assert.assertEquals(downloadText, baseText);
    }
}
