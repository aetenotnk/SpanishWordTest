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
        jsonConfig = new JSONObject(readResourceFile("testconfig.json"));
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

        deleteFiles(new File(System.getProperty("user.dir") + tempPath));
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
                dropBox.downloadFile("test.txt", System.getProperty("user.dir") + tempPath);
        String downloadText = readResourceFile(new File(savePath));
        String baseText = readResourceFile("testbase.txt");

        Assert.assertEquals(downloadText, baseText);
    }

    /**
     * リソースファイルの文字列を読み込む
     * @param filepath resources以下のファイル名、パス
     * @return ファイルの中身
     */
    private String readResourceFile(String filepath) throws  Exception{
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(filepath);
        MatcherAssert.assertThat(inputStream, CoreMatchers.is(CoreMatchers.notNullValue()));

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String inputStr;
        while((inputStr = br.readLine()) != null){
            stringBuilder.append(inputStr);
        }
        br.close();

        return stringBuilder.toString();
    }

    /**
     * ファイルからテキストを読み込む
     * @param file 読み込むファイル
     * @return 読み込んだテキスト
     * @throws Exception ファイルが開けないと例外を投げる
     */
    private String readResourceFile(File file) throws Exception{
        StringBuilder stringBuilder = new StringBuilder();

        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            String line;

            while((line = br.readLine()) != null){
                stringBuilder.append(line);
            }
        }

        return stringBuilder.toString();
    }

    /**
     * ファイルまたはディレクトリを削除する
     * ディレクトリの場合は再帰的に削除
     * @param file ファイルまたはディレクトリ
     */
    private void deleteFiles(File file){
        if(!file.exists()){
            return;
        }

        if(file.isFile()){
            System.out.println("delete: " + file.getAbsolutePath());
            file.delete();
            return;
        }

        for(File innerFile : file.listFiles()){
            deleteFiles(innerFile);
        }

        file.delete();
        System.out.println("delete: " + file.getAbsolutePath());
    }
}
