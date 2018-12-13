package jp.yutayamazaki.spanishwordtest.bean;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.LinkedList;
import java.util.List;

public class WordCollection extends BeanCollection<Word> {
    private static String DB_NAME_PREFIX = "";
    private static String DB_NAME_SUFFIX = "_WORD";
    private static int DB_VERSION = 1;

    private static String DB_COL_WORD_ES = "word_spanish";
    private static String DB_COL_WORD_JA = "word_japanese";
    private static String DB_COL_EXAMPLE_ES = "example_spanish";
    private static String DB_COL_EXAMPLE_JA = "example_japanese";
    private static String DB_COL_TYPE = "type";

    /**
     * テーブル名より前のCREATE文の文字列
     */
    private static String SQL_CREATE_TABLE_PREFIX = "CREATE TABLE ";
    /**
     * テーブル名より後のCREATE文の文字列
     */
    private static String SQL_CREATE_TABLE_SUFFIX =
            "(" + DB_COL_WORD_ES + " text," +
                    DB_COL_WORD_JA + " text," +
                    DB_COL_EXAMPLE_ES + " text," +
                    DB_COL_EXAMPLE_JA + " text," +
                    DB_COL_TYPE + " text)";
    private static String SQL_INSERT_OR_UPDATE_PREFIX = "REPLACE INTO ";
    private static String SQL_INSERT_OR_UPDATE_SUFFIX =
            "(" + DB_COL_WORD_ES + "," +
                    DB_COL_WORD_JA + "," +
                    DB_COL_EXAMPLE_ES + "," +
                    DB_COL_EXAMPLE_JA + " ," +
                    DB_COL_TYPE + ") " +
            "VALUES (?, ?, ?, ?, ?)";
    private static String SQL_SELECT_ALL = "SELECT * FROM ";
    private static String SQL_DELETE_ALL = "DELETE FROM ";

    public WordCollection(Context context, String testName){
        super(context,
                DB_NAME_PREFIX + testName + DB_NAME_SUFFIX,
                DB_VERSION);
    }

    /**
     * DB上のレコードをすべて削除する
     */
    public void deleteAll(){
        SQLiteDatabase db = getWritableDatabase();
        SQLiteStatement statement = db.compileStatement(SQL_DELETE_ALL + dbName);

        statement.execute();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        SQLiteStatement statement = sqLiteDatabase.compileStatement(
                SQL_CREATE_TABLE_PREFIX + dbName + SQL_CREATE_TABLE_SUFFIX);

        statement.execute();
    }

    @Override
    public Word createBean(String[] row) {
        return new Word(row[0], row[1], row[2], row[3], row[4]);
    }

    @Override
    public void insertOrUpdate(Word word) {
        SQLiteDatabase db = getWritableDatabase();
        SQLiteStatement statement = db.compileStatement(
                SQL_INSERT_OR_UPDATE_PREFIX + dbName + SQL_INSERT_OR_UPDATE_SUFFIX);

        statement.bindString(1, word.getWordSpanish());
        statement.bindString(2, word.getWordJapanese());
        statement.bindString(3, word.getExampleSpanish());
        statement.bindString(4, word.getExampleJapanese());
        statement.bindString(5, Word.WordType.getString(word.getType()));

        statement.execute();
        db.close();
    }

    @Override
    public List<Word> selectAll() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(SQL_SELECT_ALL + dbName, null);
        List<Word> result = new LinkedList<>();

        while(cursor.moveToNext()){
            String wordSpanish = cursor.getString(cursor.getColumnIndex(DB_COL_WORD_ES));
            String wordJapanese = cursor.getString(cursor.getColumnIndex(DB_COL_WORD_JA));
            String exampleSpanish = cursor.getString(cursor.getColumnIndex(DB_COL_EXAMPLE_ES));
            String exampleJapanese = cursor.getString(cursor.getColumnIndex(DB_COL_EXAMPLE_JA));
            String type = cursor.getString(cursor.getColumnIndex(DB_COL_TYPE));

            result.add(new Word(wordSpanish, wordJapanese, exampleSpanish, exampleJapanese, type));
        }

        cursor.close();
        db.close();

        return result;
    }
}
