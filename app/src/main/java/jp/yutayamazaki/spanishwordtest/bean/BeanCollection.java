package jp.yutayamazaki.spanishwordtest.bean;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import jp.yutayamazaki.spanishwordtest.dropbox.DropBox;
import jp.yutayamazaki.spanishwordtest.file.CSVLoader;

public class BeanCollection<T extends Bean> {

    private List<T> list;
    private List<String> header;

    protected BeanCollection(){
        list = new LinkedList<>();
        header = Collections.emptyList();
    }

    public void loadBeansByDropBox(DropBox dropBox, String filename, String tempDir){
        String path = dropBox.downloadFile(filename, tempDir);
        List<String[]> rows = CSVLoader.load(new File(tempDir + "/" + filename));

        header = Arrays.asList(rows.get(0));
        rows.remove(0);

        for(String[] row : rows){
            list.add(createBean(row));
        }

        new File(path).delete();
    }

    @SuppressWarnings("unchecked")
    public List<T> getAll(){
        List<T> res = new LinkedList<>();

        for(Bean bean : this.list){
            res.add((T)bean.copy());
        }

        return res;
    }

    public List<String> getHeader(){
        List<String> res = new LinkedList<>();

        res.addAll(this.header);

        return res;
    }

    protected T createBean(String[] row){
        return null;
    }
}
