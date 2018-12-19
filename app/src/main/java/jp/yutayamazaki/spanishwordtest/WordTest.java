package jp.yutayamazaki.spanishwordtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import jp.yutayamazaki.spanishwordtest.bean.Word;
import jp.yutayamazaki.spanishwordtest.manager.WordTestManager;

public class WordTest extends AppCompatActivity {
    // TODO: 2018/12/16 問題数は設定から変更できるようにする
    private static int TEST_COUNT = 20;
    // スワイプ判定する最低のX軸の移動距離
    private static float SWIPE_MIN_DISTANCE = 50;
    // スワイプ判定する最低のX軸の速度
    private static float SWIPE_MIN_SPEED = 200;
    // Y軸の移動距離がこれ以上ならX軸の移動と判定しない
    private static float SWIPE_MAX_OFF_PATH = 200;

    private WordTestManager testManager;

    private TextView questionTextView;
    private TextView spanishTextView;
    private TextView japaneseTextView;
    private Button previousButton;
    private Button nextButton;
    private EditText answerText;

    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_test);

        testManager = WordTestManager.class.cast(
                getIntent().getSerializableExtra(TestList.EXTRA_WORD_TEST_MANAGER));
        testManager.init(TEST_COUNT);
        setTitle(testManager.getTitle());

        // アクションバーのバックボタンを有効にする
        ActionBar actionbar = getSupportActionBar();
        if(actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
        }

        questionTextView = findViewById(R.id.question_text);
        spanishTextView = findViewById(R.id.spanish_text);
        japaneseTextView = findViewById(R.id.japanese_text);
        previousButton = findViewById(R.id.previous_button);
        nextButton = findViewById(R.id.next_button);
        answerText = findViewById(R.id.answer);

        setButtonEvent();
        setSwipeEvent();
    }

    @Override
    protected void onStart() {
        super.onStart();

        setContents();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // アクションバーのバックボタンが押されたときの挙動
        if(id == android.R.id.home){
            backModeSelect();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 端末の戻るボタンを押したときの挙動
     */
    @Override
    public void onBackPressed() {
        backModeSelect();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    /**
     * 各ボタンのイベントを設定
     */
    private void setButtonEvent(){
        previousButton.setOnClickListener(view ->{
            // 解答を取得
            testManager.setAnswer(answerText.getText().toString());
            testManager.previous();
            setContents();
        });
        nextButton.setOnClickListener(view ->{
            // 解答を取得
            testManager.setAnswer(answerText.getText().toString());
            if(testManager.isLast()){
                Intent resultIntent = new Intent(getApplication(), TestResult.class);

                resultIntent.putExtra(TestList.EXTRA_WORD_TEST_MANAGER, testManager);

                startActivity(resultIntent);
                finish();
                overridePendingTransition(R.anim.in_right, R.anim.out_left);
            }
            else{
                testManager.next();
                setContents();
            }
        });
    }

    /**
     * スワイプのイベントを設定
     */
    private void setSwipeEvent(){
        GestureDetector.SimpleOnGestureListener listener =
                new GestureDetector.SimpleOnGestureListener(){
                    @Override
                    public boolean onFling(MotionEvent e1,
                                           MotionEvent e2,
                                           float velocityX,
                                           float velocityY) {
                        float distance_x = Math.abs(e1.getX() - e2.getX());
                        float velocity_x = Math.abs(velocityY);
                        float distance_y = Math.abs(e1.getY() - e2.getY());

                        if(distance_y > SWIPE_MAX_OFF_PATH){
                            return false;
                        }

                        if(velocity_x < SWIPE_MIN_SPEED){
                            return false;
                        }

                        if(distance_x < SWIPE_MIN_DISTANCE){
                            return false;
                        }

                        // 左にスワイプ
                        if(e1.getX() - e2.getX() > 0){
                            nextButton.callOnClick();
                        }
                        // 右にフリック
                        else{
                            if(!testManager.isFirst()){
                                previousButton.callOnClick();
                            }
                        }

                        return false;
                    }
                };

        gestureDetector = new GestureDetector(this, listener);
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

        // 解答欄を設定
        answerText.setText(testManager.getAnswer());

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

    /**
     * モード選択に戻る
     */
    private void backModeSelect(){
        // ダイアログで戻るか確認する
        new AlertDialog.Builder(this)
                .setTitle("Caution")
                .setMessage("モード選択に戻りますか？")
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    Intent intent = new Intent(getApplication(), ModeSelect.class);

                    intent.putExtra(TestList.EXTRA_WORD_TEST_MANAGER, testManager);

                    startActivity(intent);
                    overridePendingTransition(R.anim.in_left, R.anim.out_right);
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> {
                    // 何もしない
                })
                .show();
    }
}
