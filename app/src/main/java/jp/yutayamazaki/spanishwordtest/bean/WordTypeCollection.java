package jp.yutayamazaki.spanishwordtest.bean;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.LinkedList;
import java.util.List;

public class WordTypeCollection extends BeanCollection<WordType> {
    private static String DB_NAME = "WordType";
    private static int DB_VERSION = 1;

    private static String DB_COL_WORD_TYPE_STRING = "WordTypeString";
    private static String DB_COL_WORD_TYPE_DISPLAY = "WordTypeDisplay";

    private static String SQL_CREATE_TABLE =
            "CREATE TABLE " + DB_NAME + "(" +
                    DB_COL_WORD_TYPE_STRING + " text primary key," +
                    DB_COL_WORD_TYPE_DISPLAY + " text)";
    private static String SQL_INSERT_OR_UPDATE =
            "REPLACE INTO " + DB_NAME + "(" +
                    DB_COL_WORD_TYPE_STRING + "," +
                    DB_COL_WORD_TYPE_DISPLAY + ")" +
                    "VALUES(?, ?)";
    private static String SQL_SELECT_ALL = "SELECT * FROM " + DB_NAME;
    private static String SQL_SELECT_BY_WORD_TYPE_STRING =
            "SELECT * FROM " + DB_NAME + " " +
                    "WHERE " + DB_COL_WORD_TYPE_STRING + "=?";

    public WordTypeCollection(Context context){
        super(context, DB_NAME, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        SQLiteStatement statement = sqLiteDatabase.compileStatement(SQL_CREATE_TABLE);

        statement.execute();
    }

    @Override
    public WordType createBean(String[] row) {
        return new WordType(row);
    }

    @Override
    public void insertOrUpdate(WordType wordType) {
        SQLiteDatabase db = getWritableDatabase();
        SQLiteStatement statement = db.compileStatement(SQL_INSERT_OR_UPDATE);

        statement.bindString(1, wordType.getWordTypeString());
        statement.bindString(2, wordType.getWordTypeDisplay());

        statement.execute();
        db.close();
    }

    @Override
    public List<WordType> selectAll() {
        SQLiteDatabase db = getReadableDatabase();
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
        db.close();

        return result;
    }

    public WordType selectByWordTypeString(String wordTypeString){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(SQL_SELECT_BY_WORD_TYPE_STRING, new String[]{ wordTypeString });

        cursor.moveToNext();

        String resWordTypeString = cursor.getString(cursor.getColumnIndex(DB_COL_WORD_TYPE_STRING));
        String resWordTypeDisplay = cursor.getString(cursor.getColumnIndex(DB_COL_WORD_TYPE_DISPLAY));

        cursor.close();
        db.close();

        return new WordType(resWordTypeString, resWordTypeDisplay);
    }
}
