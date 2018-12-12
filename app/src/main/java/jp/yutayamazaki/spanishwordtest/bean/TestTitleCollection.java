package jp.yutayamazaki.spanishwordtest.bean;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

/**
 * TestTitleのコレクションクラス
 * ※ スングルトン
 */
public class TestTitleCollection extends BeanCollection<TestTitle> {
    private static String DB_NAME = "TestTitle";
    private static int DB_VERSION = 1;

    private static String SQL_CREATE_TABLE =
            "CREATE TABLE " + DB_NAME + "(" +
                    "id integer," +
                    "title text," +
                    "caption text," +
                    "filepath text)";

    public TestTitleCollection(Context context){
        super(context, DB_NAME, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        SQLiteStatement statement = sqLiteDatabase.compileStatement(SQL_CREATE_TABLE);

        statement.execute();
    }

    @Override
    public TestTitle createBean(String[] row) {
        return new TestTitle(Integer.parseInt(row[0]), row[1], row[2], row[3]);
    }
}
