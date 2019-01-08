package jp.yutayamazaki.spanishwordtest;

import android.database.sqlite.SQLiteDatabase;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.lang.reflect.Field;

import jp.yutayamazaki.spanishwordtest.bean.BeanDBHelper;

@RunWith(RobolectricTestRunner.class)
public class BeanDBHelperTest {
    /**
     * DB作成テスト
     */
    @Test
    public void createDB(){
        BeanDBHelper helper = new BeanDBHelper(RuntimeEnvironment.application);
        SQLiteDatabase db = helper.getReadableDatabase();

        Assert.assertNotNull(db);
    }

    /**
     * DBのバージョンが更新されたときのテスト
     * @throws Exception BeanDBHelper.DB_VERSIOが存在しないと例外を投げる
     */
    @Test
    public void updateDB() throws Exception{
        BeanDBHelper helper = new BeanDBHelper(RuntimeEnvironment.application);
        Class c = helper.getClass();
        Field versionField = c.getDeclaredField("DB_VERSION");
        versionField.setAccessible(true);
        // バージョン1のDBを作成
        versionField.setInt(helper, 1);
        new BeanDBHelper(RuntimeEnvironment.application).getReadableDatabase();
        // バージョンを2に更新
        versionField.set(helper, 2);
        SQLiteDatabase db = new BeanDBHelper(RuntimeEnvironment.application).getReadableDatabase();

        Assert.assertNotNull(db);
    }
}
