package cn.tsinghua.tc.feature;

import cn.tsinghua.tc.cache.LabelCache;
import cn.tsinghua.tc.util.FeatureUtil;
import cn.tsinghua.tc.util.SelectKth;

import java.util.*;

/**
 * Created by shayan on 2016/6/4.
 */
public class MutualInformationFeatureSelector {

    private List<String> selectedFeatures;
    // 所有特征（单词）的数目表
    private Map<String, Integer> featureCounts;
    // 所有类（文档类型）数目表
    private Map<String, Integer> classCounts;

    // total number of documents
    private double docCount;

    //map which stores the counts of feature-class combinations. class feature
    //Map<List<Object>, Integer> featureClassCounts;

    // stores the counts of feature in class.
    private Map<String,Map<String, Integer>> classFeatureCounts;

    private Integer maxFeatures;

    public MutualInformationFeatureSelector(Map<String, Integer> featureCounts, Integer maxFeatures) {
        // init as original;
        this.selectedFeatures = Arrays.asList(featureCounts.keySet().toArray(new String[featureCounts.keySet().size()]));
        this.featureCounts = featureCounts;
        this.classCounts = LabelCache.getInstance().getLabelFileCount();
        this.classFeatureCounts = LabelCache.getInstance().getTermCountOnLabel();
        int totalDocs = 0;
        for(Map.Entry<String,Integer> docFile:LabelCache.getInstance().getLabelFileCount().entrySet()){
            totalDocs += docFile.getValue();
        }
        this.docCount = (double) totalDocs;
        this.maxFeatures = maxFeatures;

        filterFeatures();
    }

    public List<String> getSelectedFeatures() {
        return selectedFeatures;
    }

    public void setSelectedFeatures(List<String> selectedFeatures) {
        this.selectedFeatures = selectedFeatures;
    }

    public Map<String, Integer> getFeatureCounts() {
        return featureCounts;
    }

    public void setFeatureCounts(Map<String, Integer> featureCounts) {
        this.featureCounts = featureCounts;
    }

    public double getDocCount() {
        return docCount;
    }

    public void setDocCount(double docCount) {
        this.docCount = docCount;
    }

    public Integer getMaxFeatures() {
        return maxFeatures;
    }

    public void setMaxFeatures(Integer maxFeatures) {
        this.maxFeatures = maxFeatures;
    }

    public  void filterFeatures(){
        mutualInformationScore();
    }

    private void mutualInformationScore(  ){
        Map<String, Double> featureScores = new HashMap<String, Double>();

        double N = this.docCount;

        final double log2 = Math.log(2.0);

        for (Map.Entry<String, Integer> featureCount : featureCounts.entrySet()){
            String feature = featureCount.getKey();
            double N1_ = featureCount.getValue(); //calculate the N1. (number of records that has the feature)
            double N0_ = N - N1_; //also the N0. (number of records that DONT have the feature)

            double bestScore = Double.NEGATIVE_INFINITY; //REMEMBER! larger scores means more important feature.

            for(Map.Entry<String, Integer> classCount : classCounts.entrySet()) {
                String theClass = classCount.getKey();

                double N_1 = classCount.getValue();
                double N_0 = N - N_1;
                //Integer featureClassC = featureClassCounts.get(Arrays.<Object>asList(feature, theClass));
                Integer featureClassC = classFeatureCounts.get(theClass).get(feature);
                double N11 = (featureClassC!=null)?featureClassC.doubleValue():0.0; //N11 is the number of records that have the feature and belong on the specific class

                double N01 = N_1 - N11; //N01 is the total number of records that do not have the particular feature BUT they belong to the specific class

                double N00 = N0_ - N01;
                double N10 = N1_ - N11;

                //calculate Mutual Information
                //Note we calculate it partially because if one of the N.. is zero the log will not be defined and it will return NAN.
                double scorevalue=0.0;
                if(N11>0.0) {
                    scorevalue+=(N11/N)*Math.log((N/N1_)*(N11/N_1))/log2;
                }
                if(N01>0.0) {
                    scorevalue+=(N01/N)*Math.log((N/N0_)*(N01/N_1))/log2;
                }
                if(N10>0.0) {
                    scorevalue+=(N10/N)*Math.log((N/N1_)*(N10/N_0))/log2;
                }
                if(N00>0.0) {
                    scorevalue+=(N00/N)*Math.log((N/N0_)*(N00/N_0))/log2;
                }

                if(scorevalue>bestScore) {
                    bestScore = scorevalue;
                }
            }
            featureScores.put(feature, bestScore); //This Map is concurrent and there are no overlaping keys between threads
        }
        // remove low score feature.
        if(maxFeatures!=null && maxFeatures<featureScores.size()) {
            FeatureUtil.selectHighScoreFeatures(featureScores, maxFeatures);
        }

        this.selectedFeatures = Arrays.asList(featureScores.keySet().toArray(new String[featureScores.keySet().size()]));

    }





}
