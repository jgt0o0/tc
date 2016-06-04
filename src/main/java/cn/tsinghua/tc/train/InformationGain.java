package cn.tsinghua.tc.train;

import cn.tsinghua.tc.cache.FileFrequencyCache;
import cn.tsinghua.tc.cache.LabelCache;

import java.util.*;

/**
 * 信息增益计算
 * Created by ji on 16-6-4.
 */
public class InformationGain {
    public static Set<String> termSet = new HashSet<String>();

    public static Map<String, Double> gain = new HashMap<String, Double>();

    public void compute() {
        Set<String> labels = LabelCache.getInstance().getAllLabels();
        //遍历每个类标
        for (String label : labels) {
            Map<String, Double> termGain = new HashMap<String, Double>();

            List<String> filesInLabel = FileFrequencyCache.getInstance().getFilesInLabel(label);
            int labelFiles = filesInLabel.size();
            int fileCount = LabelCache.getInstance().getTrainFile().size();
            Double tmpE1 = (new Double(labelFiles)) / (new Double(fileCount));
            Double tmpE2 = (new Double(fileCount - labelFiles)) / (new Double(fileCount));

            //计算熵值
            double e = (-1) * (tmpE1 * Math.log(tmpE1) + tmpE2 * Math.log(tmpE2));

            /**
             * 开始计算信息增益
             */
            ArrayList<String> terms = LabelCache.getInstance().getLableTerms(label);
            if (terms != null && terms.size() > 0) {
                for (String term : terms) {
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
                    double D = fileCount - A - B - C;

                    double value0 = A + 1 == 0 ? 0 : (A / (A + B)) * Math.log(A / (A + B));
                    double value1 = B == 0 ? 0 : (B / (A + B)) * Math.log(B / (A + B));
                    double value2 = C == 0 ? 0 : (C / (C + D)) * Math.log(C / (C + D));
                    double value3 = D == 0 ? 0 : (D / (C + D)) * Math.log(D / (C + D));

                    double infoGain = e + ((A + B) / fileCount) * (value0 + value1) + ((C + D) / fileCount) * (value2 + value3);

                    termGain.put(term, infoGain);

                    if (gain.containsKey(term)) {
                        double tmpGain = gain.get(term);
                        if (infoGain > tmpGain) {
                            gain.put(term, infoGain);
                        }
                    } else {
                        gain.put(term, infoGain);
                    }

                }

                ValueComparator valueComparator = new ValueComparator(gain);
                TreeMap<String, Double> treeMap = new TreeMap<String, Double>(valueComparator);
                treeMap.putAll(gain);

                Map<String, Double> resultMap = new HashMap<String, Double>();
                int i = 0;
                for (Map.Entry<String, Double> entry : treeMap.entrySet()) {
                    i++;
                    if (i <= treeMap.size() * 1) {
                        resultMap.put(entry.getKey(), entry.getValue());
                        termSet.add(entry.getKey());
                    }
                }
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
                if (!gain.containsKey(termCount.getKey())) {
                    Integer count = termCountMap.get(termCount.getKey());
                    iterator.remove();
                    LabelCache.getInstance().minusTermCount(label, count);
                }
            }
        }
    }
}


class ValueComparator implements Comparator<String> {

    Map<String, Double> base;

    public ValueComparator(Map<String, Double> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}

