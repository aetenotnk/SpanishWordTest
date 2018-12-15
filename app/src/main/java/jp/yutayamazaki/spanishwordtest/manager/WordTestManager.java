package jp.yutayamazaki.spanishwordtest.manager;

import java.io.Serializable;
import java.util.List;

import jp.yutayamazaki.spanishwordtest.bean.TestTitle;
import jp.yutayamazaki.spanishwordtest.bean.Word;
import jp.yutayamazaki.spanishwordtest.bean.WordCollection;

public class WordTestManager implements Serializable {
    private TestTitle testTitle;
    private List<Word> words;

    public WordTestManager(TestTitle testTitle, WordCollection wordCollection){
        this.testTitle = testTitle;
        this.words = wordCollection.selectAll();
    }

    public String getTitle(){
        return testTitle.getTitle();
    }
}
