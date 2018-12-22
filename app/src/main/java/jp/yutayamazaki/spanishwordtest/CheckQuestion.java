package jp.yutayamazaki.spanishwordtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import jp.yutayamazaki.spanishwordtest.manager.WordTestManager;

public class CheckQuestion extends AppCompatActivity {
    private WordTestManager testManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_question);

        testManager = WordTestManager.class.cast(
                getIntent().getSerializableExtra(TestList.EXTRA_WORD_TEST_MANAGER));
        setTitle(testManager.getTitle());
    }
}
