package jp.yutayamazaki.spanishwordtest;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TestUtil {
    /**
     * リソースファイルの文字列を読み込む
     * @param filepath resources以下のファイル名、パス
     * @return ファイルの中身
     */
    public static String readResourceFile(String filepath) throws  Exception{
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(filepath);
        MatcherAssert.assertThat(inputStream, CoreMatchers.is(CoreMatchers.notNullValue()));

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String inputStr;
        while((inputStr = br.readLine()) != null){
            stringBuilder.append(inputStr);
        }
        br.close();

        return stringBuilder.toString();
    }

    /**
     * ファイルからテキストを読み込む
     * @param file 読み込むファイル
     * @return 読み込んだテキスト
     * @throws Exception ファイルが開けないと例外を投げる
     */
    public static String readResourceFile(File file) throws Exception{
        StringBuilder stringBuilder = new StringBuilder();

        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            String line;

            while((line = br.readLine()) != null){
                stringBuilder.append(line);
            }
        }

        return stringBuilder.toString();
    }

    /**
     * ファイルまたはディレクトリを削除する
     * ディレクトリの場合は再帰的に削除
     * @param file ファイルまたはディレクトリ
     */
    public static void deleteFiles(File file){
        if(!file.exists()){
            return;
        }

        if(file.isFile()){
            System.out.println("delete: " + file.getAbsolutePath());
            file.delete();
            return;
        }

        for(File innerFile : file.listFiles()){
            deleteFiles(innerFile);
        }

        file.delete();
        System.out.println("delete: " + file.getAbsolutePath());
    }
}
