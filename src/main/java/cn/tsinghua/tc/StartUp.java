package cn.tsinghua.tc;

import cn.tsinghua.tc.cache.FileFrequencyCache;
import cn.tsinghua.tc.cache.LabelCache;
import cn.tsinghua.tc.test.AccuracyCompute;
import cn.tsinghua.tc.test.Classify;
import cn.tsinghua.tc.test.WriteResultToFile;
import cn.tsinghua.tc.train.*;
import cn.tsinghua.tc.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by ji on 16-5-25.
 */
public class StartUp {
    private static final Logger LOGGER = LoggerFactory.getLogger(StartUp.class);

    private static final int INFORMATION_GAIN_TYPE = 1;

    private static final int CHI_SQUARE=2;

    public static void main(String[] arg) {
        try {
            arg = new String[]{"train"};
            PropertyUtil.getInstance();
            String type = arg[0];
            if (type.equals("train")) {
                //1.读取类标数据
                TrainLabelReader trainLabelReader = new TrainLabelReader();
                trainLabelReader.readLabel();

                //2. 读取stopWords
                StopWordsReader stopWordsReader = new StopWordsReader();
                stopWordsReader.readStopWords();

                //3.读取train文件数据
                TrainDocReader docReader = new TrainDocReader();
                docReader.readDoc();

                LOGGER.info("训练数据文件读取完成");

                //特征提取
                optimize(CHI_SQUARE);

                LOGGER.info("开始将训练结果数据写入文件");
                WriteTrainDataToFile writeTrainDataToFile = new WriteTrainDataToFile();
                writeTrainDataToFile.write();
                LOGGER.info("训练结果数据写入文件完成");

                LOGGER.info("开始测试");
                Classify classify = new Classify();
                long start = System.currentTimeMillis();
                Map<String, String> result = classify.start();
                long end = System.currentTimeMillis();
                LOGGER.info("耗时:{}", end - start);
                LOGGER.info("测试完成");

                LOGGER.info("分类结果写入文件");
                WriteResultToFile writeResultToFile = new WriteResultToFile();
                writeResultToFile.write(result);
                LOGGER.info("分类结果写入文件完成");

                LOGGER.info("开始统计准确率");
                AccuracyCompute accuracyCompute = new AccuracyCompute();
                Double accuracy = accuracyCompute.compute(result);
                LOGGER.info("准确率统计完成");
                LOGGER.warn("准确率为{}", accuracy);


                LabelCache.getInstance().getL();
                FileFrequencyCache.getInstance().getFilesContainsTerm("a");
            } else if (type.equals("test")) {

            } else {

            }
            System.exit(0);
        } catch (Exception e) {
            LOGGER.error("处理异常,系统退出", e);
            System.exit(-1);
        }

    }


    private static void optimize(int type) {

        if (type == INFORMATION_GAIN_TYPE) {
            InformationGain informationGain = new InformationGain();
            informationGain.compute();
            informationGain.filterData();
        }else if (type == CHI_SQUARE) {
            ChiSquare chiSquare = new ChiSquare();
            chiSquare.compute();
            chiSquare.filterData();
        }
    }
}
