package jp.yutayamazaki.spanishwordtest.bean;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.LinkedList;
import java.util.List;

public class WordCollection extends BeanCollection<Word> {
    private static String DB_NAME_PREFIX = "WordTest";
    private static String DB_NAME_SUFFIX = "_WORD";
    private static int DB_VERSION = 2;

    private static String DB_COL_WORD_ES = "word_spanish";
    private static String DB_COL_WORD_JA = "word_japanese";
    private static String DB_COL_EXAMPLE_ES = "example_spanish";
    private static String DB_COL_EXAMPLE_JA = "example_japanese";
    private static String DB_COL_TYPE = "type";

    /**
     * テーブル名より前のCREATE文の文字列
     */
    private static String SQL_CREATE_TABLE_PREFIX = "CREATE TABLE IF NOT EXISTS ";
    /**
     * テーブル名より後のCREATE文の文字列
     */
    private static String SQL_CREATE_TABLE_SUFFIX =
            "(" + DB_COL_WORD_ES + " text," +
                    DB_COL_WORD_JA + " text," +
                    DB_COL_EXAMPLE_ES + " text," +
                    DB_COL_EXAMPLE_JA + " text," +
                    DB_COL_TYPE + " text," +
                    "FOREIGN KEY (" + DB_COL_TYPE + ") REFERENCES " +
                    WordTypeCollection.getDbName() + "(" +
                    WordTypeCollection.getDbColWordTypeString() + ")" +
                    ")";
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
    private static String SQL_DROP_TABLE = "DROP TABLE IF EXISTS ";

    private int testId;
    private WordTypeCollection wordTypeCollection;

    public WordCollection(BeanDBHelper dbHelper, int testId){
        super(dbHelper,
                DB_NAME_PREFIX + testId + DB_NAME_SUFFIX,
                DB_VERSION);

        this.testId = testId;
        this.wordTypeCollection = new WordTypeCollection(dbHelper);
    }

    /**
     * DB上のレコードをすべて削除する
     */
    public void deleteAll(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        SQLiteStatement statement = db.compileStatement(SQL_DELETE_ALL + dbName);

        statement.execute();
    }

    @Override
    public Word createBean(String[] row) {
        return new Word(
                row[0],
                row[1],
                row[2],
                row[3],
                wordTypeCollection.selectByWordTypeString(row[4]));
    }

    @Override
    public void createTable(SQLiteOpenHelper dbHelper){
        SQLiteStatement statement = dbHelper.getWritableDatabase().compileStatement(
            SQL_CREATE_TABLE_PREFIX + dbName + SQL_CREATE_TABLE_SUFFIX);

        statement.execute();
    }

    public void dropTable() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        SQLiteStatement statement = db.compileStatement(SQL_DROP_TABLE + dbName);

        statement.execute();
    }

    /**
     * TestWordXX_WORD テーブルをすべて削除
     * @param dbHelper SQLiteOpenHelper
     */
    public static void dropTable(SQLiteOpenHelper dbHelper){
        dropTable(dbHelper.getWritableDatabase());
    }

    public static void dropTable(SQLiteDatabase db){
        Cursor cursor = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name LIKE 'WordTest%$_WORD' ESCAPE '$'", null);

        while(cursor.moveToNext()){
            String tableName = cursor.getString(cursor.getColumnIndex("name"));
            SQLiteStatement statement = db.compileStatement(
                    "DROP TABLE IF EXISTS " + tableName);

            statement.execute();
        }

        cursor.close();
    }

    @Override
    public void insertOrUpdate(Word word) {
        SQLiteDatabase db =dbHelper.getWritableDatabase();
        SQLiteStatement statement = db.compileStatement(
                SQL_INSERT_OR_UPDATE_PREFIX + dbName + SQL_INSERT_OR_UPDATE_SUFFIX);

        statement.bindString(1, word.getWordSpanish());
        statement.bindString(2, word.getWordJapanese());
        statement.bindString(3, word.getExampleSpanish());
        statement.bindString(4, word.getExampleJapanese());
        statement.bindString(5, word.getType().getWordTypeString());

        statement.execute();
        db.close();
    }

    @Override
    public List<Word> selectAll() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQL_SELECT_ALL + dbName, null);
        List<Word> result = new LinkedList<>();

        while(cursor.moveToNext()){
            String wordSpanish = cursor.getString(cursor.getColumnIndex(DB_COL_WORD_ES));
            String wordJapanese = cursor.getString(cursor.getColumnIndex(DB_COL_WORD_JA));
            String exampleSpanish = cursor.getString(cursor.getColumnIndex(DB_COL_EXAMPLE_ES));
            String exampleJapanese = cursor.getString(cursor.getColumnIndex(DB_COL_EXAMPLE_JA));
            String typeString = cursor.getString(cursor.getColumnIndex(DB_COL_TYPE));
            WordType type = wordTypeCollection.selectByWordTypeString(typeString);

            result.add(new Word(wordSpanish, wordJapanese, exampleSpanish, exampleJapanese, type));
        }

        cursor.close();
        db.close();

        return result;
    }
}
