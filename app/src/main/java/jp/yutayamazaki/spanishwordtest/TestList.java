package jp.yutayamazaki.spanishwordtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class TestList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_list);

        setTestList();
    }

    private void setTestList(){
        ArrayList<HashMap<String, String>> listData = new ArrayList<>();

        for(int i = 0;i < 10;i++){
            HashMap<String, String> data = new HashMap<>();

            data.put("title", "テスト" + i);
            data.put("caption", "テスト" + i + "です。");

            listData.add(data);
        }

        SimpleAdapter adapter = new SimpleAdapter(
                this,
                listData,
                R.layout.test_list_row,
                new String[]{"title", "caption"},
                new int[]{R.id.test_row_title, R.id.test_row_caption}
        );

        ListView listView = ListView.class.cast(findViewById(R.id.test_list));
        listView.setAdapter(adapter);
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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        resizeTestList();
    }
}
