package jp.yutayamazaki.spanishwordtest.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * CSVファイル読み込みクラス
 */
public class CSVLoader {
    /**
     * CSVを読み込む
     * @param file 読み込むファイル
     * @return 各行をカンマで分割した文字列配列のリスト
     */
    @SuppressWarnings("unchecked")
    public static List<String[]> load(File file){
        List data = new LinkedList();

        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            // ヘッダー
            String[] header = parseHeader(br.readLine());
            data.add(header);

            while(br.ready()){
                data.add(parseLine(br.readLine(), header.length));
            }
        }
        catch (IOException e){
            e.printStackTrace();
            return Collections.emptyList();
        }

        return data;
    }

    /**
     * ヘッダ行をカンマで分割する
     * @param line ヘッダ行
     * @return 分割した文字列配列
     */
    private static String[] parseHeader(String line){
        // TODO: 2018/12/10 もともとの文字列にカンマが含まれることも想定する
        return line.split(",");
    }

    /**
     * 列数を指定して行をカンマで分割する
     * @param line 行
     * @param column 列数
     * @return 分割した文字列配列
     */
    private static String[] parseLine(String line, int column){
        // TODO: 2018/12/10 もともとの文字列にカンマが含まれることも想定する
        List<String>row = new ArrayList<>(Arrays.asList(line.split(",")));

        // もし足りなければ必要な分まで空の文字列を加える
        while(row.size() < column){
            row.add("");
        }

        return row.toArray(new String[0]);
    }
}
