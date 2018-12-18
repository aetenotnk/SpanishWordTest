package jp.yutayamazaki.spanishwordtest.widget;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import jp.yutayamazaki.spanishwordtest.R;

public class ResultListHolder extends RecyclerView.ViewHolder {
    public TextView spanishWordText;
    public TextView japaneseWordText;
    public TextView spanishExampleText;
    public TextView japaneseExampleText;

    public ResultListHolder(View itemView) {
        super(itemView);

        this.spanishWordText = itemView.findViewById(R.id.word_spanish);
        this.japaneseWordText = itemView.findViewById(R.id.word_japanese);
        this.spanishExampleText = itemView.findViewById(R.id.spanish_example);
        this.japaneseExampleText = itemView.findViewById(R.id.japanese_example);
    }
}
