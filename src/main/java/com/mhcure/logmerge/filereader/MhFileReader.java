package com.mhcure.logmerge.filereader;

import com.mhcure.logmerge.config.MhFileAggregatorProperties;
import com.mhcure.logmerge.constants.MhFileConstants;
import com.mhcure.logmerge.helper.MhFileAggregatorHelper;
import com.mhcure.logmerge.utils.MhMessagePropertiesFileReader;
import com.mhcure.logmerge.utils.MhMessageKeyEnum;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class MhFileReader {

    @Autowired
    MhFileAggregatorProperties mhFileAggregatorProperties;
    @Value("${logfiles.location}")
    private String logFilesLocation;
    @Value("${com.mhcure.logfiles.APP.log.dateTime.pattern}")
    private String appLogDateTimePatternRegexText;
    @Value("${com.mhcure.logfiles.APP.log.dateTime.format}")
    private String appLogDateTimeFormat;
    @Value("${com.mhcure.logfiles.SIP.log.dateTime.pattern}")
    private String sipLogDateTimePatternRegexText;
    @Value("${com.mhcure.logfiles.SIP.log.dateTime.format}")
    private String sipLogDateTimeFormat;
    @Value("${com.mhcure.logfiles.SIPIS.log.dateTime.pattern}")
    private String sipsLogDateTimePatternRegexText;
    @Value("${com.mhcure.logfiles.SIPIS.log.dateTime.format}")
    private String sipisLogDateTimeFormat;
    @Value("${com.mhcure.logfiles.LOCALPUSH.log.dateTime.pattern}")
    private String localPushLogDateTimePatternRegexText;
    @Value("${com.mhcure.logfiles.LOCALPUSH.log.dateTime.format}")
    private String localPushLogDateTimeFormat;
    @Value("${APP_files_prefix}")
    private String appFilePrefix;
    @Value("${SIP_files_prefix}")
    private String sipFilePrefix;
    @Value("${SIPIS_files_prefix}")
    private String sipisFilePrefix;
    @Value("${LOCALPUSH_files_prefix}")
    private String localPushFilePrefix;
    @Value("${com.mhcure.logfiles.decrypted.dateTime.format}")
    private String decryptedDateTimeFormat;
    @Value("${com.mhcure.logfiles.decrypted.dateTime.pattern}")
    private String decryptedDateTimePattern;
    @Value("${logfiles.encryptionKey}")
    private String encryptionKey;
    @Value("${com.mhcure.logfiles.encryptedFileExtension}")
    private String encryptFileExtension;

    public List<String> getFilesList() {
        List<String> fileList = new ArrayList<>();
        File logFilesLocationFile = new File(logFilesLocation);
        MhFileAggregatorHelper.printToConsole(MhFileConstants.USER_PROMPT_SPACE);
        MhFileAggregatorHelper.printToConsole(MhMessagePropertiesFileReader.getMessage
                (MhMessageKeyEnum.MESSAGE_LOG_FILES_LOCATION.getKey()).replace("logFolderLocation", logFilesLocation));
        if (!logFilesLocationFile.exists()) {
            MhFileAggregatorHelper.printToConsole(logFilesLocation + MhMessagePropertiesFileReader.
                    getMessage(MhMessageKeyEnum.MESSAGE_INVALID_FILE_LOCATION.getKey()));
            return fileList;
        }
        String[] logFilesArray = logFilesLocationFile.list();
        if (logFilesArray != null) {
            fileList = Arrays.asList(logFilesArray);
        } else {
            MhFileAggregatorHelper.printToConsole(MhMessagePropertiesFileReader
                    .getMessage(MhMessageKeyEnum.MESSAGE_NO_FILES_FOUND.getKey()) + logFilesLocation);
        }
        return fileList;
    }

    public Map<Long, String> readFileUsingBufferedReader(String fileName) throws Exception {
        Map<Long, String> fileContentsMap = new HashMap<>();
        FileReader logFileReader = null;
        File logFile = new File(logFilesLocation + MhFileConstants.BACKSLASH + fileName);
        boolean isEncryptedFile = false;
        Cipher cipherObject = null;
        if (!logFile.isFile()) {
            System.out.println(fileName + MhMessagePropertiesFileReader.getMessage(MhMessageKeyEnum.INVALID_INPUT_TO_SAVE_DECRYPTED_FILE.getKey()));
            return fileContentsMap;
        }
        logFileReader = new FileReader(logFile);
        if (MhFileAggregatorHelper.isFileEncrypted(fileName)) {
            isEncryptedFile = true;
            cipherObject = getCipherObject();
            //logFileReader = new FileReader(decryptedFileLocation + backslash + fileName);
        }
        String dateTimeFormatInLogFile = getDateTimeFormatInLogFile(fileName);
        String dateTimeRegexPatternInLogFile = getDateTimeRegexPatternInLogFile(fileName);
        int logDateTimePatternLength = dateTimeFormatInLogFile.length();
        Pattern logTypePattern = Pattern.compile(dateTimeRegexPatternInLogFile);
        BufferedReader br = new BufferedReader(logFileReader);
        int lineCounter = 0;
        Long keyForPreviousLine = null;
        for (String line; (line = br.readLine()) != null; ) {
            if (isEncryptedFile) {
                line = getDecryptedText(line, cipherObject);
            }
            Long keyForLine = null;
            boolean lineAddedToMap = false;
            String lineToBeInserted = new StringTokenizer(fileName.replaceAll("_", " "))
                    .nextToken() + MhFileConstants.FILENAME_LOGS_TMT_SEPARATOR + line;
            if (line != null && line.length() > logDateTimePatternLength) {
                String dateTimePart = line.substring(0, logDateTimePatternLength);

                Matcher appLogTypeMatcher = logTypePattern.matcher(dateTimePart);
                if (appLogTypeMatcher.find()) {
                    boolean lineStartsWithDatePattern = appLogTypeMatcher.start() == 0;
                    long dateTimeInMilliSeconds = getTimeInMilliSeconds(dateTimePart, dateTimeFormatInLogFile);
                    keyForLine = dateTimeInMilliSeconds;
                    if (fileContentsMap.get(keyForLine) != null) {
                        //Since timestamp can be duplicated in a same file
                        fileContentsMap.put(keyForLine, fileContentsMap.get(keyForLine) + MhFileConstants
                                .NEW_LINE_CHAR + lineToBeInserted);
                    } else {
                        fileContentsMap.put(keyForLine, lineToBeInserted);
                    }
                    lineAddedToMap = true;
                }
            }
            //if line don't have datetimepart then append to prev line
            if (!lineAddedToMap && keyForPreviousLine != null && fileContentsMap.get(keyForPreviousLine) != null) {
                fileContentsMap.put(keyForPreviousLine, fileContentsMap.get(keyForPreviousLine) + lineToBeInserted);
            }

            if (keyForLine != null) {
                keyForPreviousLine = keyForLine;
            }
            lineAddedToMap = false;
            lineCounter++;
        }
        if (logFileReader != null) {
            logFileReader.close();
        }
        br.close();
        return fileContentsMap;
    }

    public String getDecryptedText(String encryptedData, Cipher cipherObject) throws Exception {
        byte[] decodedValue = (Base64.getDecoder().decode(encryptedData));
        byte[] decryptedLine = cipherObject.doFinal(decodedValue);
        return new String(decryptedLine);
    }

    private Cipher getCipherObject() throws Exception {
        final byte[] encryptionKey = getEncryptionKey().getBytes();
        final String ALGO = "AES";
        Cipher cipherObject = null;
        Key key = null;
        if (key == null) key = new SecretKeySpec(encryptionKey, "AES");
        if (cipherObject == null) {
            cipherObject = Cipher.getInstance("AES");
            cipherObject.init(Cipher.DECRYPT_MODE, key);
        }
        return cipherObject;
    }

    private long getTimeInMilliSeconds(String dateTimeText, String dateTimeFormatInLogFile) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(dateTimeFormatInLogFile);
        LocalDateTime parsedTimeStamp = LocalDateTime.parse(dateTimeText, dateFormat);
        return ZonedDateTime.of(parsedTimeStamp, ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    private String getDateTimeFormatInLogFile(String fileName) {
        if (fileName.startsWith(appFilePrefix)) {
            return fileName.endsWith(encryptFileExtension) ? decryptedDateTimeFormat : appLogDateTimeFormat;
        } else if (fileName.startsWith(sipFilePrefix)) {
            return sipLogDateTimeFormat;
        } else if (fileName.startsWith(sipisFilePrefix)) {
            return sipisLogDateTimeFormat;
        } else if (fileName.startsWith(localPushFilePrefix)) {
            return localPushLogDateTimeFormat;
        }
        return appLogDateTimeFormat;
    }

    private String getDateTimeRegexPatternInLogFile(String fileName) {
        if (fileName.startsWith(appFilePrefix)) {
            return fileName.endsWith(encryptFileExtension) ? decryptedDateTimePattern : appLogDateTimePatternRegexText;
        } else if (fileName.startsWith(sipFilePrefix)) {
            return sipLogDateTimePatternRegexText;
        } else if (fileName.startsWith(sipisFilePrefix)) {
            return sipsLogDateTimePatternRegexText;
        } else if (fileName.startsWith(localPushFilePrefix)) {
            return localPushLogDateTimePatternRegexText;
        }
        return appLogDateTimePatternRegexText;
    }
}
