package jp.yutayamazaki.spanishwordtest.bean;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import jp.yutayamazaki.spanishwordtest.dropbox.DropBox;
import jp.yutayamazaki.spanishwordtest.file.CSVLoader;

/**
 * Beanのコレクションクラス
 * @param <T> Beanを継承したクラス
 */
class BeanCollection<T extends Bean> {
    private List<T> list;
    private List<String> header;

    public BeanCollection(){
        list = new LinkedList<>();
        header = Collections.emptyList();
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

        header = Arrays.asList(rows.get(0));
        rows.remove(0);

        for(String[] row : rows){
            list.add(createBean(row));
        }

        // ダウンロードしたファイルは削除する
        new File(path).delete();
    }

    /**
     * 保持しているデータリストをコピーして取得する
     * @return データリスト
     */
    @SuppressWarnings("unchecked")
    public List<T> getAll(){
        List<T> res = new LinkedList<>();

        for(Bean bean : this.list){
            res.add((T)bean.copy());
        }

        return res;
    }

    /**
     * カラム名を取得する
     * @return カラム名のリスト
     */
    public List<String> getHeader(){
        List<String> res = new LinkedList<>();

        res.addAll(this.header);

        return res;
    }

    /**
     * 文字列配列からデータを作成する
     * ※ 継承先で実装する
     * @param row 文字列配列
     * @return 作成したデータ
     */
    protected T createBean(String[] row){
        return null;
    }
}
