package jp.yutayamazaki.spanishwordtest;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import jp.yutayamazaki.spanishwordtest.dropbox.DropBox;

public class DropBoxTest {
    private DropBox dropBox;

    /**
     * 各テストの前処理
     * DropBoxインスタンスを作成しておく
     * @throws Exception 設定ファイルが読み込めなければ例外を投げる
     */
    @Before
    public void setUp() throws Exception {
        // jsonファイルからDropBoxのトークンを取得
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("testconfig.json");
        MatcherAssert.assertThat(inputStream, CoreMatchers.is(CoreMatchers.notNullValue()));

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String inputStr;
        while((inputStr = br.readLine()) != null){
            stringBuilder.append(inputStr);
        }
        br.close();

        JSONObject json = new JSONObject(stringBuilder.toString());
        String token = json.getJSONObject("dropbox").getString("token");
        String useragent = json.getJSONObject("dropbox").getString("useragent");

        dropBox = new DropBox(token, useragent);
        MatcherAssert.assertThat(dropBox, CoreMatchers.is(CoreMatchers.notNullValue()));
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
}
