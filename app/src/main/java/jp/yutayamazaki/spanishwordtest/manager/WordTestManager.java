package jp.yutayamazaki.spanishwordtest.manager;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import jp.yutayamazaki.spanishwordtest.bean.TestTitle;
import jp.yutayamazaki.spanishwordtest.bean.Word;
import jp.yutayamazaki.spanishwordtest.bean.WordCollection;

public class WordTestManager implements Serializable {
    private static String BLANK = "(    )";
    private static String EXAMPLE_SPLIT = " / ";

    private TestTitle testTitle;
    private List<Word> words;
    private List<Word> questionWords;
    private int testCount;
    private int currentTestCount;

    public WordTestManager(TestTitle testTitle, WordCollection wordCollection){
        this.testTitle = testTitle;
        this.words = wordCollection.selectAll();
        this.questionWords = new LinkedList<>();
    }

    public void init(int testCount){
        this.testCount = testCount;
        this.currentTestCount = 0;

        setWords();
    }

    public String getTitle(){
        return testTitle.getTitle();
    }

    public Word getCurrentWord(){
        return questionWords.get(currentTestCount);
    }

    public void next(){
        currentTestCount++;
    }

    public void previous(){
        currentTestCount--;
    }

    public boolean isFirst(){
        return currentTestCount == 0;
    }

    public boolean isLast(){
        return currentTestCount == (questionWords.size() - 1);
    }

    public int getCurrentTestCount(){
        return currentTestCount;
    }

    /**
     * かっこ内を隠したスペイン語の例文を取得する
     * @param word 単語
     * @return かっこ内を隠したスペイン語の例文
     */
    public static String getBlindSpanishExample(Word word, int index){
        Pattern pattern = Pattern.compile("\\(.*\\)");
        Matcher matcher = pattern.matcher(getSpanishExample(word, index));

        return matcher.replaceAll(BLANK);
    }

    public static String getSpanishExample(Word word, int index){
        return word.getExampleSpanish().split(EXAMPLE_SPLIT)[index];
    }

    public static String getJapaneseExample(Word word, int index){
        return word.getExampleJapanese().split(EXAMPLE_SPLIT)[index];
    }

    /**
     * 出題する単語を設定する
     */
    private void setWords(){
        // 例文を含む単語だけ抽出
        List<Word> filteredWords = words.stream().filter(
                w -> !w.getExampleSpanish().equals("")).collect(Collectors.toList());

        questionWords.clear();

        // 抽出した単語からランダムに出題する問題を設定
        Collections.shuffle(filteredWords);
        for(int i = 0;i < testCount;i++){
            questionWords.add(filteredWords.get(i));
        }
    }
}
