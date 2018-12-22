package jp.yutayamazaki.spanishwordtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

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
