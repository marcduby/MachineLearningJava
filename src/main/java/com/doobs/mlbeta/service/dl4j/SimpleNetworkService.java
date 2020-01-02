package com.doobs.mlbeta.service.dl4j;

import com.doobs.mlbeta.model.ModelBuilder;
import com.doobs.mlbeta.util.FileConverter;
import com.doobs.mlbeta.util.MlException;
import org.datavec.api.io.converters.LabelWriterConverter;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.schema.Schema;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.io.ClassPathResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

@Service
public class SimpleNetworkService {
    // instance variables
    private final Logger serviceLog = Logger.getLogger(this.getClass().getName());

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    FileConverter fileConverter;

    @Autowired
    ModelBuilder modelBuilder;

    public void runPetalExample() throws MlException {
        // dataset variables
        int labelIndex = 4;
        int numberOfClasses = 3;
        int trainingBatchSize = 150;
        double testSplit = 0.8;

        // get the iris file
        String irisFileName = "classpath:data/simple/iris/iris.data";
        String irisTransformedFileName = "classpath:data/simple/iris/iris_trans.csv";

        // create the label converter to convert from the string labels to integers
        // TODO - figure out or build way to pull the distinct labels from the record reader
//        String[] labelArray = {"Iris-setosa", "Iris-virginica", "Iris-versicolor"};
//        LabelWriterConverter labelWriterConverter = new LabelWriterConverter(new ArrayList<String>(Arrays.asList(labelArray)));

        // build a schema to load and transform the data
        Schema schema = new Schema.Builder()
                .addColumnsDouble("slength", "swidth", "plength", "pwidth")
                .addColumnCategorical("class", "Iris-setosa", "Iris-virginica", "Iris-versicolor")
                .build();
        TransformProcess transformProcess = new TransformProcess.Builder(schema)
                .categoricalToInteger("class")
                .build();

        // log
        this.serviceLog.info("in Iris data example");

        try {
            // read the records
            RecordReader recordReader = new CSVRecordReader(0, ",");
            File irisFile = resourceLoader.getResource(irisFileName).getFile();
            recordReader.initialize(new FileSplit(irisFile));
            this.serviceLog.info("Read file: " + (irisFile == null ? null : irisFile.getPath()));

            // create a transformed file
            File transformedFile = fileConverter.transformFile(irisFile, irisTransformedFileName, transformProcess, 0, ",");
            RecordReader transformedRecordReader = new CSVRecordReader(0, ",");
            transformedRecordReader.initialize(new FileSplit(transformedFile));

            // iterate the data
            DataSetIterator dataSetIterator = new RecordReaderDataSetIterator(transformedRecordReader, trainingBatchSize, labelIndex, numberOfClasses);
            DataSet dataSet = dataSetIterator.next();
            dataSet.shuffle();
            SplitTestAndTrain splitTestAndTrain = dataSet.splitTestAndTrain(testSplit);

            // get the training and test data
            DataSet trainingData = splitTestAndTrain.getTrain();
            DataSet testData = splitTestAndTrain.getTest();
            this.serviceLog.info("split the iris test/train data");

            // normalize the data
            DataNormalization normalization = new NormalizerStandardize();
            normalization.fit(trainingData);
            normalization.transform(trainingData);
            normalization.transform(testData);
            this.serviceLog.info("normalized the iris test/train data");

            // get the model
            MultiLayerNetwork model = this.modelBuilder.buildSoftmaxModel(4, 3);

            // train the model
            model.fit(trainingData);

            // evaluate the model
            Evaluation evaluation = new Evaluation(3);
            INDArray output = model.output(testData.getFeatures());
            evaluation.eval(testData.getLabels(), output);

            // log
            this.serviceLog.info(evaluation.stats());

        } catch (IOException exception) {
            String message = "Got IO exception: " + exception.getLocalizedMessage();
            this.serviceLog.severe(message);
            throw new MlException(message);

        } catch (NumberFormatException exception) {
            String message = "Got number format exception: " + exception.getLocalizedMessage();
            this.serviceLog.severe(message);
            throw new MlException(message);

        } catch (InterruptedException exception) {
            String message = "Got Interrupted exception: " + exception.getLocalizedMessage();
            throw new MlException(message);
        }



    }
}
