package jp.yutayamazaki.spanishwordtest;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;

import jp.yutayamazaki.spanishwordtest.preference.SettingPreferenceFragment;

public class SettingActivity extends PreferenceActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingPreferenceFragment())
                .commit();
    }
}
