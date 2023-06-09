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

import javax.security.auth.kerberos.EncryptionKey;
import java.io.File;
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

    @Value("${save_decrypted_files}")
    private String saveDecryptedFiles;

    @Value("${generated_decrypted.files_location}")
    private String decryptedFileLocation;
    public static void main(String[] args) {
        MhFileAggregatorHelper.printToConsole("!!!!!!!!!!!! starting app");
        ConfigurableApplicationContext ctx = SpringApplication.run(LogAggregatorApplication.class, args);
        MhFileAggregatorHelper.printToConsole("!!!!!!!!!!!! exiting app");
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
        MhFileAggregatorHelper.printToConsole("!!!!!!!!!!!! inside commandLinRunner");
        long programStartTime = 0;
        if (isMergingRequired()) {
            programStartTime = System.currentTimeMillis();
            performLogAggregation();
        }
        long programEndTime = System.currentTimeMillis();
        double programTimeInSeconds = getTimeDiffInSeconds(programEndTime, programStartTime);
        MhFileAggregatorHelper.printToConsole(MhMessagePropertiesFileReader.getMessage(MhMessageKeyEnum.
                MESSAGE_TOTAL_TIME_TO_RUN_PROGRAM.getKey()) + programTimeInSeconds);
        return args -> {
        };
    }

    private boolean isMergingRequired() {
        String userInput = "";
        MhFileAggregatorHelper.printInstructionsOnConsole(MhFileConstants.NEW_LINE_CHAR);
        do {
            MhFileAggregatorHelper.printToConsole(MhMessagePropertiesFileReader.getMessage(MhMessageKeyEnum.
                    MESSAGE_SPECIFIED_FOLDER.getKey()).replace(MhFileConstants.LOG_FOLDER_LOCATION, mhFileReader.getLogFilesLocation()));
            MhFileAggregatorHelper.printToConsole(MhFileConstants.USER_PROMPT_SPACE);
            Scanner in = new Scanner(System.in);
            userInput = in.nextLine();
            if (!userInput.equalsIgnoreCase(mergeFilesYes) && !userInput.equalsIgnoreCase(exitApplicationYes)) {
                MhFileAggregatorHelper.printToConsole(MhFileConstants.USER_PROMPT_SPACE);
                MhFileAggregatorHelper.printToConsole(MhMessagePropertiesFileReader
                        .getMessage(MhMessageKeyEnum.INVALID_INPUT_TO_CONTINUE_MERGING_LOG_FILE.getKey()));
            }
        } while (!userInput.equalsIgnoreCase(exitApplicationYes) && !userInput.equalsIgnoreCase(mergeFilesYes));
        return userInput.equalsIgnoreCase(mergeFilesYes);
    }

    private void performLogAggregation() throws IOException {
        MhFileAggregatorHelper.printToConsole("!!!!!!!!!!!! inside performLogAggregation");

        List<String> logFilesPathList = mhFileReader.getFilesList();
        Map<Long, String> fileContentsMap = new HashMap<>();
        TreeMap<Long, String> fileContentsTreeMap = new TreeMap<>();
        int totalFilesCount = logFilesPathList.size();
        String outputFileName = mhFileWriter.getLogFilesOutPutName();
        if (totalFilesCount > 0) {
            MhFileAggregatorHelper.printToConsole(MhMessagePropertiesFileReader.getMessage(MhMessageKeyEnum.TOTAL_FILES_FOUND
                    .getKey()).replace(MhFileConstants.TOTAL_FILES, "" + totalFilesCount));
            int fileCounter = 0;
            long fileReadStartTime = System.currentTimeMillis();
            boolean saveDecryptedFile = isDecryptedFilesSavingRequired();
            for (String logFileName : logFilesPathList) {
                fileCounter++;
                Map<Long, String> singleFileContentsMap = new HashMap<>();
                TreeMap<Long, String> singleFileContentsTreeMap = new TreeMap<>();
                if (!outputFileName.equals(logFileName)) {
                        singleFileContentsMap = mhFileReader.readFileUsingBufferedReader(logFileName);// UseThisToUseHashMap
                    if (singleFileContentsMap == null){
                        return;
                    }
                        fileContentsTreeMap.putAll(singleFileContentsMap);
                        fileContentsTreeMap.putAll(singleFileContentsTreeMap);

                } else {
                    MhFileAggregatorHelper.printToConsole(MhMessagePropertiesFileReader.getMessage(MhMessageKeyEnum.IGNORE_MERGING.getKey()));
                }
                MhFileAggregatorHelper.printToConsole(MhMessagePropertiesFileReader.getMessage(MhMessageKeyEnum.MESSAGE_PROCESSING_FILE
                        .getKey()).replace(MhFileConstants.TOTAL_NUMBER_OF_FILES, "" + fileCounter).replace(MhFileConstants.TOTAL_FILES , "" + totalFilesCount));

                //Write Decrypted File
                if (MhFileAggregatorHelper.isFileEncrypted(logFileName)) {
                    String destinationFileName = logFileName.substring(0, logFileName.lastIndexOf("."));
                    if (saveDecryptedFile) {
                        mhFileWriter.writeDecryptedFile(destinationFileName, singleFileContentsMap);
                    }
                }
            }
            long fileReadEndTime = System.currentTimeMillis();
            double fileReadTimeInseconds = getTimeDiffInSeconds(fileReadEndTime, fileReadStartTime);
            MhFileAggregatorHelper.printToConsole(MhMessagePropertiesFileReader.getMessage(MhMessageKeyEnum.TOTAL_TIME_TO_READ_FILES
                    .getKey()) + fileReadTimeInseconds);
        } else {
            MhFileAggregatorHelper.printToConsole(MhMessagePropertiesFileReader.
                    getMessage(MhMessageKeyEnum.MESSAGE_INVALID_FILE_LOCATION.getKey()));
            MhFileAggregatorHelper.printToConsole(MhFileConstants.LINE_SEPARATOR);
            return;
        }
        long fileWriteStartTime = System.currentTimeMillis();
        MhFileAggregatorHelper.printToConsole("!!!!!!!!!!!! calling writeToFile");

        mhFileWriter.writeToFile(fileContentsTreeMap);// UseThisToUseTreeMap
        long fileWriteEndTime = System.currentTimeMillis();
        double fileWriteTimeInSeconds = getTimeDiffInSeconds(fileWriteEndTime, fileWriteStartTime);
        MhFileAggregatorHelper.printToConsole(MhMessagePropertiesFileReader.getMessage(MhMessageKeyEnum.TOTAL_TIME_TO_WRITE_FILES.
                getKey()) + fileWriteTimeInSeconds);
        MhFileAggregatorHelper.printToConsole(MhFileConstants.LINE_SEPARATOR);
        MhFileAggregatorHelper.printToConsole(MhMessagePropertiesFileReader.getMessage(MhMessageKeyEnum.
                        MESSAGE_FINISHED_MERGING.getKey()).replace(MhFileConstants.TOTAL_FILES, "" + totalFilesCount)
                .replace(MhFileConstants.MERGED_FILE_LOCATION, mhFileWriter.getLogFilesOutPutLocation() + MhFileConstants.FORWARD_SLASH + mhFileWriter.getLogFilesOutPutName()));
        MhFileAggregatorHelper.printToConsole(MhFileConstants.LINE_SEPARATOR);
        MhFileAggregatorHelper.printToConsole(MhFileConstants.LINE_SEPARATOR);
    }

    private boolean isDecryptedFilesSavingRequired() {
        if(!new File(decryptedFileLocation).exists()){
            MhFileAggregatorHelper.printToConsole(MhMessagePropertiesFileReader.getMessage(MhMessageKeyEnum.INVALID_DECRYPTED_LOCATION.getKey()));
            return false;
        }
        if (saveDecryptedFiles.equalsIgnoreCase(saveDecryptedFiles)) {
            MhFileAggregatorHelper.printToConsole(MhMessagePropertiesFileReader.getMessage
                    (MhMessageKeyEnum.SAVE_DECRYPTED_FILES.getKey()));
        }
        return saveDecryptedFiles.equalsIgnoreCase(MhFileConstants.USER_PERMISSION_YES);
    }

    private double getTimeDiffInSeconds(long endTime, long startTime) {
        return (endTime - startTime) / (1000.0);
    }
}
