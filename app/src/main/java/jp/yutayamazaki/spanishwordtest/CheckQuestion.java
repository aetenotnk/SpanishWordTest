package jp.yutayamazaki.spanishwordtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import jp.yutayamazaki.spanishwordtest.manager.WordTestManager;
import jp.yutayamazaki.spanishwordtest.widget.CheckQuestionAdapter;

public class CheckQuestion extends AppCompatActivity {
    private WordTestManager testManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_question);

        testManager = (WordTestManager) getIntent().getSerializableExtra(TestList.EXTRA_WORD_TEST_MANAGER);
        setTitle(testManager.getTitle());

        RecyclerView questionList = findViewById(R.id.question_list);
        questionList.setAdapter(new CheckQuestionAdapter(testManager));
        questionList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // アクションバーのバックボタンが押されたときの挙動
        if(id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        // アクティビティが終わるときのアニメションを設定
        overridePendingTransition(R.anim.in_left, R.anim.out_right);
    }
}
