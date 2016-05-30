package cn.tsinghua.tc.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by ji on 16-5-25.
 */
public class PropertyUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyUtil.class);

    private static final PropertyUtil _INSTANCE = new PropertyUtil();

    //训练数据根目录
    public String TRAIN_DIR;
    //训练文件目录
    public String TRAIN_DOC_DIR;
    //训练数据以及类标文件
    public String TRAIN_LABEL_FILE;

    //训练结果输出目录
    public String TRAIN_OUT_DIR;
    //每个类标下文件数目
    public String LABEL_FILE_COUNT_FILE;
    //每个单词在各类标中出现的次数
    public String LABEL_WORD_PER_COUNT;
    //每个类标下的总词数
    public String LABEL_WORD_COUNT;

    private PropertyUtil() {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = this.getClass().getClassLoader().getResourceAsStream("train.properties");
            properties.load(inputStream);

            TRAIN_DIR = properties.getProperty("train.dir");

            TRAIN_DOC_DIR = TRAIN_DIR + "/" + properties.getProperty("train.doc.dir");

            TRAIN_LABEL_FILE = TRAIN_DIR + "/" + properties.getProperty("train.label.file");

            TRAIN_OUT_DIR = properties.getProperty("train.out.dir");

            LABEL_FILE_COUNT_FILE = TRAIN_OUT_DIR + "/" + properties.getProperty("train.out.label.fileCount.file");

            LABEL_WORD_PER_COUNT = TRAIN_OUT_DIR + "/" + properties.getProperty("train.out.label.wordCount.per.file");

            LABEL_WORD_COUNT = TRAIN_OUT_DIR + "/" + properties.getProperty("train.out.label.wordCount.file");
        } catch (Exception e) {
            LOGGER.error("读取配置异常", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LOGGER.error("关闭properties文件流失败");
                }
            }
        }
    }

    public static PropertyUtil getInstance() {
        return _INSTANCE;
    }
}
