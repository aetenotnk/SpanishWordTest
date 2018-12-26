package jp.yutayamazaki.spanishwordtest.preference;

import android.preference.Preference;
import android.widget.Toast;

import jp.yutayamazaki.spanishwordtest.R;

/**
 * 設定の数字の入力が範囲ないかチェックするリスナー
 */
public class NumberValidator implements Preference.OnPreferenceChangeListener {
    private int minValue;
    private int maxValue;

    public NumberValidator(int min, int max){
        this.minValue = min;
        this.maxValue = max;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int newValueInt = Integer.valueOf(newValue.toString());
        String message = preference.getContext().getResources().getString(R.string.test_count_alert);

        // 入力をチェックして範囲外ならメッセージを表示する
        if(newValueInt < minValue || maxValue < newValueInt){
            Toast.makeText(preference.getContext(), message, Toast.LENGTH_SHORT).show();

            return false;
        }

        return true;
    }
}
