package cn.tsinghua.tc.test;

import cn.tsinghua.tc.cache.LabelCache;
import cn.tsinghua.tc.cache.StopWordCache;
import cn.tsinghua.tc.util.PorterStemmer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ji on 16-5-30.
 */
public class Classify {
    private static final Logger LOGGER = LoggerFactory.getLogger(Classify.class);

    public void classify(String file) {
        Map<String, Integer> termCount = new HashMap<String, Integer>();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(file))));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                this.handleLine(line, termCount);
            }
        } catch (Exception e) {
            LOGGER.error("分类文件失败", e);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    LOGGER.error("关闭文件失败", e);
                }
            }
        }

        String result = this.calProb(termCount);
        LOGGER.info("[{}]属于分类:{}", file,result);
    }

    private void handleLine(String line, Map<String, Integer> termCount) {
        String res[] = line.split("[^a-zA-Z]");
        if (res.length > 0) {
            PorterStemmer porterStemmer = new PorterStemmer();
            for (int i = 0; i < res.length; i++) {
                if (("".equals(res[i].trim())) || StopWordCache.contains(res[i].toLowerCase())) {
//                    LOGGER.info("跳过单词:{}", res[i]);
                } else {
                    char[] charArray = res[i].toLowerCase().toCharArray();
                    porterStemmer.add(charArray, charArray.length);
                    porterStemmer.stem();
                    String str = porterStemmer.toString();
                    if (termCount.containsKey(str)) {
                        Integer count = termCount.get(str);
                        termCount.put(str, count.intValue() + 1);
                    } else {
                        termCount.put(str, 1);
                    }
                }
            }
        }
    }

    private String calProb(Map<String, Integer> testTermMap) {
        BigDecimal maxP = null;
        String resultCate = null;
        for (String label : LabelCache.getInstance().getLabels()) {
            BigDecimal p = new BigDecimal(1);
            for (Map.Entry<String, Integer> entry : testTermMap.entrySet()) {
                BigDecimal testVal = new BigDecimal(entry.getValue().intValue() + 1);
                BigDecimal tmpProb = (testVal).divide(new BigDecimal(LabelCache.getInstance().getTotalWordCount()).add(new BigDecimal(LabelCache.getInstance().getWordCountInLabel(label))), 10, BigDecimal.ROUND_CEILING);
                p = p.multiply(tmpProb);
            }
            BigDecimal tmpProb = p.multiply(new BigDecimal(LabelCache.getInstance().getWordCountInLabel(label)).divide(new BigDecimal(LabelCache.getInstance().getTotalWordCount()), 10, BigDecimal.ROUND_CEILING));

            if (maxP == null) {
                maxP = tmpProb;
                resultCate = label;
            } else if (tmpProb.compareTo(maxP) == 1) {
                maxP = tmpProb;
                resultCate = label;
            }
        }
        return resultCate;
    }

}
