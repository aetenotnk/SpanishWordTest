package jp.yutayamazaki.spanishwordtest.widget;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import jp.yutayamazaki.spanishwordtest.R;
import jp.yutayamazaki.spanishwordtest.bean.Word;
import jp.yutayamazaki.spanishwordtest.manager.WordTestManager;

public class CheckQuestionAdapter extends RecyclerView.Adapter<CheckQuestionHolder> {
    private List<Word> words;

    public CheckQuestionAdapter(WordTestManager testManager){
        words = testManager.getValidWords();
    }

    @NonNull
    @Override
    public CheckQuestionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.check_list_row, parent, false);

        return new CheckQuestionHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckQuestionHolder holder, int position) {
        Word word = words.get(position);

        holder.spanishWordText.setText(word.getWordSpanish());
        holder.japaneseWordText.setText(word.getWordJapanese());
        holder.spanishExampleText.setText(word.getExampleSpanish());
        holder.japaneseExampleText.setText(word.getExampleJapanese());
    }

    @Override
    public int getItemCount() {
        return words.size();
    }
}
