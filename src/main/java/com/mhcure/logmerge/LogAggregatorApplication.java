package com.mhcure.logmerge;

import com.mhcure.logmerge.config.MhFileAggregatorProperties;
import com.mhcure.logmerge.constants.MhFileConstants;
import com.mhcure.logmerge.filereader.MhFileReader;
import com.mhcure.logmerge.filewriter.MhFileWriter;
import com.mhcure.logmerge.helper.MhFileAggregatorHelper;
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

    @Autowired
    MhFileAggregatorProperties mhFileAggregatorProperties;
    @Autowired
    private MhFileReader mhFileReader;
    @Autowired
    private MhFileWriter mhFileWriter;

    @Value("${com.mhcure.userInfo.message.ignore_outputfiles}")
    private String ignoreOutPutFilesMessage;
    @Value("${com.mhcure.userPrompt.message.proccedWithMerge}")
    private String mergeFileProceedMessage;
    @Value("${com.mhcure.userPrompt.message.correctFolderValue}")
    private String correctValue;
    @Value("${com.mhcure.userInfo.message.totaltime.to.runprogram}")
    private String totalTimeToRunProgram;
    @Value("${com.mhcure.userInfo.message.totaltime.to.writefiles}")
    private String totalTimeToWriteFiles;
    @Value("${com.mhcure.userInfo.message.totaltime.to.readfiles}")
    private String totalTimeToReadFiles;
    @Value("${com.mhcure.userInfo.message.mergelogfiles}")
    private String mergedLogFiles;
    @Value("${com.mhcure.logfiles.mergefiles_yes_value}")
    private String toMergeFiles;
    @Value("${com.mhcure.logfiles.toExit}")
    private String valueToExitApplication;
    @Value("${com.mhcure.userPrompt.message.restartprogram}")
    private String restartProgrammeMessage;
    @Value("${com.mhcure.userInfo.message.invalidentry}")
    private String inValidEntryMessage;
    @Value("${com.mhcure.userInfo.message.filesTBeMerge}")
    private String filesToMere;
    @Value("${com.mhcure.userInfo.message.totalfiles}")
    private String totalFiles;
    @Value("${com.mhcure.userInfo.message.processingfiles}")
    private String finishedProcessingFiles;
    @Value("${com.mhcure.userInfo.message.mergingfiles}")
    private String finishedMergingFiles;
    @Value("${com.mhcure.userInfo.message.files}")
    private String files;
    @Value("${com.mhcure.userPrompt.message.folderspecified}")
    private String logFilesFolder;
    @Value("${com.mhcure.logfiles.save.decrypted.files_yes_value}")
    private String toSaveDecryptedFiles;
    @Value("${com.mhcure.logfiles.save.decrypted.files_no_value}")
    private String toContinueWithoutSaving;
    private String keyToSaveDecryptedFiles;

    @Value("${com.mhcure.logfiles.ask_to_save.decrypted.files}")
    private String messageToStoreDecryptedFiles;
    @Value("${com.mhcure.logfiles.invalid.entry_to_save_decrypted.files}")
    private String invalidEntryToStoreDecryptedFiles;

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
            System.out.println(MhFileConstants.NEW_LINE_CHAR.getKey());
            MhFileAggregatorHelper.printInstructionsOnConsole(logFilesFolder + mhFileReader.
                    getMhFileAggregatorProperties().getLogfileslocation() + MhFileConstants.NEW_LINE_CHAR.getKey() + correctValue +
                    mhFileAggregatorProperties.getMhFileAggregatorPropertiesLocation() + restartProgrammeMessage + mergeFileProceedMessage);
            Scanner in = new Scanner(System.in);
            userInput = in.nextLine();
            programStartTime = System.currentTimeMillis();
            if (userInput.equalsIgnoreCase(toMergeFiles)) {
                performLogAggregation();
            } else {
                if (!userInput.equalsIgnoreCase(toMergeFiles) && !userInput.equalsIgnoreCase(valueToExitApplication)) {
                    MhFileAggregatorHelper.printInstructionsOnConsole(inValidEntryMessage
                            + mergeFileProceedMessage);
                }
            }
            long programEndTime = System.currentTimeMillis();
            double programTimeInseconds = getTimeDiffInSeconds(programEndTime, programStartTime);

            System.out.println(totalTimeToRunProgram + programTimeInseconds);
        } while (!userInput.equalsIgnoreCase(valueToExitApplication) && !userInput.equalsIgnoreCase(toMergeFiles));

        return args -> {
        };
    }

    private void performLogAggregation() throws ParseException, IOException {

        long programStartTime = System.currentTimeMillis();
        List<String> logFilesPathList = mhFileReader.getFilesList();
        Map<Long, String> fileContentsMap = new HashMap<>();
        TreeMap<Long, String> fileContentsTreeMap = new TreeMap<>();
        int totalFilesCount = logFilesPathList.size();

        String outputFileName = mhFileWriter.getLogFilesOutPutName();
        if (totalFilesCount > 0) {
            System.out.println(totalFiles + totalFilesCount + filesToMere);
            System.out.println(messageToStoreDecryptedFiles);
            keyToSaveDecryptedFiles = new Scanner(System.in).next();
            while (!keyToSaveDecryptedFiles.equalsIgnoreCase(toSaveDecryptedFiles) && !keyToSaveDecryptedFiles.equalsIgnoreCase(toContinueWithoutSaving)) {
                System.out.println(invalidEntryToStoreDecryptedFiles);
                keyToSaveDecryptedFiles = new Scanner(System.in).next();
            }
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
                  System.out.println(ignoreOutPutFilesMessage);
                }
                System.out.println(finishedProcessingFiles + fileCounter + MhFileConstants.BACKSLASH.getKey() + totalFilesCount + files);

                //Write Decrypted File
                if (MhFileAggregatorHelper.isFileEncrypted(logFileName)) {
                    String destinationFileName = logFileName.substring(0, logFileName.lastIndexOf("."));
                    if (keyToSaveDecryptedFiles.equalsIgnoreCase(toSaveDecryptedFiles)) {
                        mhFileWriter.writeToFile(destinationFileName, singleFileContentsMap);
                    }
                }
            }
            long fileReadEndTime = System.currentTimeMillis();
            double fileReadTimeInseconds = getTimeDiffInSeconds(fileReadEndTime, fileReadStartTime);
            System.out.println(totalTimeToReadFiles + fileReadTimeInseconds);
        }
        List<String> fileContentsList = new ArrayList(fileContentsMap.values());

        long fileWriteStartTime = System.currentTimeMillis();

        mhFileWriter.writeToFile(fileContentsTreeMap);// UseThisToUseTreeMap

        long fileWriteEndTime = System.currentTimeMillis();
        double fileWriteTimeInSeconds = getTimeDiffInSeconds(fileWriteEndTime, fileWriteStartTime);
        System.out.println(totalTimeToWriteFiles + fileWriteTimeInSeconds);
        System.out.println(MhFileConstants.LINE_SEPARATOR.getKey());
        System.out.println(finishedMergingFiles + totalFilesCount + mergedLogFiles
                + mhFileWriter.getLogFilesOutPutLocation() + MhFileConstants.BACKSLASH.getKey() + mhFileWriter.getLogFilesOutPutLocation());
        System.out.println(MhFileConstants.LINE_SEPARATOR.getKey());
        System.out.println(MhFileConstants.LINE_SEPARATOR.getKey());
    }


    private double getTimeDiffInSeconds(long endTime, long startTime) {
        return (endTime - startTime) / (1000.0);
    }
}
