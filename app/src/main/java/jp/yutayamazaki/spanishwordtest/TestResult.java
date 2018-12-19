package jp.yutayamazaki.spanishwordtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import jp.yutayamazaki.spanishwordtest.manager.WordTestManager;
import jp.yutayamazaki.spanishwordtest.widget.ResultListAdapter;

public class TestResult extends AppCompatActivity {
    private WordTestManager testManager;

    private TextView scoreText;
    private TextView baseScoreText;
    private Spinner filterSpinner;
    private RecyclerView resultList;
    private Button tryAgainButton;
    private Button checkQuestionButton;
    private Button returnTestListButton;

    private ResultListAdapter resultListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);

        this.testManager = WordTestManager.class.cast(
                getIntent().getSerializableExtra(TestList.EXTRA_WORD_TEST_MANAGER));
        setTitle(testManager.getTitle());

        this.scoreText = findViewById(R.id.score_actual);
        this.baseScoreText = findViewById(R.id.score_base);
        this.filterSpinner = findViewById(R.id.result_filter);
        this.resultList = findViewById(R.id.result_list);
        this.tryAgainButton = findViewById(R.id.try_again_button);
        this.checkQuestionButton = findViewById(R.id.check_button);
        this.returnTestListButton = findViewById(R.id.return_test_list_button);

        setResultList();
        setScore();
        setButtonListener();
    }

    /**
     * 結果一覧の設定
     */
    private void setResultList(){
        resultListAdapter = new ResultListAdapter(testManager);
        this.resultList.setAdapter(resultListAdapter);
        this.resultList.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * スコアの表示の設定
     */
    private void setScore(){
        this.scoreText.setText(String.valueOf(Math.round(testManager.getScore())));
        this.baseScoreText.setText(String.valueOf(Math.round(testManager.getMaxScore())));
    }

    private void setButtonListener(){
        // フィルターの設定
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ResultListAdapter.Filter filter = ResultListAdapter.Filter.ALL;

                switch (i){
                    case 0:
                        filter = ResultListAdapter.Filter.ALL;
                        break;
                    case 1:
                        filter = ResultListAdapter.Filter.MISTAKE;
                        break;
                    case 2:
                        filter = ResultListAdapter.Filter.CORRECT;
                        break;
                }

                resultListAdapter.filterWords(filter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                resultListAdapter.filterWords(ResultListAdapter.Filter.ALL);
            }
        });
        tryAgainButton.setOnClickListener(view -> {
            Intent wordTestIntent = new Intent(getApplication(), WordTest.class);

            wordTestIntent.putExtra(TestList.EXTRA_WORD_TEST_MANAGER, testManager);

            startActivity(wordTestIntent);
            overridePendingTransition(R.anim.in_left, R.anim.out_right);
        });
    }
}
