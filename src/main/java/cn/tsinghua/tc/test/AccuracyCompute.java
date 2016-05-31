package cn.tsinghua.tc.test;

import cn.tsinghua.tc.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ji on 16-5-31.
 */
public class AccuracyCompute {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccuracyCompute.class);

    public Double compute(Map<String, String> result) {
        int right = 0;
        int wrong = 0;

        Map<String, String> labelMap = this.readLabelFromFile();
        for (Map.Entry<String, String> entry : result.entrySet()) {
            String label = labelMap.get(entry.getKey());
            if (entry.getValue().equals(label)) {
                right++;
            } else {
                wrong++;
            }
        }
        Double accuracy = new Double(right) / new Double(right + wrong);
        return accuracy;
    }

    private Map<String, String> readLabelFromFile() {
        File file = new File(PropertyUtil.getInstance().TEST_LABEL_FILE);
        if (!file.exists()) {
            throw new RuntimeException("测试类标数据不存在");
        }

        Map<String, String> result = new HashMap<String, String>();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] tmpLabel = line.split("\\t");
                if (tmpLabel.length != 2) {
                    continue;
                }
                result.put(tmpLabel[0], tmpLabel[1]);
            }
        } catch (Exception e) {
            LOGGER.error("读取测试类标数据异常", e);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    LOGGER.error("文件流关闭异常", e);
                }
            }
        }
        return result;
    }
}
