package cn.tsinghua.tc.cache;

import javax.print.Doc;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by shayan on 2016/6/5.
 *
 */
public class DocCache {

    //map which stores the counts of feature-class combinations. class feature
    private final Map<List<String>, Integer> featureClassCounts = new ConcurrentHashMap<List<String>, Integer>();

    // 出现某个词的文档数量表 (feature ， docCount)
    private final Map<String, Integer> featureDocCounts = new ConcurrentHashMap<String, Integer>();

    private static final DocCache _INSTANCE = new DocCache();

    private DocCache() {
    }

    public static DocCache getInstance() {
        return _INSTANCE;
    }


    public Map<List<String>, Integer> getFeatureClassCounts() {
        return featureClassCounts;
    }

    public Map<String, Integer> getFeatureDocCounts() {
        return featureDocCounts;
    }

    /**
     * 统计含有 feature-label 的文档数
     *
     * @param feature
     * @param label
     */
    public void addFeatureClassCount(String feature, String label) {
        synchronized (featureClassCounts) {
            List<String> key = Arrays.<String>asList(feature, label);
            if (featureClassCounts.containsKey(key)) {
                Integer count = featureClassCounts.get(key);
                featureClassCounts.put(key, ++count);
            } else {
                featureClassCounts.put(key, 1);
            }
        }
    }

    public void addFeatureDocCount(String feature){
        synchronized (featureDocCounts){
            if(featureDocCounts.containsKey(feature)){
                Integer count = featureDocCounts.get(feature);
                featureDocCounts.put(feature, ++count);
            }else{
                featureDocCounts.put(feature, 1);
            }
        }
    }
}
