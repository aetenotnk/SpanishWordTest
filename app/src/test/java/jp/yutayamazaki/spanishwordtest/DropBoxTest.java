package jp.yutayamazaki.spanishwordtest;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import jp.yutayamazaki.spanishwordtest.dropbox.DropBox;

public class DropBoxTest {
    /**
     * DropBpxに繋げられるかテスト
     * @throws Exception 設定ファイルが読み込めなければ例外を投げる
     */
    @Test
    public void canConnectToDropBox() throws Exception{
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

        DropBox dropBox = new DropBox(token, useragent);
        MatcherAssert.assertThat(dropBox, CoreMatchers.is(CoreMatchers.notNullValue()));

        // ユーザ名が取得できなければトークンが誤っている
        Assert.assertThat(dropBox.getCurrentUserName(),
                CoreMatchers.is(CoreMatchers.notNullValue()));
    }
}
