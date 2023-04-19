package com.mhcure.javatools;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import com.mhcure.javatools.config.MhFileAggregatorProperties;
import com.mhcure.javatools.filereader.MhFileReader;
import com.mhcure.javatools.filewriter.MhFileWriter;
import com.mhcure.javatools.helper.MhFileAggregatoHelper;


/*
 * >mvn clean install  -U dependency:copy-dependencies
 */
@SpringBootApplication
public class LogAggregatorApplication {

    @Autowired
    MhFileAggregatorProperties mhFileAggregatorProperties;
    @Autowired
    private MhFileReader mhFileReader;
    @Autowired
    private MhFileWriter mhFileWriter;

    @Value("${com.mhcure.logfiles.backslach}")
    private String backslash;
    @Value("${com.mhcure.logfiles.outputfiles}")
    private String outputfiles;
    @Value("${com.mhcure.logfiles.merge}")
    private String mergefiles;
    @Value("${com.mhcure.lofiles.corectvalue}")
    private String correctvalue;
    @Value("${com.mhcure.logfiles.lineseperator}")
    private String lineseperator;
    @Value("${com.mhcure.logfiles.totaltime.to.runprogram}")
    private String ToalTimeToRunProgram;
    @Value("${com.mhcure.logfiles.totaltime.to.writefiles}")
    private String TotalTimeToWriteFiles;
    @Value("${com.mhcure.logfiles.totaltime.to.readfiles}")
    private String TotalTimeToReadFiles;
    @Value("${com.mhcure.logfiles.megelogfiles}")
    private String MergedLogFiles;
    @Value("${com.mhcure.logfiles.tomergefiles}")
    private String ToMergeFiles;
    @Value("${com.mhcure.logfiles.toexitthefiles}")
    private String ToExitTheFiles;
    @Value("${com.mhcure.logfiles.restartprogram}")
    private String ToRestartProgram;
    @Value("${com.mhcure.logfiles.invalidentry}")
    private String InvalidEntry;
    @Value("${com.mhcure.logfiles.filestomerge}")
    private String FilesToMerge;
    @Value("${com.mhcure.logfiles.totalfiles}")
    private String TotalFiles;
    @Value("${com.mhcure.logfiles.processingfiles}")
    private String FinishedProcessingFiles;
    @Value("${com.mhcure.logfiles.mergingfiles}")
    private String FinishedMergingFiles;
    @Value("${com.mhcure.logfiles.files}")
    private String FIles;
    @Value("${com.mhcure.logfiles.nextline}")
    private String NextLine;
    @Value("${com.mhcure.lofiles.folderspecified}")
    private String LogFilesFolder;

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(LogAggregatorApplication.class, args);
        exitApplication(ctx);
    }

    public static void exitApplication(ConfigurableApplicationContext ctx) {
        int exitCode = SpringApplication.exit(ctx, new ExitCodeGenerator() {
            @Override
            public int getExitCode() {
                // no errors
                return 0;
            }
        });
        System.exit(exitCode);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) throws ParseException, IOException {
        long programStartTime;

        String userInput = "";
        do {
            System.out.println(NextLine);
            MhFileAggregatoHelper.printInstructionsOnConsole(LogFilesFolder + mhFileReader.
                    getMhFileAggregatorProperties().getLogFilesLocation() + NextLine + correctvalue +
                    mhFileAggregatorProperties.getMhFileAggregatorPropertiesLocation() + ToRestartProgram + mergefiles);
            Scanner in = new Scanner(System.in);
            userInput = in.nextLine();
            programStartTime = System.currentTimeMillis();
            if (userInput.equalsIgnoreCase(ToMergeFiles)) {
                performLogAggregation();
            } else {
                if (!userInput.equalsIgnoreCase(ToMergeFiles) && !userInput.equalsIgnoreCase(ToExitTheFiles)) {
                    MhFileAggregatoHelper.printInstructionsOnConsole(InvalidEntry
                            + mergefiles);
                }
            }
            long programEndTime = System.currentTimeMillis();
            double programTimeInseconds = getTimeDiffInSeconds(programEndTime, programStartTime);
            System.out.println(ToalTimeToRunProgram + programTimeInseconds);
        } while (!userInput.equalsIgnoreCase(ToExitTheFiles) && !userInput.equalsIgnoreCase(ToMergeFiles));

        return args -> {
        };
    }

    private void performLogAggregation() throws ParseException, IOException {

        long programStartTime = System.currentTimeMillis();
        List<String> logFilesPathList = mhFileReader.getFilesList();
        Map<Long, String> fileContentsMap = new HashMap<>();
        TreeMap<Long, String> fileContentsTreeMap = new TreeMap<>();
        int totalFilesCount = logFilesPathList.size();
        String outputFileName = mhFileWriter.getLogFilesOutputName();
        if (totalFilesCount > 0) {
            System.out.println(TotalFiles + totalFilesCount + FilesToMerge);
            int fileCounter = 0;
            long fileReadStartTime = System.currentTimeMillis();
            for (String logFileName : logFilesPathList) {
                fileCounter++;
                Map<Long, String> singleFileContentsMap = new HashMap<>();
                TreeMap<Long, String> singleFileContentsTreeMap = new TreeMap<>();
                if (!outputFileName.equals(logFileName)) {
                    try {
                        singleFileContentsMap = mhFileReader.readFileUsingBufferedReader(logFileName);// UseThisToUseHashMap
                        fileContentsTreeMap.putAll(singleFileContentsMap);
                        fileContentsTreeMap.putAll(singleFileContentsTreeMap);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    System.out.println(outputfiles);
                }
                System.out.println(FinishedProcessingFiles + fileCounter + backslash + totalFilesCount + FIles);
                
                //Write Decrypted File
                if(MhFileAggregatoHelper.isFileEncrypted(logFileName)) {
            		String destinationFileName = logFileName.substring(0, logFileName.lastIndexOf("."));                
                    mhFileWriter.writeToFile(destinationFileName, singleFileContentsMap);
                }
            }
            long fileReadEndTime = System.currentTimeMillis();
            double fileReadTimeInseconds = getTimeDiffInSeconds(fileReadEndTime, fileReadStartTime);
            System.out.println(TotalTimeToReadFiles + fileReadTimeInseconds);
            List<String> fileContentsList = new ArrayList(fileContentsMap.values());

            long fileWriteStartTime = System.currentTimeMillis();

            mhFileWriter.writeToFile(fileContentsTreeMap);// UseThisToUseTreeMap

            long fileWriteEndTime = System.currentTimeMillis();
            double fileWriteTimeInseconds = getTimeDiffInSeconds(fileWriteEndTime, fileWriteStartTime);
            System.out.println(TotalTimeToWriteFiles + fileWriteTimeInseconds);
            System.out.println(lineseperator);
            System.out.println(FinishedMergingFiles + totalFilesCount + MergedLogFiles
                    + mhFileWriter.getLogFilesOutputLocation() + backslash + mhFileWriter.getLogFilesOutputName());
            System.out.println(lineseperator);
            System.out.println(lineseperator);
        }
    }

    private double getTimeDiffInSeconds(long endTime, long startTime) {
        return (endTime - startTime) / (1000.0);
    }
}
