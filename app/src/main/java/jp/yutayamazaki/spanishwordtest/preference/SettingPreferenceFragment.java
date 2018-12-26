package jp.yutayamazaki.spanishwordtest.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import jp.yutayamazaki.spanishwordtest.R;

public class SettingPreferenceFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static int TEST_COUNT_MIN = 1;
    private static int TEST_COUNT_MAX = 99;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
        findPreference("test_count").setOnPreferenceChangeListener(
                new NumberValidator(TEST_COUNT_MIN, TEST_COUNT_MAX));
        findPreference("about_app").setOnPreferenceClickListener(preference -> {
            String title = getResources().getString(R.string.about_app_title);
            String message_format = getResources().getString(R.string.about_app_message);
            String versionName = getVersionName();
            String message = String.format(message_format, versionName);

            new AlertDialog.Builder(getActivity())
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("OK", null)
                    .show();

            return true;
        });
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
        String text = sharedPreferences.getString("test_count", "");

        findPreference("test_count").setSummary(text);
    }

    /**
     * アプリのバージョン名を取得
     * @return アプリのバージョン名
     */
    private String getVersionName(){
        Context context = getContext();

        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo info = packageManager.getPackageInfo(context.getPackageName(), 0);
            Log.d("SpanishWordTest", info.versionName);

            return info.versionName;
        }
        catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }

        return "";
    }
}
