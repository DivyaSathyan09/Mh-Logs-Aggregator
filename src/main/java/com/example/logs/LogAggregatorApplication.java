package com.example.logs;

import com.example.logs.config.MhFileAggregatorProperties;
import com.example.logs.filereader.MhFileReader;
import com.example.logs.filewriter.MhFileWriter;
import com.example.logs.helper.MhFileAggregatoHelper;
import org.springframework.beans.factory.annotation.Autowired;
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
        long programStartTime = System.currentTimeMillis();

        String userInput = "";
        do {
            System.out.println("\n\n\n");
            MhFileAggregatoHelper.printInstructionsOnConsole("Log files folder specified is " +
                    mhFileReader.getMhFileAggregatorProperties().getLogFilesLocation() + " \n"
                    + "If this is not correct then specify the correct value in property file at " + mhFileAggregatorProperties.getMhFileAggregatorPropertiesLocation() + " and restart the program. \n"
                    + "To merge all log files from this location, please enter Y or press X to exit the program");
            Scanner in = new Scanner(System.in);
            userInput = in.nextLine();
            programStartTime = System.currentTimeMillis();
            if (userInput.equalsIgnoreCase("Y")) {
                performLogAggregation();
            } else {
                if (!userInput.equalsIgnoreCase("Y") && !userInput.equalsIgnoreCase("X")) {
                    MhFileAggregatoHelper.printInstructionsOnConsole("Invalid entry. "
                            + "To merge all log files from this location, please enter Y or press X to exit the program");

                }
            }
            long programEndTime = System.currentTimeMillis();
            double programTimeInseconds = getTimeDiffInSeconds(programEndTime, programStartTime);
            System.out.println("Total time (in seconds) to run this program = " + programTimeInseconds);
        } while (!userInput.equalsIgnoreCase("X") && !userInput.equalsIgnoreCase("Y"));

        return args -> {
            //System.out.println("File Merging finished. Please inspect file at outputpath");
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
            System.out.println("Found total " + totalFilesCount + " files to be merged");

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
                    System.out.println("Ignoring file since its output file");
                }
                System.out.println("Finished processing " + fileCounter + "/" + totalFilesCount + " files");

            }
            long fileReadEndTime = System.currentTimeMillis();
            double fileReadTimeInseconds = getTimeDiffInSeconds(fileReadEndTime, fileReadStartTime);
            System.out.println("Total time (in seconds) to read all files = " + fileReadTimeInseconds);
            List<String> fileContentsList = new ArrayList(fileContentsMap.values());

            long fileWriteStartTime = System.currentTimeMillis();

            mhFileWriter.writeBufferedUsingTreeMap(fileContentsTreeMap, 4 * (int) MEG);// UseThisToUseTreeMap

            long fileWriteEndTime = System.currentTimeMillis();
            double fileWriteTimeInseconds = getTimeDiffInSeconds(fileWriteEndTime, fileWriteStartTime);
            System.out.println("Total time (in seconds) to write files = " + fileWriteTimeInseconds);
            System.out.println("----------------------****************----------------------------------");
            System.out.println("Finished merging " + totalFilesCount + " log files. Merged log file is available at  " + mhFileWriter.getLogFilesOutputLocation() + "/" + mhFileWriter.getLogFilesOutputName());
            System.out.println("----------------------****************----------------------------------");
            System.out.println("----------------------****************----------------------------------");
        }
    }

    private double getTimeDiffInSeconds(long endTime, long startTime) {
        return (endTime - startTime) / (1000.0);
    }
}
