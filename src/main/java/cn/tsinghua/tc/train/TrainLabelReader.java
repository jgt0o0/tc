package cn.tsinghua.tc.train;

import cn.tsinghua.tc.cache.LabelCache;
import cn.tsinghua.tc.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * Created by ji on 16-5-25.
 */
public class TrainLabelReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainLabelReader.class);

    public void readLabel() throws Exception {
        LOGGER.info("开始read label 文件");
        InputStreamReader reader = null;
        BufferedReader bufferedReader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(new File(PropertyUtil.getInstance().TRAIN_LABEL_FILE)));
            bufferedReader = new BufferedReader(reader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] tmpLabel = line.split("\\t");
                if (tmpLabel.length != 2) {
                    continue;
                }

                String fileName = tmpLabel[0].trim();
                String fileLabel = tmpLabel[1].trim();

                LabelCache.getInstance().addFileLabel(fileName, fileLabel);
            }
        } catch (Exception e) {
            LOGGER.error("读取训练类标数据异常", e);
            throw new RuntimeException(e);
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
        LOGGER.info("read label 文件结束");
    }
}
