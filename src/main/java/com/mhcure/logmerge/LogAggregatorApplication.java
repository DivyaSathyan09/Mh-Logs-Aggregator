package com.mhcure.logmerge;

import com.mhcure.logmerge.config.MhFileAggregatorProperties;
import com.mhcure.logmerge.constants.UserPromptConstants;
import com.mhcure.logmerge.filereader.MhFileReader;
import com.mhcure.logmerge.filewriter.MhFileWriter;
import com.mhcure.logmerge.helper.MhFileAggregatoHelper;
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
    private String backSlash;
    @Value("${com.mhcure.userInfo.message.outputfiles}")
    private String outPutFiles;
    @Value("${com.mhcure.userPrompt.message.merge}")
    private String mergedFile;
    @Value("${com.mhcure.userInfo.message.corectvalue}")
    private String correctValue;
    @Value("${com.mhcure.userPrompt.message.lineseperator}")
    private String lineSeparator;
    @Value("${com.mhcure.userInfo.message.totaltime.to.runprogram}")
    private String totalTimeToRunProgram;
    @Value("${com.mhcure.userInfo.message.totaltime.to.writefiles}")
    private String totalTimeToWriteFiles;
    @Value("${com.mhcure.userInfo.message.totaltime.to.readfiles}")
    private String totalTimeToReadFiles;
    @Value("${com.mhcure.userInfo.message.mergelogfiles}")
    private String mergedLOgFiles;
    @Value("${com.mhcure.logfiles.mergefiles}")
    private String toMergeFiles;
    @Value("${com.mhcure.logfiles.toexitthefiles}")
    private String toExitTheProgramme;
    @Value("${com.mhcure.userPrompt.message.restartprogram}")
    private String toRestartTheProgramme;
    @Value("${com.mhcure.userInfo.message.invalidentry}")
    private String inValidEntry;
    @Value("${com.mhcure.logfiles.filestomerge}")
    private String filesToMere;
    @Value("${com.mhcure.userInfo.message.totalfiles}")
    private String totalFiles;
    @Value("${com.mhcure.userInfo.message.processingfiles}")
    private String finishedProcessingFiles;
    @Value("${com.mhcure.userInfo.message.mergingfiles}")
    private String finishedMergingFiles;
    @Value("${com.mhcure.userInfo.message.files}")
    private String files;
    @Value("${com.mhcure.logfiles.newLineChar}")
    private String newLineChar;
    @Value("${com.mhcure.userPrompt.message.folderspecified}")
    private String logFilesFolder;
    @Value("${com.mhcure.logfiles.validkey.to.save.decrypted.files}")
    private String toSaveDecryptedFiles;
    @Value("${com.mhcure.logfiles.invalidkey.to.save.decrypted.files}")
    private String toContinueWithoutSaving;
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
        do {
            System.out.println(newLineChar);
            MhFileAggregatoHelper.printInstructionsOnConsole(logFilesFolder + mhFileReader.
                    getMhFileAggregatorProperties().getLogfileslocation() + newLineChar + correctValue +
                    mhFileAggregatorProperties.getMhFileAggregatorPropertiesLocation() + toRestartTheProgramme + mergedFile);
            Scanner in = new Scanner(System.in);
            userInput = in.nextLine();
            programStartTime = System.currentTimeMillis();
            if (userInput.equalsIgnoreCase(toMergeFiles)) {
                performLogAggregation();
            } else {
                if (!userInput.equalsIgnoreCase(toMergeFiles) && !userInput.equalsIgnoreCase(toExitTheProgramme)) {
                    MhFileAggregatoHelper.printInstructionsOnConsole(inValidEntry
                            + mergedFile);
                }
            }
            long programEndTime = System.currentTimeMillis();
            double programTimeInseconds = getTimeDiffInSeconds(programEndTime, programStartTime);
            System.out.println(totalTimeToRunProgram + programTimeInseconds);
        } while (!userInput.equalsIgnoreCase(toExitTheProgramme) && !userInput.equalsIgnoreCase(toMergeFiles));

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
            System.out.println(UserPromptConstants.PROMPT_MESSAGE_TO_ASK_USER_FOR_SAVING_DECRYPTED_FILES.getKey());
            keyToSaveDecryptedFiles = new Scanner(System.in).next();
            while(!keyToSaveDecryptedFiles.equalsIgnoreCase(toSaveDecryptedFiles) && !keyToSaveDecryptedFiles.equalsIgnoreCase(toContinueWithoutSaving)){
                System.out.println(UserPromptConstants.PROMPT_MESSAGE_AT_INVALID_KEY_FOR_SAVING_DECRYPTED_FILES.getKey());
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
                    System.out.println(outPutFiles);
                }
                System.out.println(finishedProcessingFiles + fileCounter + backSlash + totalFilesCount +files);

                //Write Decrypted File
                if(MhFileAggregatoHelper.isFileEncrypted(logFileName)) {
                    String destinationFileName = logFileName.substring(0, logFileName.lastIndexOf("."));
                    if(keyToSaveDecryptedFiles.equalsIgnoreCase(toSaveDecryptedFiles)){
                    mhFileWriter.writeToFile(destinationFileName, singleFileContentsMap);
                    }
                }
            }
            long fileReadEndTime = System.currentTimeMillis();
            double fileReadTimeInseconds = getTimeDiffInSeconds(fileReadEndTime, fileReadStartTime);
            System.out.println(totalTimeToReadFiles + fileReadTimeInseconds);
            List<String> fileContentsList = new ArrayList(fileContentsMap.values());

            long fileWriteStartTime = System.currentTimeMillis();

            mhFileWriter.writeToFile(fileContentsTreeMap);// UseThisToUseTreeMap

            long fileWriteEndTime = System.currentTimeMillis();
            double fileWriteTimeInseconds = getTimeDiffInSeconds(fileWriteEndTime, fileWriteStartTime);
            System.out.println(totalTimeToWriteFiles + fileWriteTimeInseconds);
            System.out.println(lineSeparator);
            System.out.println(finishedMergingFiles + totalFilesCount + mergedLOgFiles
                    + mhFileWriter.getLogFilesOutPutLocatiobn() + backSlash + mhFileWriter.getLogFilesOutPutLocatiobn());
            System.out.println(lineSeparator);
            System.out.println(lineSeparator);
        }
    }

    private double getTimeDiffInSeconds(long endTime, long startTime) {
        return (endTime - startTime) / (1000.0);
    }
}
