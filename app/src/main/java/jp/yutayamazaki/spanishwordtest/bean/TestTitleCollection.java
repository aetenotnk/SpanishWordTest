package jp.yutayamazaki.spanishwordtest.bean;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import jp.yutayamazaki.spanishwordtest.dropbox.DropBox;
import jp.yutayamazaki.spanishwordtest.file.CSVLoader;

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
    private static String DB_COL_VERSION = "version";

    private static String SQL_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + DB_NAME + "(" +
                    DB_COL_ID + " integer primary key," +
                    DB_COL_TITLE + " text," +
                    DB_COL_CAPTION + " text," +
                    DB_COL_FILEPATH + " text," +
                    DB_COL_VERSION + " integer)";
    private static String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + DB_NAME;
    private static String SQL_INSERT_OR_UPDATE =
            "REPLACE INTO " + DB_NAME + "(" +
                    DB_COL_ID + "," +
                    DB_COL_TITLE + "," +
                    DB_COL_CAPTION + "," +
                    DB_COL_FILEPATH + "," +
                    DB_COL_VERSION + ")" +
                    "VALUES(?, ?, ?, ?, ?)";
    private static String SQL_SELECT_ALL =
            "SELECT * FROM " + DB_NAME;
    private static String SQL_DELETE_ALL =
            "DELETE FROM " + DB_NAME;
    private static String SQL_DELETE_BY_ID =
            "DELETE FROM " + DB_NAME + " WHERE id=?";

    public TestTitleCollection(BeanDBHelper dbHelper){
        super(dbHelper, DB_NAME, DB_VERSION);
    }

    @Override
    public TestTitle createBean(String[] row) {
        return new TestTitle(Integer.parseInt(row[0]), row[1], row[2], row[3], Integer.parseInt(row[4]));
    }

    @Override
    public void createTable(SQLiteOpenHelper dbHelper) {
        SQLiteStatement statement =
                dbHelper.getWritableDatabase().compileStatement(SQL_CREATE_TABLE);

        statement.execute();
    }

    public static void dropTable(SQLiteOpenHelper dbHelper) {
        dropTable(dbHelper.getWritableDatabase());
    }

    public static void dropTable(SQLiteDatabase db) {
        db.compileStatement(SQL_DROP_TABLE).execute();
    }

    @Override
    public void insertOrUpdate(TestTitle testTitle) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        SQLiteStatement statement = db.compileStatement(SQL_INSERT_OR_UPDATE);

        statement.bindLong(1, testTitle.getId());
        statement.bindString(2, testTitle.getTitle());
        statement.bindString(3, testTitle.getCaption());
        statement.bindString(4, testTitle.getFilepath());
        statement.bindLong(5, testTitle.getVersion());

        statement.execute();
        db.close();
    }

    @Override
    public List<TestTitle> selectAll() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQL_SELECT_ALL, null);
        List<TestTitle> result = new LinkedList<>();

        while(cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex(DB_COL_ID));
            String title = cursor.getString(cursor.getColumnIndex(DB_COL_TITLE));
            String caption = cursor.getString(cursor.getColumnIndex(DB_COL_CAPTION));
            String filepath = cursor.getString(cursor.getColumnIndex(DB_COL_FILEPATH));
            int version = cursor.getInt(cursor.getColumnIndex(DB_COL_VERSION));

            result.add(new TestTitle(id, title, caption, filepath, version));
        }

        cursor.close();
        db.close();

        return result;
    }

    /**
     * DB内のデータをすべて削除する
     */
    public void deleteAll() {
        SQLiteStatement statement = dbHelper.getWritableDatabase().compileStatement(SQL_DELETE_ALL);

        statement.execute();
    }

    /**
     * idを指定してデータを削除する
     * @param id 削除するデータのid
     */
    public void deleteById(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        SQLiteStatement statement = db.compileStatement(SQL_DELETE_BY_ID);

        statement.bindLong(1, id);

        statement.execute();
    }

    /**
     * DropBox上にある試験一覧から更新、追加された試験を取得する
     * @param dropBox DropBoxオブジェクト
     * @param filename DropBox上のファイル名
     * @param tempDir 一時的に保存するディレクトリ
     * @return 更新、追加されたTestTitleのリスト
     */
    public List<TestTitle> getUpdatedOrNewTestTitleByDropBox(DropBox dropBox, String filename, String tempDir) {
        String path = dropBox.downloadFile(filename, tempDir);
        List<String[]> rows = CSVLoader.load(new File(tempDir + "/" + filename));
        List<TestTitle> oldTestTitles = selectAll();
        List<TestTitle> result = new LinkedList<>();

        rows.remove(0);

        for(String[] row : rows) {
            TestTitle newTestTitle = createBean(row);
            // 既存のデータを取得
            List<TestTitle> oldTestTitleStream = oldTestTitles.stream()
                    .filter(testTitle ->
                            testTitle.getId() == newTestTitle.getId())
                    .collect(Collectors.toList());

            // 既存のデータがない
            if(oldTestTitleStream.size() == 0) {
                result.add(newTestTitle);

                continue;
            }

            TestTitle oldTestTitle = oldTestTitleStream.get(0);

            // バージョンが更新されている
            if(oldTestTitle.getVersion() < newTestTitle.getVersion()) {
                result.add(newTestTitle);
            }
        }

        // ダウンロードしたファイルは削除する
        new File(path).delete();

        return result;
    }
}
