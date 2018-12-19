package jp.yutayamazaki.spanishwordtest.widget;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import jp.yutayamazaki.spanishwordtest.R;
import jp.yutayamazaki.spanishwordtest.bean.Word;
import jp.yutayamazaki.spanishwordtest.manager.WordTestManager;

public class ResultListAdapter extends RecyclerView.Adapter<ResultListHolder> {
    private String NUMBER_PREFIX = "Q";
    private String NUMBER_SUFFIX = ".";

    private WordTestManager testManager;

    private List<Pair<Word, Integer>> filteredWords;

    public enum Filter{
        ALL,
        CORRECT,
        MISTAKE
    }

    public ResultListAdapter(WordTestManager testManager){
        this.testManager = testManager;
        this.filteredWords = testManager.getQuestionWordPairs();
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
        Word word = filteredWords.get(position).first;
        // スペイン語の単語の前に問題番号と評価の記号を付ける
        WordTestManager.Grade grade =
                WordTestManager.evaluate(word, 0, testManager.getAnswer(position));
        int questionNumber = filteredWords.get(position).second + 1;
        String numberText = NUMBER_PREFIX + questionNumber + NUMBER_SUFFIX;
        String spanishWordText = grade + numberText + word.getWordSpanish();

        holder.spanishWordText.setText(spanishWordText);
        holder.japaneseWordText.setText(word.getWordJapanese());
        holder.spanishExampleText.setText(word.getExampleSpanish());
        holder.japaneseExampleText.setText(word.getExampleJapanese());
    }

    @Override
    public int getItemCount() {
        return filteredWords.size();
    }

    /**
     * 表示する単語を絞りこむ
     * @param filterCondition 絞り込む条件
     */
    public void filterWords(Filter filterCondition){
        switch (filterCondition){
            case ALL:
                filteredWords = testManager.getQuestionWordPairs();
                break;
            case CORRECT:
                filteredWords = testManager.getCorrectWordPairs();
                break;
            case MISTAKE:
                filteredWords = testManager.getMistakeWordPairs();
                break;
            default:
                filteredWords = Collections.emptyList();
        }
        notifyDataSetChanged();
    }
}
