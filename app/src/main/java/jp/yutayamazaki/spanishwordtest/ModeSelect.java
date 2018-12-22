package jp.yutayamazaki.spanishwordtest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import jp.yutayamazaki.spanishwordtest.manager.WordTestManager;

public class ModeSelect extends AppCompatActivity {
    private WordTestManager testManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_select);

        testManager = WordTestManager.class.cast(
                getIntent().getSerializableExtra(TestList.EXTRA_WORD_TEST_MANAGER));
        setTitle(testManager.getTitle());

        Button startButton = findViewById(R.id.start_button);
        Button checkButton = findViewById(R.id.check_button);
        startButton.setOnClickListener(view -> {
            Intent wordTestIntent = new Intent(getApplication(), WordTest.class);

            wordTestIntent.putExtra(TestList.EXTRA_WORD_TEST_MANAGER, testManager);

            startActivity(wordTestIntent);
            overridePendingTransition(R.anim.in_right, R.anim.out_left);
        });
        checkButton.setOnClickListener(view ->{
            Intent checkQuestionIntent = new Intent(getApplication(), CheckQuestion.class);

            checkQuestionIntent.putExtra(TestList.EXTRA_WORD_TEST_MANAGER, testManager);

            startActivity(checkQuestionIntent);
            overridePendingTransition(R.anim.in_right, R.anim.out_left);
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.in_left, R.anim.out_right);
    }
}
