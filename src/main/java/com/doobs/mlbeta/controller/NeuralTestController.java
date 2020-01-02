package com.doobs.mlbeta.controller;

import com.doobs.mlbeta.service.dl4j.SimpleNetworkService;
import com.doobs.mlbeta.util.MlException;
import com.doobs.mlbeta.util.MlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.management.loading.MLet;
import java.util.logging.Logger;

@RestController
@RequestMapping("/testml")
public class NeuralTestController {
    // instance variables
    private final Logger controllerLog = Logger.getLogger(this.getClass().getName());

    @Autowired
    SimpleNetworkService simpleNetworkService;

    @RequestMapping(value = "/simplenet", method = RequestMethod.GET, produces = "application/json")
    public Object runSimpleNetwork() {
        // local variables
        Object result = null;

        // log
        this.controllerLog.info("Got request for simplenet");

        // make the call
        try {
            // call the service
            this.simpleNetworkService.runPetalExample();

        } catch (MlException exception) {
            result = MlUtils.getErrorMap(exception);
        }

        // return
        return result;
    }

}
