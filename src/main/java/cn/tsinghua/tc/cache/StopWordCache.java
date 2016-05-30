package cn.tsinghua.tc.cache;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ji on 16-5-30.
 */
public class StopWordCache {
    private static final List<String> stopWordList = new ArrayList<String>();

    public static void addStopWord(String word) {
        stopWordList.add(word);
    }

    public static boolean contains(String word) {
        return stopWordList.contains(word);
    }
}
