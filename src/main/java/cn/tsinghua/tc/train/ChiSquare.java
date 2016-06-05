package cn.tsinghua.tc.train;

import cn.tsinghua.tc.cache.FileFrequencyCache;
import cn.tsinghua.tc.cache.FutureCache;
import cn.tsinghua.tc.cache.LabelCache;

import java.util.*;

/**
 * 卡方校验计算
 * Created by ji on 16-6-4.
 */
public class ChiSquare {

    public static final Map<String, Double> termChiMap = new HashMap<String, Double>();

    public void compute() {

        //文档总数:N
        int N = LabelCache.getInstance().getTrainFile().size();

        Set<String> labels = LabelCache.getInstance().getAllLabels();
        //遍历每个类标
        for (String label : labels) {
            //属于该label的所有文档
            List<String> filesInLabel = FileFrequencyCache.getInstance().getFilesInLabel(label);

            //该类标下的所有词
            ArrayList<String> terms = LabelCache.getInstance().getLableTerms(label);
            if (terms != null && terms.size() > 0) {
                for (String term : terms) {
                    //包含该词的所有文档
                    List<String> filesContainsTrem = FileFrequencyCache.getInstance().getFilesContainsTerm(term);
                    double A = 0;
                    double B = 0;
                    //1 计算包含term,且属于该label的文档数量
                    for (String file : filesContainsTrem) {
                        if (filesInLabel.contains(file)) {
                            A++;
                        } else {
                            //2 计算包含term,不属于该label的文档数量
                            B++;
                        }
                    }


                    //3 计算不包含term,属于该label的文档数量
                    int C = 0;
                    for (String file : filesInLabel) {
                        //如果属于该label,但是不包含该term
                        if (!filesContainsTrem.contains(file)) {
                            C++;
                        }
                    }

                    //4 计算不包含term,且不属于该label的文档数量
                    double D = N - A - B - C;

                    double temp = (A * D - B * C);
                    double chi = N * (temp * temp) / ((A + C) * (A + B) * (B + D) * (C + D));

                    if (termChiMap.containsKey(chi)) {
                        double tmpChi = termChiMap.get(term);
                        if (chi > tmpChi) {
                            termChiMap.put(term, chi);
                        }
                    } else {
                        termChiMap.put(term, chi);
                    }
                }
            }

        }

        this.sort();

    }

    private void sort() {
        ValueComparator valueComparator = new ValueComparator(termChiMap);
        TreeMap<String, Double> treeMap = new TreeMap<String, Double>(valueComparator);
        treeMap.putAll(termChiMap);

        int i = 0;
        for (Map.Entry<String, Double> entry : treeMap.entrySet()) {
            i++;
            if (i <= treeMap.size() * 1) {
                FutureCache.addFuture(entry.getKey());
            }
        }
    }

    public void filterData() {
        Map<String, Map<String, Integer>> termCountLabel = LabelCache.getInstance().getTermCountOnLabel();
        for (Map.Entry<String, Map<String, Integer>> entry : termCountLabel.entrySet()) {
            String label = entry.getKey();
            Map<String, Integer> termCountMap = entry.getValue();
            Iterator<Map.Entry<String, Integer>> iterator = termCountMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Integer> termCount = iterator.next();
                if (!termChiMap.containsKey(termCount.getKey())) {
                    Integer count = termCountMap.get(termCount.getKey());
                    iterator.remove();
                    LabelCache.getInstance().minusTermCount(label, count);
                }
            }
        }
    }
}
