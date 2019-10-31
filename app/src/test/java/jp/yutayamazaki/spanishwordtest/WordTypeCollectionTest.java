package jp.yutayamazaki.spanishwordtest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.List;

import jp.yutayamazaki.spanishwordtest.bean.BeanDBHelper;
import jp.yutayamazaki.spanishwordtest.bean.WordType;
import jp.yutayamazaki.spanishwordtest.bean.WordTypeCollection;

@RunWith(RobolectricTestRunner.class)
public class WordTypeCollectionTest {
    @Test
    public void insert(){
        WordType wordType1 = new WordType("v", "動詞");
        WordType wordType2 = new WordType("adjective", "形容詞");
        WordTypeCollection wordTypeCollection =
                new WordTypeCollection(new BeanDBHelper(RuntimeEnvironment.application));

        wordTypeCollection.insertOrUpdate(wordType1);
        wordTypeCollection.insertOrUpdate(wordType2);

        List<WordType> all = wordTypeCollection.selectAll();

        Assert.assertEquals(2, all.size());
    }

    @Test
    public void update(){
        WordType wordType1 = new WordType("v", "どうし");
        WordType wordType2 = new WordType("v", "動詞");
        WordTypeCollection wordTypeCollection =
                new WordTypeCollection(new BeanDBHelper(RuntimeEnvironment.application));

        wordTypeCollection.insertOrUpdate(wordType1);
        wordTypeCollection.insertOrUpdate(wordType2);

        List<WordType> all = wordTypeCollection.selectAll();

        Assert.assertEquals(1, all.size());
        Assert.assertEquals("v", all.get(0).getWordTypeString());
        Assert.assertEquals("動詞", all.get(0).getWordTypeDisplay());
    }

    @Test
    public void deleteAll(){
        WordType wordType1 = new WordType("v", "動詞");
        WordType wordType2 = new WordType("adjective", "形容詞");
        WordTypeCollection wordTypeCollection =
                new WordTypeCollection(new BeanDBHelper(RuntimeEnvironment.application));

        wordTypeCollection.insertOrUpdate(wordType1);
        wordTypeCollection.insertOrUpdate(wordType2);

        List<WordType> all = wordTypeCollection.selectAll();

        Assert.assertEquals(2, all.size());

        wordTypeCollection.deleteAll();

        all = wordTypeCollection.selectAll();

        Assert.assertEquals(0, all.size());
    }
}
