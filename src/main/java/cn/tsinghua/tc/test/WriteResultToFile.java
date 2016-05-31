package cn.tsinghua.tc.test;

import cn.tsinghua.tc.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * Created by ji on 16-5-31.
 */
public class WriteResultToFile {
    private static final Logger LOGGER = LoggerFactory.getLogger(WriteResultToFile.class);

    public void write(Map<String, String> paramMap) {
        File file = new File(PropertyUtil.getInstance().TEST_LABEL_OUT_FILE);
        if (file.exists()) {
            file.delete();
        }

        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                writer.write(entry.getKey() + "\t" + entry.getValue() + "\n");
            }
            writer.flush();
        } catch (Exception e) {
            LOGGER.error("分类结果写入文件失败", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    LOGGER.error("文件流关闭异常", e);
                }
            }
        }
    }
}
