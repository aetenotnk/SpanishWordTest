package jp.yutayamazaki.spanishwordtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import jp.yutayamazaki.spanishwordtest.bean.Word;
import jp.yutayamazaki.spanishwordtest.manager.WordTestManager;

public class WordTest extends AppCompatActivity {
    // TODO: 2018/12/16 問題数は設定から変更できるようにする
    private static int TEST_COUNT = 20;

    private WordTestManager testManager;

    private TextView questionTextView;
    private TextView spanishTextView;
    private TextView japaneseTextView;
    private Button previousButton;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_test);

        testManager = WordTestManager.class.cast(
                getIntent().getSerializableExtra(TestList.EXTRA_WORD_TEST_MANAGER));
        setTitle(testManager.getTitle());

        questionTextView = findViewById(R.id.question_text);
        spanishTextView = findViewById(R.id.spanish_text);
        japaneseTextView = findViewById(R.id.japanese_text);
        previousButton = findViewById(R.id.previous_button);
        nextButton = findViewById(R.id.next_button);

        // 各ボタンのクリック時のイベントを実装
        previousButton.setOnClickListener(view ->{
            testManager.previous();
            setContents();
        });
        nextButton.setOnClickListener(view ->{
            if(testManager.isLast()){
                // TODO: 2018/12/16 試験結果画面の実装
                Intent modeSelectIntent = new Intent(getApplication(), ModeSelect.class);

                modeSelectIntent.putExtra(TestList.EXTRA_WORD_TEST_MANAGER, testManager);

                startActivity(modeSelectIntent);
                overridePendingTransition(R.anim.in_right, R.anim.out_left);
            }
            else{
                testManager.next();
                setContents();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        testManager.init(TEST_COUNT);
        setContents();
    }

    private void setContents(){
        Word currentWord = testManager.getCurrentWord();
        String questionText =
                this.getResources().getString(R.string.test_question_prefix) +
                        (testManager.getCurrentTestCount() + 1) +
                        this.getResources().getString(R.string.test_question);

        // テストの問題文を設定
        questionTextView.setText(questionText);
        spanishTextView.setText(WordTestManager.getBlindSpanishExample(currentWord, 0));
        japaneseTextView.setText(WordTestManager.getJapaneseExample(currentWord, 0));

        // 前へボタンの制御
        if(testManager.isFirst()){
            previousButton.setVisibility(Button.INVISIBLE);
        }
        else{
            previousButton.setVisibility(Button.VISIBLE);
        }

        // 次へボタンのテキストの設定
        if(testManager.isLast()){
            nextButton.setText(R.string.test_finish_button);
        }
        else{
            nextButton.setText(R.string.test_next_button);
        }
    }
}
