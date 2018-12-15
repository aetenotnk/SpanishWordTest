package jp.yutayamazaki.spanishwordtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import jp.yutayamazaki.spanishwordtest.manager.WordTestManager;

public class ModeSelect extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_select);

        WordTestManager manager =WordTestManager.class.cast(
                getIntent().getSerializableExtra(TestList.EXTRA_WORD_TEST_MANAGER));
        setTitle(manager.getTitle());
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.in_left, R.anim.out_right);
    }
}
