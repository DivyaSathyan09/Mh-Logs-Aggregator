package com.example.logs;

import com.example.logs.config.MhFileAggregatorProperties;
import com.example.logs.filereader.MhFileReader;
import com.example.logs.filewriter.MhFileWriter;
import com.example.logs.helper.MhFileAggregatoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;


/*
 * >mvn clean install  -U dependency:copy-dependencies
 */
@SpringBootApplication
public class LogAggregatorApplication {

    private static final double MEG = (Math.pow(1024, 2));
    @Autowired
    MhFileAggregatorProperties mhFileAggregatorProperties;
    @Autowired
    private MhFileReader mhFileReader;
    @Autowired
    private MhFileWriter mhFileWriter;

    @Value("${com.mhcure.logfiles.backslash}")
    private String backslash;
    @Value("${com.mhcure.userInfo.message.outputfiles}")
    private String outputfiles;
    @Value("${com.mhcure.userPrompt.message.merge}")
    private String mergefiles;
    @Value("${com.mhcure.userInfo.message.corectvalue}")
    private String correctvalue;
    @Value("${com.mhcure.userPrompt.message.lineseperator}")
    private String lineseperator;
    @Value("${com.mhcure.userInfo.message.totaltime.to.runprogram}")
    private String toaltimetorunprogram;
    @Value("${com.mhcure.userInfo.message.totaltime.to.writefiles}")
    private String totaltimetowritefiles;
    @Value("${com.mhcure.userInfo.message.totaltime.to.readfiles}")
    private String totaltimetoreadfiles;
    @Value("${com.mhcure.userInfo.message.mergelogfiles}")
    private String mergedlogfiles;
    @Value("${com.mhcure.logfiles.mergefiles}")
    private String tomergefiles;
    @Value("${com.mhcure.logfiles.toexitthefiles}")
    private String toexitthefiles;
    @Value("${com.mhcure.userPrompt.message.restartprogram}")
    private String torestartprogram;
    @Value("${com.mhcure.userInfo.message.invalidentry}")
    private String invalidentry;
    @Value("${com.mhcure.logfiles.filestomerge}")
    private String filestomerge;
    @Value("${com.mhcure.userInfo.message.totalfiles}")
    private String totalfiles;
    @Value("${com.mhcure.userInfo.message.processingfiles}")
    private String finishedprocessingfiles;
    @Value("${com.mhcure.userInfo.message.mergingfiles}")
    private String finishedmergingfiles;
    @Value("${com.mhcure.userInfo.message.files}")
    private String files;
    @Value("${com.mhcure.logfiles.newLineChar}")
    private String newlinechar;
    @Value("${com.mhcure.userPrompt.message.folderspecified}")
    private String logfilesfolder;

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
            System.out.println(newlinechar);
            MhFileAggregatoHelper.printInstructionsOnConsole(logfilesfolder + mhFileReader.
                    getMhFileAggregatorProperties().getLogfileslocation() + newlinechar + correctvalue +
                    mhFileAggregatorProperties.getMhFileAggregatorPropertiesLocation() + torestartprogram + mergefiles);
            Scanner in = new Scanner(System.in);
            userInput = in.nextLine();
            programStartTime = System.currentTimeMillis();
            if (userInput.equalsIgnoreCase(tomergefiles)) {
                performLogAggregation();
            } else {
                if (!userInput.equalsIgnoreCase(tomergefiles) && !userInput.equalsIgnoreCase(toexitthefiles)) {
                    MhFileAggregatoHelper.printInstructionsOnConsole(invalidentry
                            + mergefiles);
                }
            }
            long programEndTime = System.currentTimeMillis();
            double programTimeInseconds = getTimeDiffInSeconds(programEndTime, programStartTime);
            System.out.println(toaltimetorunprogram + programTimeInseconds);
        } while (!userInput.equalsIgnoreCase(toexitthefiles) && !userInput.equalsIgnoreCase(tomergefiles));

        return args -> {
        };
    }

    private void performLogAggregation() throws ParseException, IOException {

        long programStartTime = System.currentTimeMillis();
        List<String> logFilesPathList = mhFileReader.getFilesList();
        Map<Long, String> fileContentsMap = new HashMap<>();
        TreeMap<Long, String> fileContentsTreeMap = new TreeMap<>();
        int totalFilesCount = logFilesPathList.size();
        String outputFileName = mhFileWriter.getLogfilesoutputname();
        if (totalFilesCount > 0) {
            System.out.println(totalfiles + totalFilesCount + filestomerge);
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
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    System.out.println(outputfiles);
                }
                System.out.println(finishedprocessingfiles + fileCounter + backslash + totalFilesCount + files);
            }
            long fileReadEndTime = System.currentTimeMillis();
            double fileReadTimeInseconds = getTimeDiffInSeconds(fileReadEndTime, fileReadStartTime);
            System.out.println(totaltimetoreadfiles + fileReadTimeInseconds);
            List<String> fileContentsList = new ArrayList(fileContentsMap.values());

            long fileWriteStartTime = System.currentTimeMillis();

            mhFileWriter.writeBufferedUsingTreeMap(fileContentsTreeMap, 4 * (int) MEG);// UseThisToUseTreeMap

            long fileWriteEndTime = System.currentTimeMillis();
            double fileWriteTimeInseconds = getTimeDiffInSeconds(fileWriteEndTime, fileWriteStartTime);
            System.out.println(totaltimetowritefiles + fileWriteTimeInseconds);
            MhFileAggregatoHelper.printTOConsole((lineseperator));
            System.out.println(finishedmergingfiles + totalFilesCount + mergedlogfiles
                    + mhFileWriter.getLogfilesoutputlocation() + backslash + mhFileWriter.getLogfilesoutputname());
            MhFileAggregatoHelper.printTOConsole((lineseperator));
            MhFileAggregatoHelper.printTOConsole((lineseperator));
        }
    }

    private double getTimeDiffInSeconds(long endTime, long startTime) {
        return (endTime - startTime) / (1000.0);
    }
}
