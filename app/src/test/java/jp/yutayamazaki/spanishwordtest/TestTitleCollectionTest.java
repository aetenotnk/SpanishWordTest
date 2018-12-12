package jp.yutayamazaki.spanishwordtest;

import android.database.sqlite.SQLiteDatabase;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.io.File;

import jp.yutayamazaki.spanishwordtest.bean.TestTitleCollection;
import jp.yutayamazaki.spanishwordtest.dropbox.DropBox;

@RunWith(RobolectricTestRunner.class)
public class TestTitleCollectionTest {
    private DropBox dropBox;
    private JSONObject jsonConfig;

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

    @Test
    public void createTable(){
        TestTitleCollection testTitleCollection =
                new TestTitleCollection(RuntimeEnvironment.application);
        SQLiteDatabase db = testTitleCollection.getReadableDatabase();

        Assert.assertNotEquals(null, db);
    }
}
