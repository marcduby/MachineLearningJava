package com.doobs.mlbeta.service.dl4j;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.io.ClassPathResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SimpleNetworkService {

    @Autowired
    private ResourceLoader resourceLoader;

    public void runPetalExample() throws IOException, InterruptedException {
        // dataset variables
        int labelIndex = 4;
        int numberOfClasses = 3;
        int trainingBatchSize = 150;
        double testSplit = 0.8;

        // get the iris file
        String irisFileName = "classpath:iris.txt";

        // read the records
        RecordReader recordReader = new CSVRecordReader(0, ",");
        recordReader.initialize(new FileSplit(resourceLoader.getResource(irisFileName).getFile()));

        // iterate the data
        DataSetIterator dataSetIterator = new RecordReaderDataSetIterator(recordReader, trainingBatchSize, labelIndex, numberOfClasses);
        DataSet dataSet = dataSetIterator.next();
        dataSet.shuffle();
        SplitTestAndTrain splitTestAndTrain = dataSet.splitTestAndTrain(testSplit);

        // get the training and test data
        DataSet trainingData = splitTestAndTrain.getTrain();
        DataSet testData = splitTestAndTrain.getTest();

        // normalize the data
        DataNormalization normalization = new NormalizerStandardize();
        normalization.fit(trainingData);
        normalization.transform(trainingData);
        normalization.transform(testData);



    }
}
