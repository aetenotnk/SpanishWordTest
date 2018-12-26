package jp.yutayamazaki.spanishwordtest.preference;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import jp.yutayamazaki.spanishwordtest.R;

public class SettingPreferenceFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
    }

    @Override
    public void onStart() {
        super.onStart();
        setSummary();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager()
                .getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager()
                .getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        setSummary();
    }

    /**
     * 各設定の概要を表示する
     */
    private void setSummary(){
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        String text = sharedPreferences.getString("testText", "");

        findPreference("testText").setSummary(text);
    }
}
