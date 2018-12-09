package jp.yutayamazaki.spanishwordtest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import jp.yutayamazaki.spanishwordtest.dropbox.DropBox;

public class TestList extends AppCompatActivity {
    private DropBox dropBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_list);

        String token = getResources().getString(R.string.dropbox_token);
        String userAgent = getResources().getString(R.string.dropbox_useragant);
        this.dropBox = new DropBox(token, userAgent);

        new Thread(new Runnable() {
            @Override
            public void run() {
                setTestList();
            }
        }).start();
    }

    private void setTestList(){
        String filepath = dropBox.downloadFile("testlist.csv", getFilesDir().getPath());
        ArrayList<HashMap<String, String>> listData = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new FileReader(new File(filepath)))){
            // ヘッダを飛ばす
            br.readLine();

            while(br.ready()){
                String line = br.readLine();

                String[] row = line.split(",");
                HashMap<String, String>data = new HashMap<>();

                data.put("title", row[1]);
                data.put("caption", row.length < 3 ? "" : row[2]);

                listData.add(data);
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }


        final SimpleAdapter adapter = new SimpleAdapter(
                this,
                listData,
                R.layout.test_list_row,
                new String[]{"title", "caption"},
                new int[]{R.id.test_row_title, R.id.test_row_caption}
        );

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView listView = ListView.class.cast(findViewById(R.id.test_list));
                listView.setAdapter(adapter);
            }
        });
    }

    /**
     * そのままだと画面下のナビゲーションバーに被る可能性があるので、
     * リストのサイズを調整する。
     */
    private void resizeTestList(){
        RelativeLayout contents = RelativeLayout.class.cast(findViewById(R.id.test_list_contents));
        ListView listView = ListView.class.cast(findViewById(R.id.test_list));

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = contents.getHeight() - 16;
        listView.setLayoutParams(params);
    }

    private void accessDropBox(){
        String path = dropBox.downloadFile("testlist.csv", getFilesDir().getPath());

        File file = new File(path);

        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            String line;

            while((line = br.readLine()) != null){
                Log.d("textfile", line);
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        resizeTestList();
    }
}
