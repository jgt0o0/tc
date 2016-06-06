package cn.tsinghua.tc.train;

import cn.tsinghua.tc.cache.LabelCache;
import cn.tsinghua.tc.cache.StopWordCache;
import cn.tsinghua.tc.util.PorterStemmer;
import cn.tsinghua.tc.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 训练数据集文章读取分析
 * Created by ji on 16-5-25.
 */
public class TrainDocReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainDocReader.class);

    private AtomicInteger fileCount = new AtomicInteger();

    private ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(5);

    private CountDownLatch countDownLatch;

    public void readDoc() throws Exception {
        try {
            Map<String, String> trainFileLabel = LabelCache.getInstance().getTrainFile();

            if (trainFileLabel != null && trainFileLabel.size() > 0) {
                countDownLatch = new CountDownLatch(trainFileLabel.size());
                String fileDir = PropertyUtil.getInstance().TRAIN_DOC_DIR;
                for (Map.Entry<String, String> fileLabel : trainFileLabel.entrySet()) {
                    String fileName = fileDir + "/" + fileLabel.getKey();
                    File trainFile = new File(fileName);
                    threadPoolExecutor.execute(new FileHandler(trainFile));
                }

                countDownLatch.await();
            }
        } catch (Exception e) {
            LOGGER.error("读取训练文件数据异常", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 文件处理线程
     */
    class FileHandler implements Runnable {
        File file;
        int wordCount = 0;

        public FileHandler(File file) {
            this.file = file;
        }

        public void run() {
            if (!file.exists()) {
                LOGGER.warn(file.getAbsolutePath() + "不存在");
                LabelCache.getInstance().removeFile(file.getName());
                return;
            }

            String label = LabelCache.getInstance().getFileLabel(file.getName());

            int value = fileCount.incrementAndGet();
            LOGGER.info("读取第{}个文件:{}", value, file.getName());

            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    this.handleLine(line, label);
                }
                //记录每个类标中的词总数
                LabelCache.getInstance().addLableWordCount(label, wordCount);
                //记录总词数
                LabelCache.getInstance().addWordTotalCount(wordCount);
            } catch (Exception e) {
                LOGGER.error("读取文件异常:{}", file.getAbsolutePath(), e);
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        LOGGER.error("文件流关闭异常", e);
                    }
                }
                countDownLatch.countDown();
            }
        }

        private void handleLine(String line, String label) {
            String res[] = line.split("[^a-zA-Z]");
            if (res.length > 0) {
                PorterStemmer porterStemmer = new PorterStemmer();
                for (int i = 0; i < res.length; i++) {
                    if (("".equals(res[i].trim())) || StopWordCache.contains(res[i].toLowerCase())) {
//                        LOGGER.info("跳过单词:{}", res[i]);
                    } else {
                        wordCount++;
                        char[] charArray = res[i].toLowerCase().toCharArray();
                        porterStemmer.add(charArray, charArray.length);
                        porterStemmer.stem();
                        String str = porterStemmer.toString();
                        //记录这个词在类标中的出现次数
                        LabelCache.getInstance().addTermCountToLabel(label, str);
                    }
                }
            }
        }
    }
}
