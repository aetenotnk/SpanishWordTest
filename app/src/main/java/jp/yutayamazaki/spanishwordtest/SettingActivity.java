package jp.yutayamazaki.spanishwordtest;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

public class SettingActivity extends PreferenceActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingPreferenceFragment())
                .commit();
    }

    public static class SettingPreferenceFragment extends PreferenceFragment
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
}
