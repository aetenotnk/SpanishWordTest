package jp.yutayamazaki.spanishwordtest;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import jp.yutayamazaki.spanishwordtest.bean.TestTitle;
import jp.yutayamazaki.spanishwordtest.bean.TestTitleCollection;
import jp.yutayamazaki.spanishwordtest.dropbox.DropBox;

public class TestList extends AppCompatActivity {
    private static String TEST_TITLE_FILE = "testlist.csv";

    private DropBox dropBox;
    private TestTitleCollection testTitleCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_list);

        String token = getResources().getString(R.string.dropbox_token);
        String userAgent = getResources().getString(R.string.dropbox_useragant);
        this.dropBox = new DropBox(token, userAgent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        ScheduledExecutorService schedule = Executors.newSingleThreadScheduledExecutor();

        schedule.submit(new Runnable() {
            @Override
            public void run() {
                loadDataFromDropBox();
            }
        });

        schedule.submit(new Runnable() {
            @Override
            public void run() {
                setTestList();
            }
        });
    }

    /**
     * DropBoxからデータを読み込む
     */
    private void loadDataFromDropBox(){
        testTitleCollection = new TestTitleCollection(this);
        // テスト一覧のデータを取得
        testTitleCollection.loadBeansByDropBox(dropBox,
                TEST_TITLE_FILE,
                getFilesDir().getAbsolutePath());
    }

    private void setTestList(){
        final List<HashMap<String, String>>listData = new ArrayList<>();

        for(TestTitle testTitle : testTitleCollection.selectAll()){
            HashMap<String, String>row = new HashMap<>();

            row.put("title", testTitle.getTitle());
            row.put("caption", testTitle.getCaption());

            listData.add(row);
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

                // リスト内の項目をタップした時の処理
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent modeSelectIntent = new Intent(getApplication(), ModeSelect.class);

                        startActivity(modeSelectIntent);
                        overridePendingTransition(R.anim.in_right, R.anim.out_left);
                    }
                });
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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        resizeTestList();
    }
}
