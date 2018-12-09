package jp.yutayamazaki.spanishwordtest.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class CSVLoader {
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

    private static String[] parseHeader(String line){
        return line.split(",");
    }

    private static String[] parseLine(String line, int column){
        List<String>row = new ArrayList<>(Arrays.asList(line.split(",")));

        // もし足りなければ必要な分まで空の文字列を加える
        while(row.size() < column){
            row.add("");
        }

        return row.toArray(new String[0]);
    }
}
