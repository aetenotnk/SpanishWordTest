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
    private static char ESCAPE_CHAR = '"';

    /**
     * CSVを読み込む
     * @param file 読み込むファイル
     * @return 各行をカンマで分割した文字列配列のリスト
     */
    @SuppressWarnings("unchecked")
    public static List<String[]> load(File file){
        List data = new LinkedList();

        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            // ファイルの中身が空なら空のリストを返す
            if(!br.ready()){
                return Collections.emptyList();
            }

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
        return split(line);
    }

    /**
     * 列数を指定して行をカンマで分割する
     * @param line 行
     * @param column 列数
     * @return 分割した文字列配列
     */
    private static String[] parseLine(String line, int column){
        List<String>row = new ArrayList<>(Arrays.asList(split(line)));

        // もし足りなければ必要な分まで空の文字列を加える
        while(row.size() < column){
            row.add("");
        }

        return row.toArray(new String[0]);
    }

    private static String[] split(String line){
        List<String>list = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        String[] temp;

        // 連続したダブルクォートを1つに置き換え
        line = line.replaceAll("\"\"", "\"");
        temp = line.split(",");

        for(String part : temp){
            if(stringBuilder.length() == 0){
                if(part.charAt(0) == ESCAPE_CHAR) {
                    stringBuilder.append(part);
                }
                else{
                    list.add(part);
                }
            }
            else{
                stringBuilder.append(part);

                if(part.charAt(part.length() - 1) == ESCAPE_CHAR){
                    list.add(stringBuilder.toString());
                    stringBuilder = new StringBuilder();
                }
            }
        }

        return list.toArray(new String[0]);
    }
}
