package cn.tsinghua.tc.train;

import cn.tsinghua.tc.cache.FileFrequencyCache;
import cn.tsinghua.tc.cache.LabelCache;

import java.util.List;
import java.util.Set;

/**
 * 卡方校验计算
 * Created by ji on 16-6-4.
 */
public class ChiSquare {

    public void compute() {
        Set<String> labels = LabelCache.getInstance().getAllLabels();
        //遍历每个类标
        for (String label : labels) {
            //todo 计算类标的熵
            List<String> files = FileFrequencyCache.getInstance().getFilesInLabel(label);
            int labelFiles = files.size();
            int fileCount = LabelCache.getInstance().getTrainFile().size();
            Double tmpE1 = (new Double(labelFiles)) / (new Double(fileCount));
            Double tmpE2 = (new Double(fileCount - labelFiles)) / (new Double(fileCount));

            //计算熵值
            double e = (-1) * (tmpE1 * Math.log(tmpE1) + tmpE2 + Math.log(tmpE2));

            
        }

    }
}
