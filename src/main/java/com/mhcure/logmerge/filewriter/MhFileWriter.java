package com.mhcure.logmerge.filewriter;

import com.mhcure.logmerge.config.MhFileAggregatorProperties;
import com.mhcure.logmerge.constants.MhFileConstants;
import com.mhcure.logmerge.helper.MhFileAggregatorHelper;
import com.mhcure.logmerge.utils.MhMessagePropertiesFileReader;
import com.mhcure.logmerge.utils.MhMessageKeyEnum;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;

@Data
public class MhFileWriter {

    private static final double MEG = (Math.pow(1024, 2));

    @Value("${logfiles.output.location}")
    private String logFilesOutPutLocation;

    @Value("${logfiles.output.filename}")
    private String logFilesOutPutName;

    @Value("${com.mhcure.logfiles.APP.log.dateTime.pattern}")
    private String appLogDateTimePatternRegex;

    @Value("${com.mhcure.logfiles.APP.log.dateTime.format}")
    private String appLOgDateTimeFormat;

    @Value("${generated_decrypted.files_location}")
    private String decryptedFileLocation;

    @Value("${split_log_files_into_multiple}")
    private String splitLogFilesIntoMultiple;

    @Value("${number_of_lines_per_outputfile}")
    private Integer numberOfLinesPerOutPutFiles;

    private int fileCounter;

    public void writeToFile(TreeMap<Long, String> fileContentsTreeMap) throws IOException {
        MhFileAggregatorHelper.printToConsole("!!!!!!!!!!!! inside writeToFile");

        int bufSize = 4 * (int) MEG;
        double countLines = 0;
        // Display the TreeMap which is naturally sorted
        TreeMap<Long, String> sortedTreeMapWithFileLines = fileContentsTreeMap;
        File logFilesOutPutFile = new File(logFilesOutPutLocation);
        MhFileAggregatorHelper.printToConsole("!!!!!!!!!!!! inside writeToFile " + logFilesOutPutFile.exists());

        if (!new File(logFilesOutPutLocation).exists()) {
            MhFileAggregatorHelper.printToConsole(MhFileConstants.USER_PROMPT_SPACE);
            MhFileAggregatorHelper.printToConsole(MhMessagePropertiesFileReader.getMessage(MhMessageKeyEnum.INVALID_LOGFILE_OUTPUT_LOCATION.getKey()));
            MhFileAggregatorHelper.printToConsole(MhFileConstants.USER_PROMPT_SPACE);
            MhFileAggregatorHelper.printToConsole("!!!!!!!!!!!! inside writeToFile return from here");
            return;
        }
        if (splitLogFilesIntoMultiple.equalsIgnoreCase(MhFileConstants.USER_PERMISSION_N0)) {
            MhFileAggregatorHelper.printToConsole("!!!!!!!!!!!! inside writeToFile saving single file");
            saveInSingleFile(sortedTreeMapWithFileLines, bufSize);
            MhFileAggregatorHelper.printToConsole("!!!!!!!!!!!! inside writeToFile saving single file over and returning");
            return;
        }
        MhFileAggregatorHelper.printToConsole("!!!!!!!!!!!! inside writeToFile should not have reached here");

        if (numberOfLinesPerOutPutFiles <= 1) {
            saveInSingleFile(sortedTreeMapWithFileLines, bufSize);
            return;
        }
        File file = new File(logFilesOutPutLocation + MhFileConstants.BACKSLASH + logFilesOutPutName);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        for (Map.Entry<Long, String> entry : sortedTreeMapWithFileLines.entrySet()) {
            if (countLines == numberOfLinesPerOutPutFiles) {
                bufferedWriter = createNewFile();
                countLines = 0;
            }
            writeLineToFile(entry.getValue(), bufferedWriter);
            countLines++;

        }
        bufferedWriter.close();
        MhFileAggregatorHelper.printToConsole(MhFileConstants.USER_PROMPT_SPACE);
        MhFileAggregatorHelper.printToConsole(MhMessagePropertiesFileReader.getMessage(MhMessageKeyEnum.WRITE_BUFFERED_SIZE.getKey()) + bufSize + ")... ");
    }

    private void saveInSingleFile(TreeMap<Long, String> sortedTreeMapWithFileLines, int bufSize) throws IOException {
        try {
            File outputFileWithFullPath = new File(logFilesOutPutLocation + MhFileConstants.BACKSLASH + logFilesOutPutName);
            System.out.println("outputFileWithFullPath is writable " + outputFileWithFullPath.canWrite());
            //check if this file exists. if yes, then make it writable.
            //else create a e=new file and make it writable.
            createAndGrantPermissionsForFile(outputFileWithFullPath);
            FileWriter fileWriter = new FileWriter(outputFileWithFullPath);
            System.out.println("starting write outputFileWithFullPath");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            int lineCount = 1;
            for (Map.Entry<Long, String> entry : sortedTreeMapWithFileLines.entrySet()) {
                System.out.println("In for loop for " + lineCount++);
                writeLineToFile(entry.getValue(), bufferedWriter);
            }
            System.out.println("for loop over");
//            bufferedWriter.flush();
            bufferedWriter.close();
//            fileWriter.flush();
            fileWriter.close();
            System.out.println("Closed outputFileWithFullPath handlers");

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("************** Done");

        MhFileAggregatorHelper.printToConsole(MhMessagePropertiesFileReader.getMessage(MhMessageKeyEnum.WRITE_BUFFERED_SIZE.getKey()) + bufSize + ")... ");
        System.out.println("***************** Done 1");
    }

    public void writeDecryptedFile(String destinationFileName, Map<Long, String> singleFileContentsMap) throws IOException {
        int bufSize = 4 * (int) MEG;
        File file = new File(decryptedFileLocation + MhFileConstants.BACKSLASH + destinationFileName);
        FileWriter writer = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(writer, bufSize);
        for (Map.Entry<Long, String> entry : singleFileContentsMap.entrySet()) {
            writeLineToFile(entry.getValue(), bufferedWriter);
        }
        bufferedWriter.close();
    }

    private void writeLineToFile(String record, Writer writer) throws IOException {
        long start = System.currentTimeMillis();
        writer.write(record);
        long end = System.currentTimeMillis();
    }

    private BufferedWriter createNewFile() throws IOException {
        fileCounter++;
        File file = new File(logFilesOutPutLocation + MhFileConstants.BACKSLASH + logFilesOutPutName + fileCounter);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        return bufferedWriter;
    }

    private void createAndGrantPermissionsForFile(File outputFileWithFullPath) throws IOException {
        MhFileAggregatorHelper.printToConsole("!!!!!!!!!!!!create and grant permission!!!!!   , " + outputFileWithFullPath.getAbsolutePath() + " , "+ outputFileWithFullPath.getPath() + " , " + outputFileWithFullPath.getCanonicalPath() + " , " +outputFileWithFullPath.exists() );
        if (outputFileWithFullPath.exists()) {
            MhFileAggregatorHelper.printToConsole("!!!!!!!file exists!!!!!!!");
        } else {
            boolean value = outputFileWithFullPath.createNewFile();
            MhFileAggregatorHelper.printToConsole("!!!!!!!!!!!!after creating  and grant permission!!!!!   , " + outputFileWithFullPath.getAbsolutePath() + " , "+ outputFileWithFullPath.getPath() + " , " + outputFileWithFullPath.getCanonicalPath() + " , " +outputFileWithFullPath.exists() );
            MhFileAggregatorHelper.printToConsole("!!!!!!!!created new file!!!!!!");
        }
        outputFileWithFullPath.setWritable(true);
    }
}

