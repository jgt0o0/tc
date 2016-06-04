package cn.tsinghua.tc.cache;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ji on 16-6-4.
 */
public class FileFrequencyCache {
    private static final FileFrequencyCache _INSTANCE = new FileFrequencyCache();

    //每个次在哪些文档中出现
    private final Map<String, Set<String>> termFiles = new ConcurrentHashMap<String, Set<String>>();

    //每个类标下的文档列表
    private final Map<String, Set<String>> labelFiles = new ConcurrentHashMap<String, Set<String>>();

    private FileFrequencyCache() {
    }

    public static FileFrequencyCache getInstance() {
        return _INSTANCE;
    }

    public void addFileContainsTerm(String term, String file) {
        synchronized (termFiles) {
            Set<String> files = termFiles.get(term);
            if (files == null) {
                files = new HashSet<String>();
            }
            files.add(file);
            termFiles.put(term, files);
        }
    }

    public void addFileInLabel(String label, String file) {
        synchronized (labelFiles) {
            Set<String> files = labelFiles.get(label);
            if (files == null) {
                files = new HashSet<String>();
            }
            files.add(file);
            labelFiles.put(label, files);
        }
    }


    public List<String> getFilesInLabel(String label) {
        ArrayList result = new ArrayList<String>();
        if (labelFiles.containsKey(label)) {
            result = new ArrayList(labelFiles.get(label));
        }
        return result;
    }

    public List<String> getFilesContainsTerm(String term) {
        ArrayList result = new ArrayList<String>();
        if (termFiles.containsKey(term)) {
            result = new ArrayList(termFiles.get(term));
        }
        return result;
    }
}
