package com.doobs.mlbeta.util;

import org.apache.commons.io.FileUtils;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.records.writer.RecordWriter;
import org.datavec.api.records.writer.impl.csv.CSVRecordWriter;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.partition.NumberOfRecordsPartitioner;
import org.datavec.api.split.partition.Partitioner;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.writable.Writable;
import org.datavec.local.transforms.LocalTransformExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Component
public class FileConverter {
    // instance variables
    private Logger converterLog = Logger.getLogger(this.getClass().getName());

    @Autowired
    ResourceLoader resourceLoader;

    public File transformFile(File inputFile, String outputPath, TransformProcess transformProcess, int linesToSkip, String delimiter) throws MlException {
        // local variables
        File outputFile = null;

        // log
        this.converterLog.info("Transforming file: " + (inputFile == null ? null : inputFile.getPath()));
        this.converterLog.info("New file will be: " + (outputPath == null ? null : outputPath));

        try {
            // get the transformed file
//        outputFile = new File(outputPath);
            outputFile = resourceLoader.getResource(outputPath).getFile();

            if(outputFile.exists()){
                outputFile.delete();
            }
            outputFile.createNewFile();

            //Define input reader and output writer:
            RecordReader rr = new CSVRecordReader(linesToSkip, delimiter);
            rr.initialize(new FileSplit(inputFile));

            RecordWriter rw = new CSVRecordWriter();
            Partitioner p = new NumberOfRecordsPartitioner();
            rw.initialize(new FileSplit(outputFile), p);

            //Process the data:
            List<List<Writable>> originalData = new ArrayList<>();
            while(rr.hasNext()){
                originalData.add(rr.next());
            }

            List<List<Writable>> processedData = LocalTransformExecutor.execute(originalData, transformProcess);
            rw.writeBatch(processedData);
            rw.close();


            //Print before + after:
            System.out.println("\n\n---- Original Data File ----");
            String originalFileContents = FileUtils.readFileToString(inputFile, Charset.defaultCharset());
            System.out.println(originalFileContents);

            System.out.println("\n\n---- Processed Data File ----");
            String fileContents = FileUtils.readFileToString(outputFile, Charset.defaultCharset());
            System.out.println(fileContents);

            System.out.println("\n\nDONE");

        } catch (IOException exception) {
            String message = "Got IO exception: " + exception.getLocalizedMessage();
            this.converterLog.severe(message);
            throw new MlException(message);

        } catch (InterruptedException exception) {
            String message = "Got interrupted exception: " + exception.getLocalizedMessage();
            this.converterLog.severe(message);
            throw new MlException(message);

        } catch (Exception exception) {
            String message = "Got general exception: " + exception.getLocalizedMessage();
            exception.printStackTrace();
            this.converterLog.severe(message);
            throw new MlException(message);
        }

        // return
        return outputFile;
    }
}
