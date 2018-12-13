package jp.yutayamazaki.spanishwordtest.bean;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.io.File;
import java.util.List;

import jp.yutayamazaki.spanishwordtest.dropbox.DropBox;
import jp.yutayamazaki.spanishwordtest.file.CSVLoader;

/**
 * Beanのコレクションクラス
 * @param <T> Beanを継承したクラス
 */
abstract class BeanCollection<T extends Bean> extends SQLiteOpenHelper {
    private static String SQL_DROP_TABLE = "DROP TABLE IF EXISTS ";

    protected String dbName;
    protected int version;

    public BeanCollection(Context context, String dbName, int version){
        super(context, dbName, null, version);
        this.dbName = dbName;
        this.version = version;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        SQLiteStatement statement = sqLiteDatabase.compileStatement(SQL_DROP_TABLE + dbName);

        statement.execute();

        onCreate(sqLiteDatabase);
    }

    /**
     * DropBoxからデータを読み込む
     * @param dropBox DropBoxオブジェクト
     * @param filename DropBox上のファイル名
     * @param tempDir 一時的に保存するディレクトリ
     */
    public void loadBeansByDropBox(DropBox dropBox, String filename, String tempDir){
        String path = dropBox.downloadFile(filename, tempDir);
        List<String[]> rows = CSVLoader.load(new File(tempDir + "/" + filename));

        rows.remove(0);

        for(String[] row : rows){
            // DBにデータを保存する
            insertOrUpdate(createBean(row));
        }

        // ダウンロードしたファイルは削除する
        new File(path).delete();
    }

    /**
     * 文字列配列からデータを作成する
     * ※ 継承先で実装する
     * @param row 文字列配列
     * @return 作成したデータ
     */
    public abstract T createBean(String[] row);

    /**
     * データを挿入する
     * 主キーが同じものがあれば更新する
     * @param bean 挿入、更新するデータ
     */
    public abstract void insertOrUpdate(T bean);

    /**
     * データをすべて取得する
     * @return DBから取得した全レコード
     */
    public abstract List<T> selectAll();
}
