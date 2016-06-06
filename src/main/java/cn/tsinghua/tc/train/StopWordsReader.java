package cn.tsinghua.tc.train;

import cn.tsinghua.tc.cache.StopWordCache;
import cn.tsinghua.tc.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * 读取stopWord到内存
 * Created by ji on 16-5-30.
 */
public class StopWordsReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(StopWordsReader.class);

    public void readStopWords() throws Exception {
        LOGGER.info("开始读取stopWord");
        String stopWordFile = PropertyUtil.getInstance().TRAIN_DIR + "/stopword.txt";
        File file = new File(stopWordFile);
        if (!file.exists()) {
            throw new RuntimeException(stopWordFile + "不存在");
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            while ((line = reader.readLine()) != null) {
                StopWordCache.addStopWord(line.trim());
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        LOGGER.info("StopWord读取完成");
    }
}
