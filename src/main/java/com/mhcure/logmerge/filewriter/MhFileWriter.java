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

    public void writeToFile(TreeMap<Long, String> fileContentsTreeMap) throws IOException {
        int bufSize = 4 * (int) MEG;
        double countLines = 0;
        File file = new File(logFilesOutPutLocation + MhFileConstants.BACKSLASH + logFilesOutPutName);
        // Display the TreeMap which is naturally sorted
        TreeMap<Long, String> sortedTreeMapWithFileLines = fileContentsTreeMap;
        FileWriter writer = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(writer, bufSize);
        for (Map.Entry<Long, String> entry : sortedTreeMapWithFileLines.entrySet()) {
            writeLineToFile(entry.getValue(), bufferedWriter);
            countLines++;
        }
        bufferedWriter.close();

       MhFileAggregatorHelper.printToConsole(MhMessagePropertiesFileReader.getMessage(MhMessageKeyEnum.WRITE_BUFFERED_SIZE.getKey()) + bufSize + ")... ");

    }

    public void writeToFile(String destinationFileName, Map<Long, String> singleFileContentsMap) throws IOException {
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
}
