package cn.tsinghua.tc.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ji on 16-5-30.
 */
public class LabelCache {
    //每个文件对应的类标
    private final Map<String, String> fileLabelMap = new HashMap<String, String>();

    //每个类标对应的文件数量
    private final Map<String, Integer> labelFileCount = new ConcurrentHashMap<String, Integer>();

    //每个类标对应的单词总数
    private final Map<String, Integer> labelTermCount = new ConcurrentHashMap<String, Integer>();

    //每个单词在类标中的总数,类标,词,词数
    private final Map<String, Map<String, Integer>> termCountOnLabel = new ConcurrentHashMap<String, Map<String, Integer>>();

    //记录总词数
    private volatile Integer totalWordCount = 0;

    private static final LabelCache _INSTANCE = new LabelCache();

    private LabelCache() {
    }

    public static LabelCache getInstance() {
        return _INSTANCE;
    }

    /**
     * 将训练数据文件和对应的类标存储到本地
     *
     * @param file
     * @param label
     */
    public void addFileLabel(String file, String label) {
        fileLabelMap.put(file, label);
        Integer fileCount = labelFileCount.get(label);
        if (fileCount == null) {
            fileCount = 0;
        }
        labelFileCount.put(label, fileCount.intValue() + 1);
    }

    public void removeFile(String fileName) {
        String label = fileLabelMap.get(fileName);
        if (label != null) {
            fileLabelMap.remove(fileName);
            if (labelFileCount.containsKey(label)) {
                Integer count = labelFileCount.get(label);
                labelFileCount.put(label, count.intValue() - 1);
            }
        }
    }

    public Map<String, String> getTrainFile() {
        return new HashMap<String, String>(fileLabelMap);
    }

    public String getFileLabel(String file) {
        return fileLabelMap.get(file);
    }


    public void addTermCountToLabel(String label, String term) {
        synchronized (termCountOnLabel) {
            if (termCountOnLabel.containsKey(label)) {
                Map<String, Integer> value = termCountOnLabel.get(label);
                if (value.containsKey(term)) {
                    Integer count = value.get(term);
                    value.put(term, count.intValue() + 1);
                } else {
                    value.put(term, 1);
                }
                termCountOnLabel.put(label, value);
            } else {
                Map<String, Integer> value = new HashMap<String, Integer>();
                value.put(label, 1);
                termCountOnLabel.put(label, value);
            }
        }
    }

    public int getCountInLabelByTerm(String label, String term) {
        int result = 0;
        if (termCountOnLabel.containsKey(label)) {
            Integer value = termCountOnLabel.get(label).get(term);
            if (value != null) {
                result = value.intValue();
            }
        }
        return result;
    }

    public void addLableWordCount(String label, int count) {
        synchronized (labelTermCount) {
            if (labelTermCount.containsKey(label)) {
                Integer value = labelTermCount.get(label);
                value = value.intValue() + count;
                labelTermCount.put(label, value);
            } else {
                labelTermCount.put(label, count);
            }
        }
    }

    public void getL() {
        System.out.println(termCountOnLabel);
    }

    public void addWordTotalCount(int count) {
        synchronized (totalWordCount) {
            totalWordCount += count;
        }
    }


    public Integer getTotalWordCount() {
        return totalWordCount;
    }

    public Integer getWordCountInLabel(String label) {
        return labelTermCount.get(label);
    }

    public Map<String,Integer> getLabelFileCount() {
        return labelFileCount;
    }

    public  Map<String, Map<String, Integer>> getTermCountOnLabel() {
        return termCountOnLabel;
    }

    public Map<String,Integer> getLabelTermCount() {
        return labelTermCount;
    }

    public List<String> getLabels() {
        return new ArrayList<String>(labelFileCount.keySet());
    }
}
