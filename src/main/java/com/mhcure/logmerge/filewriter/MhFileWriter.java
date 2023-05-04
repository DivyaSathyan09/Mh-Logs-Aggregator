package com.mhcure.logmerge.filewriter;

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

    @Value("${logfiles.location}")
    private String logFilesLocation;

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
    private Integer numberOfLinesPerOutputfiles;

    private int fileCounter;

    public void writeToFile(TreeMap<Long, String> fileContentsTreeMap) throws IOException {
        int bufSize = 4 * (int) MEG;
        double countLines = 0;
        // Display the TreeMap which is naturally sorted
        TreeMap<Long, String> sortedTreeMapWithFileLines = fileContentsTreeMap;
        if (splitLogFilesIntoMultiple.equalsIgnoreCase(MhFileConstants.USER_PERMISSION_N0)) {
            saveInSingleFile(sortedTreeMapWithFileLines, bufSize);
            return;
        }
        MhFileAggregatorHelper.printToConsole(MhFileConstants.USER_PROMPT_SPACE);
        MhFileAggregatorHelper.printToConsole(MhMessagePropertiesFileReader.getMessage(MhMessageKeyEnum.ASK_MAXIMUM_LINES_IN_LOG_FILE.getKey()));
        MhFileAggregatorHelper.printToConsole(MhFileConstants.USER_PROMPT_SPACE);
        if (numberOfLinesPerOutputfiles <= 1) {
            saveInSingleFile(sortedTreeMapWithFileLines, bufSize);
            return;
        }
        File file = new File(logFilesOutPutLocation + MhFileConstants.BACKSLASH + logFilesOutPutName);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        for (Map.Entry<Long, String> entry : sortedTreeMapWithFileLines.entrySet()) {
            if (countLines == numberOfLinesPerOutputfiles) {
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
        File file = new File(logFilesOutPutLocation + MhFileConstants.BACKSLASH + logFilesOutPutName);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        for (Map.Entry<Long, String> entry : sortedTreeMapWithFileLines.entrySet()) {
            writeLineToFile(entry.getValue(), bufferedWriter);
        }
        bufferedWriter.close();
        MhFileAggregatorHelper.printToConsole(MhMessagePropertiesFileReader.getMessage(MhMessageKeyEnum.WRITE_BUFFERED_SIZE.getKey()) + bufSize + ")... ");
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
}
