package com.doobs.mlbeta.model;

import com.doobs.mlbeta.util.MlException;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class ModelBuilder {
    // instance variables
    private Logger builderLog = Logger.getLogger(this.getClass().getName());

    public MultiLayerNetwork buildSoftmaxModel(int numInputs, int numOutputs, int numMiddleInputs) throws MlException {
        // local variables
        MultiLayerConfiguration multiLayerConfiguration = null;
        MultiLayerNetwork model = null;

        // build the configuration
        multiLayerConfiguration = new NeuralNetConfiguration.Builder()
                .seed(6)
                .activation(Activation.TANH)
                .weightInit(WeightInit.XAVIER)
                .list()
                .layer(0, new DenseLayer.Builder().nIn(numInputs).nOut(numMiddleInputs).build())
                .layer(1, new DenseLayer.Builder().nIn(numMiddleInputs).nOut(numMiddleInputs).build())
                .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                            .activation(Activation.SOFTMAX).nIn(numMiddleInputs).nOut(numOutputs).build())
                .backpropType(BackpropType.Standard)
                .build();

        // build the model
        model = new MultiLayerNetwork(multiLayerConfiguration);
        model.init();
        this.builderLog.info("Default learning rate is: " + model.getLearningRate(0) + " - " + model.getLearningRate(1) + " - " + model.getLearningRate(2));
        model.setLearningRate(0.1);
        this.builderLog.info("New learning rate is: " + model.getLearningRate(0) + " - " + model.getLearningRate(1) + " - " + model.getLearningRate(2));
        model.setListeners(new ScoreIterationListener(100));

        // return
        return model;
    }
}
