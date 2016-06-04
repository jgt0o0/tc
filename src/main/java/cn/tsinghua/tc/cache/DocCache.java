package cn.tsinghua.tc.cache;

import javax.print.Doc;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by shayan on 2016/6/5.
 */
public class DocCache {
    //每个类标对应的文件数量
    private final Map<String, Integer> labelFileCount = new ConcurrentHashMap<String, Integer>();

    //map which stores the counts of feature-class combinations. class feature
    private final Map<List<String>, Integer> featureClassCounts = new ConcurrentHashMap<List<String>, Integer>();

    // 出现某个词的文档数量表 (feature ， docCount)
    private final Map<String, Integer> featureDocCounts = new HashMap<String, Integer>();

    private static final DocCache _INSTANCE = new DocCache();

    private DocCache() {
    }

    public static DocCache getInstance() {
        return _INSTANCE;
    }

    public Map<String, Integer> getLabelFileCount() {
        return labelFileCount;
    }

    public Map<List<String>, Integer> getFeatureClassCounts() {
        return featureClassCounts;
    }

    public Map<String, Integer> getFeatureDocCounts() {
        return featureDocCounts;
    }
}
