package jp.yutayamazaki.spanishwordtest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.text.InputType;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import jp.yutayamazaki.spanishwordtest.manager.Question;
import jp.yutayamazaki.spanishwordtest.manager.WordTestManager;

public class WordTest extends AppCompatActivity {
    private static int DEFAULT_TEST_COUNT = 20;
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
    private AppCompatImageButton previousButton;
    private AppCompatImageButton nextButton;
    private LinearLayout answerLinearLayout;
    private List<EditText> answers;

    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_test);

        testManager = (WordTestManager) getIntent().getSerializableExtra(TestList.EXTRA_WORD_TEST_MANAGER);
        String testCountStr = PreferenceManager
                .getDefaultSharedPreferences(this)
                .getString("test_count", String.valueOf(DEFAULT_TEST_COUNT));
        testManager.init(Integer.valueOf(testCountStr));
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
        answerLinearLayout = findViewById(R.id.answers);
        answers = new LinkedList<>();

        setButtonEvent();
        setSwipeEvent();
    }

    @Override
    protected void onStart() {
        super.onStart();

        setContents();
    }

    @Override
    protected void onPause() {
        super.onPause();

        for(int i = 0;i < answers.size();i++) {
            testManager.setCurrentAnswer(i, answers.get(i).getText().toString());
        }
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
            setTestManagerAnswers();
            testManager.previous();
            setContents();
        });
        nextButton.setOnClickListener(view ->{
            // 解答を取得
            setTestManagerAnswers();
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
                        // 右にスワイプ
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

    /**
     * 解答欄を設定する
     */
    private void setAnswerEditText() {
        answerLinearLayout.removeAllViews();
        answers.clear();

        Question question = testManager.getCurrentQuestion();

        for(int i = 0;i < question.getAnswerCount();i++) {
            String answer = testManager.getCurrentAnswer(i);
            boolean isLast = i == question.getAnswerCount() - 1;
            EditText editText = createAnswerEditTest(answer, i, isLast);

            answerLinearLayout.addView(editText,
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
            answers.add(editText);
        }

        EditText firstAnswer = answers.get(0);

        // カーソルを一番後ろに設定
        firstAnswer.setSelection(firstAnswer.getText().toString().length());
        // 最初の解答欄にフォーカス
        firstAnswer.requestFocus();
        // キーボードを表示
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null) {
            imm.showSoftInput(firstAnswer, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    /**
     * 解答欄を作成
     * @param answer 解答欄に入力されている内容
     * @param index 何番目の解答欄か
     * @param isLast 最後の解答欄か
     * @return 作成した解答欄
     */
    private EditText createAnswerEditTest(String answer, int index, boolean isLast) {
        EditText editText = new EditText(this);

        editText.setHint(R.string.input_answer);
        editText.setText(answer);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        // エンターキーを押した時の挙動を設定
        if(isLast) {
            editText.setOnKeyListener((v, code, event) -> {
                if(code == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    nextButton.callOnClick();
                }

                return false;
            });
        }
        else {
            editText.setOnKeyListener((v, code, event) -> {
                if(code == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    EditText next = answers.get(index + 1);

                    next.requestFocus();
                }

                return false;
            });
        }

        return editText;
    }

    /**
     * testManagerに答えを設定する
     */
    private void setTestManagerAnswers() {
        for(int i = 0;i < answers.size();i++) {
            testManager.setCurrentAnswer(i, answers.get(i).getText().toString());
        }
    }

    private void setContents(){
        Question currentQuestion = testManager.getCurrentQuestion();
        String questionText =
                this.getResources().getString(R.string.test_question_prefix) +
                        (testManager.getCurrentTestCount() + 1) +
                        currentQuestion.getQuestionText();
        String spanishText = currentQuestion.getQuestionSpanishText();
        String japaneseText = currentQuestion.getQuestionJapaneseText();

        // テストの問題文を設定
        questionTextView.setText(questionText);
        spanishTextView.setText(spanishText);
        japaneseTextView.setText(japaneseText);

        // 解答欄を設定
        setAnswerEditText();

        // 前へボタンの制御
        if(testManager.isFirst()){
            previousButton.setVisibility(Button.INVISIBLE);
        }
        else{
            previousButton.setVisibility(Button.VISIBLE);
        }

        // 次へボタンの画像の設定
        if(testManager.isLast()){
            nextButton.setImageResource(R.drawable.ic_check_24dp);
        }
        else{
            nextButton.setImageResource(R.drawable.ic_navigate_next_24dp);
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
