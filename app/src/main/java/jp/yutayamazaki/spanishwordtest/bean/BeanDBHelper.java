package jp.yutayamazaki.spanishwordtest.bean;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BeanDBHelper extends SQLiteOpenHelper {
    private static String DB_NAME = "db";
    private static int DB_VERSION = 2;

    public BeanDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        TestTitleCollection.dropTable(sqLiteDatabase);
        WordCollection.dropTable(sqLiteDatabase);
        WordTypeCollection.dropTable(sqLiteDatabase);

        onCreate(sqLiteDatabase);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);

        db.setForeignKeyConstraintsEnabled(true);
    }
}
