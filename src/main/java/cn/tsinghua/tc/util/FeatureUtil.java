package cn.tsinghua.tc.util;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by shayan on 2016/6/5.
 */
public class FeatureUtil {

    public static void selectHighScoreFeatures(Map<String, Double> featureScores, Integer maxFeatures) {
//        Logger logger = LoggerFactory.getLogger(AbstractScoreBasedFeatureSelector.class);
//        logger.debug("selectHighScoreFeatures()");

//        logger.debug("Estimating the minPermittedScore");
        Double minPermittedScore = SelectKth.largest(featureScores.values().iterator(), maxFeatures);

        //remove any entry with score less than the minimum permitted one
//        logger.debug("Removing features with scores less than threshold");
        Iterator<Map.Entry<String, Double>> it = featureScores.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<String, Double> entry = it.next();
            if(entry.getValue()<minPermittedScore) {
                it.remove();
            }
        }

        //if some extra features still exist (due to ties on the scores) remove some of those extra features
        int numOfExtraFeatures = featureScores.size()-maxFeatures;
        if(numOfExtraFeatures>0) {
//            logger.debug("Removing extra features caused by ties");
            it = featureScores.entrySet().iterator();
            while(it.hasNext() && numOfExtraFeatures>0) {
                Map.Entry<String, Double> entry = it.next();
                if(entry.getValue()-minPermittedScore<=0.0) { //DO NOT COMPARE THEM DIRECTLY USE SUBTRACTION!
                    it.remove();
                    --numOfExtraFeatures;
                }
            }
        }
    }
}
