package jp.yutayamazaki.spanishwordtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import jp.yutayamazaki.spanishwordtest.manager.WordTestManager;

public class TestResult extends AppCompatActivity {
    private WordTestManager testManager;

    private TextView scoreText;
    private TextView baseScoreText;
    private Spinner filterSpinner;
    private RecyclerView resultList;
    private Button tryAgainButton;
    private Button checkQuestionButton;
    private Button returnTestListButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);

        this.testManager = WordTestManager.class.cast(
                getIntent().getSerializableExtra(TestList.EXTRA_WORD_TEST_MANAGER));

        this.scoreText = findViewById(R.id.score_actual);
        this.baseScoreText = findViewById(R.id.score_base);
        this.filterSpinner = findViewById(R.id.result_filter);
        this.resultList = findViewById(R.id.result_list);
        this.tryAgainButton = findViewById(R.id.try_again_button);
        this.checkQuestionButton = findViewById(R.id.check_button);
        this.returnTestListButton = findViewById(R.id.return_test_list_button);
    }
}