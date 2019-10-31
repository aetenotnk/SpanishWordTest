package jp.yutayamazaki.spanishwordtest.bean;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.LinkedList;
import java.util.List;

public class WordTypeCollection extends BeanCollection<WordType> {
    private static String DB_NAME = "WordType";
    private static int DB_VERSION = 2;

    private static String DB_COL_WORD_TYPE_STRING = "word_type_string";
    private static String DB_COL_WORD_TYPE_DISPLAY = "word_type_display";

    private static String SQL_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + DB_NAME + "(" +
                    DB_COL_WORD_TYPE_STRING + " text primary key," +
                    DB_COL_WORD_TYPE_DISPLAY + " text)";
    private static String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + DB_NAME;
    private static String SQL_INSERT_OR_UPDATE =
            "REPLACE INTO " + DB_NAME + "(" +
                    DB_COL_WORD_TYPE_STRING + "," +
                    DB_COL_WORD_TYPE_DISPLAY + ")" +
                    "VALUES(?, ?)";
    private static String SQL_SELECT_ALL = "SELECT * FROM " + DB_NAME;
    private static String SQL_SELECT_BY_WORD_TYPE_STRING =
            "SELECT * FROM " + DB_NAME + " " +
                    "WHERE " + DB_COL_WORD_TYPE_STRING + "=?";
    private static String SQL_DELETE_ALL = "DELETE FROM " + DB_NAME;

    public WordTypeCollection(BeanDBHelper dbHelper){
        super(dbHelper, DB_NAME, DB_VERSION);
    }

    @Override
    public WordType createBean(String[] row) {
        return new WordType(row);
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
    public void insertOrUpdate(WordType wordType) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        SQLiteStatement statement = db.compileStatement(SQL_INSERT_OR_UPDATE);

        statement.bindString(1, wordType.getWordTypeString());
        statement.bindString(2, wordType.getWordTypeDisplay());

        statement.execute();
    }

    @Override
    public List<WordType> selectAll() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQL_SELECT_ALL, null);
        List<WordType> result = new LinkedList<>();

        int wordTypeStringColIndex = cursor.getColumnIndex(DB_COL_WORD_TYPE_STRING);
        int wordTypeDisplayColIndex = cursor.getColumnIndex(DB_COL_WORD_TYPE_DISPLAY);

        while(cursor.moveToNext()){
            String wordTypeString = cursor.getString(wordTypeStringColIndex);
            String wordTypeDisplay = cursor.getString(wordTypeDisplayColIndex);

            result.add(new WordType(wordTypeString, wordTypeDisplay));
        }

        cursor.close();

        return result;
    }

    public WordType selectByWordTypeString(String wordTypeString){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQL_SELECT_BY_WORD_TYPE_STRING, new String[]{ wordTypeString });

        cursor.moveToNext();

        String resWordTypeString = cursor.getString(cursor.getColumnIndex(DB_COL_WORD_TYPE_STRING));
        String resWordTypeDisplay = cursor.getString(cursor.getColumnIndex(DB_COL_WORD_TYPE_DISPLAY));

        cursor.close();

        return new WordType(resWordTypeString, resWordTypeDisplay);
    }

    public void deleteAll(){
        SQLiteStatement statement = dbHelper.getWritableDatabase().compileStatement(SQL_DELETE_ALL);

        statement.execute();
    }

    public static String getDbName(){
        return DB_NAME;
    }

    public static String getDbColWordTypeString(){
        return DB_COL_WORD_TYPE_STRING;
    }
}
