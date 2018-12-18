package jp.yutayamazaki.spanishwordtest.widget;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jp.yutayamazaki.spanishwordtest.R;
import jp.yutayamazaki.spanishwordtest.bean.Word;
import jp.yutayamazaki.spanishwordtest.manager.WordTestManager;

public class ResultListAdapter extends RecyclerView.Adapter<ResultListHolder> {
    private String NUMBER_PREFIX = "Q";
    private String NUMBER_SUFFIX = ".";

    private WordTestManager testManager;

    public ResultListAdapter(WordTestManager testManager){
        this.testManager = testManager;
    }

    @NonNull
    @Override
    public ResultListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.result_list_row, parent, false);

        return new ResultListHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultListHolder holder, int position) {
        Word word = testManager.getQuestionWord(position);
        // スペイン語の単語の前に問題番号と評価の記号を付ける
        WordTestManager.Grade grade =
                WordTestManager.evaluate(word, 0, testManager.getAnswer(position));
        String numberText = NUMBER_PREFIX + (position + 1) + NUMBER_SUFFIX;
        String spanishWordText = grade + numberText + word.getWordSpanish();

        holder.spanishWordText.setText(spanishWordText);
        holder.japaneseWordText.setText(word.getWordJapanese());
        holder.spanishExampleText.setText(word.getExampleSpanish());
        holder.japaneseExampleText.setText(word.getExampleJapanese());
    }

    @Override
    public int getItemCount() {
        return testManager.getTestCount();
    }
}
