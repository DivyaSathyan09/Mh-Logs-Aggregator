package com.mhcure.logmerge;

import com.mhcure.logmerge.config.MhFileAggregatorProperties;
import com.mhcure.logmerge.constants.MhFileConstants;
import com.mhcure.logmerge.filereader.MhFileReader;
import com.mhcure.logmerge.filewriter.MhFileWriter;
import com.mhcure.logmerge.helper.MhFileAggregatorHelper;
import com.mhcure.logmerge.utils.MhMessagePropertiesFileReader;
import com.mhcure.logmerge.utils.MhMessageKeyEnum;
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
    @Value("${com.mhcure.logfiles.mergefiles_yes_value}")
    private String mergeFilesYes;
    @Value("${com.mhcure.logfiles.toExit}")
    private String exitApplicationYes;
    private String keyToSaveDecryptedFiles;

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
        MhFileAggregatorHelper.printInstructionsOnConsole(MhFileConstants.NEW_LINE_CHAR);
        do {
            MhFileAggregatorHelper.printToConsole(MhMessagePropertiesFileReader.getMessage(MhMessageKeyEnum.
                    MESSAGE_SPECIFIED_FOLDER.getKey()).replace("logFolderLocation", mhFileReader.getLogFilesLocation()));
            MhFileAggregatorHelper.printToConsole(MhFileConstants.USER_PROMPT_SPACE);
            Scanner in = new Scanner(System.in);
            userInput = in.nextLine();
            programStartTime = System.currentTimeMillis();
            if (userInput.equalsIgnoreCase(mergeFilesYes)) {
                performLogAggregation();
            } else {
                if (!userInput.equalsIgnoreCase(mergeFilesYes) && !userInput.equalsIgnoreCase(exitApplicationYes)) {
                    MhFileAggregatorHelper.printToConsole(MhFileConstants.USER_PROMPT_SPACE);
                    MhFileAggregatorHelper.printToConsole(MhMessagePropertiesFileReader
                            .getMessage(MhMessageKeyEnum.INVALID_INPUT_TO_CONTINUE_MERGING_LOG_FILE.getKey()));
                }
            }
        } while (!userInput.equalsIgnoreCase(exitApplicationYes) && !userInput.equalsIgnoreCase(mergeFilesYes));
        long programEndTime = System.currentTimeMillis();
        double programTimeInseconds = getTimeDiffInSeconds(programEndTime, programStartTime);

        MhFileAggregatorHelper.printToConsole(MhMessagePropertiesFileReader.getMessage(MhMessageKeyEnum.
                MESSAGE_TOTAL_TIME_TO_RUN_PROGRAM.getKey()) + programTimeInseconds);
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
            MhFileAggregatorHelper.printToConsole(MhMessagePropertiesFileReader.getMessage(MhMessageKeyEnum.TOTAL_FILES_FOUND.getKey()).replace("totalFiles", "" + totalFilesCount));
            MhFileAggregatorHelper.printToConsole(MhMessagePropertiesFileReader.getMessage(MhMessageKeyEnum.
                    MESSAGE_TO_SAVE_DECRYPTED_FILES.getKey()));
            MhFileAggregatorHelper.printToConsole(MhFileConstants.USER_PROMPT_SPACE);
            keyToSaveDecryptedFiles = new Scanner(System.in).next();
            while (!keyToSaveDecryptedFiles.equalsIgnoreCase(mergeFilesYes) && !keyToSaveDecryptedFiles.
                    equalsIgnoreCase(exitApplicationYes)) {
                MhFileAggregatorHelper.printToConsole(MhFileConstants.USER_PROMPT_SPACE);
                MhFileAggregatorHelper.printToConsole(MhMessagePropertiesFileReader.getMessage(MhMessageKeyEnum.
                        INVALID_INPUT_TO_SAVE_DECRYPTED_FILE.getKey()));
                MhFileAggregatorHelper.printToConsole(MhFileConstants.USER_PROMPT_SPACE);
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
                    MhFileAggregatorHelper.printToConsole(MhMessagePropertiesFileReader.getMessage(MhMessageKeyEnum.IGNORE_MERGING.getKey()));
                }
                MhFileAggregatorHelper.printToConsole(MhMessagePropertiesFileReader.getMessage(MhMessageKeyEnum.MESSAGE_PROCESSING_FILE
                        .getKey()).replace("totalNumberOf", "" + fileCounter).replace("totalfiles", "" + totalFilesCount));
                //Write Decrypted File
                if (MhFileAggregatorHelper.isFileEncrypted(logFileName)) {
                    String destinationFileName = logFileName.substring(0, logFileName.lastIndexOf("."));
                    if (keyToSaveDecryptedFiles.equalsIgnoreCase(mergeFilesYes)) {
                        mhFileWriter.writeDecryptedFile(destinationFileName, singleFileContentsMap);
                    }
                }
            }
            long fileReadEndTime = System.currentTimeMillis();
            double fileReadTimeInseconds = getTimeDiffInSeconds(fileReadEndTime, fileReadStartTime);
            MhFileAggregatorHelper.printToConsole(MhMessagePropertiesFileReader.getMessage(MhMessageKeyEnum.TOTAL_TIME_TO_READ_FILES
                    .getKey()) + fileReadTimeInseconds);
        }
        List<String> fileContentsList = new ArrayList(fileContentsMap.values());
        long fileWriteStartTime = System.currentTimeMillis();
        mhFileWriter.writeToFile(fileContentsTreeMap);// UseThisToUseTreeMap
        long fileWriteEndTime = System.currentTimeMillis();
        double fileWriteTimeInSeconds = getTimeDiffInSeconds(fileWriteEndTime, fileWriteStartTime);
        MhFileAggregatorHelper.printToConsole(MhMessagePropertiesFileReader.getMessage(MhMessageKeyEnum.TOTAL_TIME_TO_WRITE_FILES.
                getKey()) + fileWriteTimeInSeconds);
        MhFileAggregatorHelper.printToConsole(MhFileConstants.LINE_SEPARATOR);
        MhFileAggregatorHelper.printToConsole(MhMessagePropertiesFileReader.getMessage(MhMessageKeyEnum.MESSAGE_FINISHED_MERGING.getKey()).replace("totalFiles", "" + totalFilesCount)
                .replace("mergedFileLocation", mhFileWriter.getLogFilesOutPutLocation() + MhFileConstants.BACKSLASH + mhFileWriter.getLogFilesOutPutName()));
        MhFileAggregatorHelper.printToConsole(MhFileConstants.LINE_SEPARATOR);
        MhFileAggregatorHelper.printToConsole(MhFileConstants.LINE_SEPARATOR);
    }

    private double getTimeDiffInSeconds(long endTime, long startTime) {
        return (endTime - startTime) / (1000.0);
    }
}
