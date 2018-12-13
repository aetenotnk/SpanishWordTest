package jp.yutayamazaki.spanishwordtest.bean;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.LinkedList;
import java.util.List;

/**
 * TestTitleのコレクションクラス
 */
public class TestTitleCollection extends BeanCollection<TestTitle> {
    private static String DB_NAME = "TestTitle";
    private static int DB_VERSION = 1;

    private static String DB_COL_ID = "id";
    private static String DB_COL_TITLE = "title";
    private static String DB_COL_CAPTION = "caption";
    private static String DB_COL_FILEPATH = "filepath";

    private static String SQL_CREATE_TABLE =
            "CREATE TABLE " + DB_NAME + "(" +
                    "id integer primary key," +
                    "title text," +
                    "caption text," +
                    "filepath text)";
    private static String SQL_INSERT_OR_UPDATE =
            "REPLACE INTO " + DB_NAME + "(" +
                    DB_COL_ID + "," +
                    DB_COL_TITLE + "," +
                    DB_COL_CAPTION + "," +
                    DB_COL_FILEPATH + ")" +
                    "VALUES(?, ?, ?, ?)";
    private static String SQL_SELECT_ALL =
            "SELECT * FROM " + DB_NAME;

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

    @Override
    public void insertOrUpdate(TestTitle testTitle) {
        SQLiteDatabase db = getWritableDatabase();
        SQLiteStatement statement = db.compileStatement(SQL_INSERT_OR_UPDATE);

        statement.bindLong(1, testTitle.getId());
        statement.bindString(2, testTitle.getTitle());
        statement.bindString(3, testTitle.getCaption());
        statement.bindString(4, testTitle.getFilepath());

        statement.execute();
        db.close();
    }

    @Override
    public List<TestTitle> selectAll() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(SQL_SELECT_ALL, null);
        List<TestTitle> result = new LinkedList<>();

        while(cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex(DB_COL_ID));
            String title = cursor.getString(cursor.getColumnIndex(DB_COL_TITLE));
            String caption = cursor.getString(cursor.getColumnIndex(DB_COL_CAPTION));
            String filepath = cursor.getString(cursor.getColumnIndex(DB_COL_FILEPATH));

            result.add(new TestTitle(id, title, caption, filepath));
        }

        cursor.close();
        db.close();

        return result;
    }
}
