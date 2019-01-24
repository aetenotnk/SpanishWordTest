package jp.yutayamazaki.spanishwordtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
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
import java.util.stream.Collectors;

import jp.yutayamazaki.spanishwordtest.bean.TestTitle;
import jp.yutayamazaki.spanishwordtest.bean.TestTitleCollection;
import jp.yutayamazaki.spanishwordtest.bean.WordCollection;
import jp.yutayamazaki.spanishwordtest.bean.WordTypeCollection;
import jp.yutayamazaki.spanishwordtest.dropbox.DropBox;
import jp.yutayamazaki.spanishwordtest.manager.WordTestManager;

public class TestList extends AppCompatActivity {
    public static String EXTRA_WORD_TEST_MANAGER = "WordTestManager";

    private static String TEST_TITLE_FILE = "testlist.csv";
    private static String WORD_TYPE_FILE = "wordtype.csv";

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

        AppCompatImageButton settingButton = toolbar.findViewById(R.id.setting_button);
        settingButton.setOnClickListener(view ->
                startActivity(new Intent(getApplication(), SettingActivity.class)));

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
        String tempDir = getFilesDir().getAbsolutePath();

        // 単語タイプ取得
        WordTypeCollection wordTypeCollection = new WordTypeCollection(this);
        wordTypeCollection.loadBeansByDropBox(dropBox,
                WORD_TYPE_FILE,
                tempDir);

        TestTitleCollection testTitleCollection = new TestTitleCollection(this);
        // 単語データを読み込みなおす試験データを取得
        List<TestTitle> reloadTestTitles = testTitleCollection.getUpdatedOrNewTestTitleByDropBox(
                dropBox, TEST_TITLE_FILE, tempDir);

        // 単語データを読み込みなおす
        for(TestTitle title : reloadTestTitles) {
            String filePath = title.getFilepath();
            WordCollection wordCollection = new WordCollection(this, title.getId());

            // 単語データを入れ替える
            wordCollection.deleteAll();
            wordCollection.loadBeansByDropBox(dropBox,
                    filePath,
                    tempDir);
        }

        List<TestTitle> oldTestTitles = testTitleCollection.selectAll();
        testTitleCollection.deleteAll();
        testTitleCollection.loadBeansByDropBox(dropBox, TEST_TITLE_FILE, tempDir);
        List<TestTitle> newTestTitles = testTitleCollection.selectAll();
        List<TestTitle> deletedTestTitles = oldTestTitles.stream()
                .filter(testTitle ->
                        newTestTitles.stream().noneMatch(newTestTitle ->
                                newTestTitle.getId() == testTitle.getId()))
                .collect(Collectors.toList());

        // 削除されたデータをDB上から削除
        for(TestTitle title : deletedTestTitles) {
            WordCollection wordCollection = new WordCollection(this, title.getId());

            testTitleCollection.deleteById(title.getId());
            wordCollection.dropTable();
        }

        testTitles = newTestTitles;
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
            ListView listView = findViewById(R.id.test_list);
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
        RelativeLayout contents = findViewById(R.id.test_list_contents);
        ListView listView = findViewById(R.id.test_list);

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
