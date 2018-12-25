package jp.yutayamazaki.spanishwordtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import jp.yutayamazaki.spanishwordtest.bean.TestTitle;
import jp.yutayamazaki.spanishwordtest.bean.TestTitleCollection;
import jp.yutayamazaki.spanishwordtest.bean.WordCollection;
import jp.yutayamazaki.spanishwordtest.dropbox.DropBox;
import jp.yutayamazaki.spanishwordtest.manager.WordTestManager;

public class TestList extends AppCompatActivity {
    public static String EXTRA_WORD_TEST_MANAGER = "WordTestManager";

    private static String TEST_TITLE_FILE = "testlist.csv";

    private DropBox dropBox;
    private ProgressBar progressBar;
    private ScheduledExecutorService loadDataSchedule;
    private List<TestTitle> testTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String token = getResources().getString(R.string.dropbox_token);
        String userAgent = getResources().getString(R.string.dropbox_useragant);
        this.dropBox = new DropBox(token, userAgent);

        // 起動時にDropBoxからデータを取得
        this.loadDataSchedule = Executors.newSingleThreadScheduledExecutor();
        loadDataSchedule.submit(this::loadDataFromDropBox);

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(ProgressBar.VISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadDataSchedule.submit(() -> {
            setTestList();
            progressBar.setVisibility(ProgressBar.INVISIBLE);
        });
    }

    /**
     * DropBoxからデータを読み込む
     */
    private void loadDataFromDropBox(){
        TestTitleCollection testTitleCollection = new TestTitleCollection(this);
        // テスト一覧のデータを取得
        testTitleCollection.loadBeansByDropBox(dropBox,
                TEST_TITLE_FILE,
                getFilesDir().getAbsolutePath());
        testTitles = testTitleCollection.selectAll();
        // 単語データを取得
        for(TestTitle title : testTitles){
            String filePath = title.getFilepath();
            WordCollection wordCollection = new WordCollection(this, title.getId());

            // 単語データを入れ替える
            wordCollection.deleteAll();
            wordCollection.loadBeansByDropBox(dropBox,
                    filePath,
                    getFilesDir().getAbsolutePath());
        }
    }

    private void setTestList(){
        final List<HashMap<String, String>>listData = new ArrayList<>();

        for(TestTitle testTitle : testTitles){
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


        runOnUiThread(() -> {
            ListView listView = ListView.class.cast(findViewById(R.id.test_list));
            listView.setAdapter(adapter);

            // リスト内の項目をタップした時の処理
            listView.setOnItemClickListener((adapterView, view, i, l) -> {
                Intent modeSelectIntent = new Intent(getApplication(), ModeSelect.class);
                TestTitle testTitle = testTitles.get(i);
                WordCollection wordCollection =
                        new WordCollection(TestList.this, testTitle.getId());

                // モード選択画面に渡すWordTestManagerを渡す
                modeSelectIntent.putExtra(EXTRA_WORD_TEST_MANAGER,
                        new WordTestManager(testTitle, wordCollection));

                startActivity(modeSelectIntent);
                overridePendingTransition(R.anim.in_right, R.anim.out_left);
            });
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
