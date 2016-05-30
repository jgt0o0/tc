package cn.tsinghua.tc.train;

import cn.tsinghua.tc.cache.LabelCache;
import cn.tsinghua.tc.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * Created by ji on 16-5-30.
 */
public class WriteTrainDataToFile {
    private static final Logger LOGGER = LoggerFactory.getLogger(WriteTrainDataToFile.class);

    public void write() throws IOException {
        File outDir = new File(PropertyUtil.getInstance().TRAIN_OUT_DIR);
        outDir.deleteOnExit();
        if (!outDir.exists()) {
            outDir.mkdir();
        }

        this.writeLabelFileCount();

        this.writeLabelWordCountPer();

        this.writeLabelWordCount();

    }

    private void writeLabelWordCount() {

        FileWriter targetFileWriter = null;
        try {
            File labelWordCountPerFile = new File(PropertyUtil.getInstance().LABEL_WORD_COUNT);
            targetFileWriter = new FileWriter(labelWordCountPerFile);
            Map<String, Integer> map = LabelCache.getInstance().getLabelTermCount();
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                String line = entry.getKey() + " " + entry.getValue() + "\n";
                targetFileWriter.write(line);
            }
            targetFileWriter.flush();
            targetFileWriter.flush();
        } catch (Exception e) {
            LOGGER.error("将单词在类标中出现次数写入文件失败", e);
            throw new RuntimeException("将单词在类标中出现次数写入文件失败", e);
        } finally {
            if (targetFileWriter != null) {
                try {
                    targetFileWriter.close();
                } catch (IOException e) {
                    LOGGER.error("关闭文件失败", e);
                }
            }
        }
    }

    private void writeLabelWordCountPer() {

        FileWriter targetFileWriter = null;
        try {
            File labelWordCountPerFile = new File(PropertyUtil.getInstance().LABEL_WORD_PER_COUNT);
            targetFileWriter = new FileWriter(labelWordCountPerFile);
            Map<String, Map<String, Integer>> map = LabelCache.getInstance().getTermCountOnLabel();
            for (Map.Entry<String, Map<String, Integer>> entry : map.entrySet()) {
                for (Map.Entry<String, Integer> e1 : entry.getValue().entrySet()) {
                    String line = entry.getKey() + " " + e1.getKey() + " " + e1.getValue() + "\n";
                    targetFileWriter.write(line);
                }
            }
            targetFileWriter.flush();
        } catch (Exception e) {
            LOGGER.error("将单词在类标中出现次数写入文件失败", e);
            throw new RuntimeException("将单词在类标中出现次数写入文件失败", e);
        } finally {
            if (targetFileWriter != null) {
                try {
                    targetFileWriter.close();
                } catch (IOException e) {
                    LOGGER.error("关闭文件失败", e);
                }
            }
        }
    }

    private void writeLabelFileCount() {
        FileWriter targetFileWriter = null;
        try {
            File labelFileCountFile = new File(PropertyUtil.getInstance().LABEL_FILE_COUNT_FILE);
            targetFileWriter = new FileWriter(labelFileCountFile);
            Map<String, Integer> map = LabelCache.getInstance().getLabelFileCount();
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                String line = entry.getKey() + " " + entry.getValue() + "\n";
                targetFileWriter.write(line);
            }
            targetFileWriter.flush();
        } catch (Exception e) {
            LOGGER.error("写入类标对应文件数到文件失败", e);
            throw new RuntimeException("写入类标对应文件数到文件失败", e);
        } finally {
            if (targetFileWriter != null) {
                try {
                    targetFileWriter.close();
                } catch (IOException e) {
                    LOGGER.error("关闭文件失败", e);
                }
            }
        }
    }
}
