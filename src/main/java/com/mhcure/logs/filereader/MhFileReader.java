package com.mhcure.javatools.filereader;

import com.mhcure.javatools.config.MhFileAggregatorProperties;
import com.mhcure.javatools.helper.MhFileAggregatoHelper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
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

    @Value("${com.mhcure.logfiles.location}")
    private String logFilesLocation;
    
    //@Value("${com.mhcure.decrypted.logfiles.location}")
    //private String decryptedFileLocation;

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
    @Value("${com.mhcure.logfiles.backslach}")
    private String backslash;
    @Value("${com.mhcure.logfiles.appfiletype}")
    private String appfiletype;
    @Value("${COM.mhcure.logfiles.sipfiletype}")
    private String sipfiletype;
    @Value("${com.mhcure.logfiles.sipisfiletype}")
    private String sipisfiletype;
    @Value("${com.mhcure.logfiles.localpushfiletype}")
    private String localpushfiletype;
 @Value("${com.mhcure.logfiles.next-line}")
    private String NextLine;
    @Value("${com.mhcure.logfiles.locationis}")
    private String LogFileLocation;
    @Value("${com.mhcure.logfiles.validlocation}")
    private String ValidLocation;
    @Value("${com.mhcure.logfiles.notfound}")
    private String NoLogFilesFound;
    @Value("${com.mhcure.logfiles.bitwiseor}")
    private String BitwiseOr;

    public List<String> getFilesList() {
        List<String> fileList = new ArrayList<>();
        File logFilesLocationFile = new File(logFilesLocation);
        System.out.println(LogFileLocation + logFilesLocation);
        if (!logFilesLocationFile.exists()) {
            MhFileAggregatoHelper.printInstructionsOnConsole(logFilesLocation + ValidLocation);
            return fileList;
        }
        String[] logFilesArray = logFilesLocationFile.list();
        if (logFilesArray != null) {
            fileList = Arrays.asList(logFilesArray);
        } else {
            System.out.println(NoLogFilesFound + logFilesLocation);

        }
        return fileList;
    }

    public Map<Long, String> readFileUsingBufferedReader(String fileName) throws Exception {
        Map<Long, String> fileContentsMap = new HashMap<>();
        FileReader logFileReader = null;
        File logFile = new File(logFilesLocation + backslash + fileName);
        boolean isEncryptedFile = false;
        Cipher cipherObject = null;
        if(!logFile.isFile()) {
        	System.out.println(fileName + "is not a valid file. So skipping");
        	return fileContentsMap;
        }
    	logFileReader = new FileReader(logFile);
        if (MhFileAggregatoHelper.isFileEncrypted(fileName)) {
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
        	if(isEncryptedFile) {
        		line = getDecryptedText(line, cipherObject);
        	}
            Long keyForLine = null;
            boolean lineAddedToMap = false;
            // System.out.println(line);
            String lineToBeInserted = new StringTokenizer(fileName.replaceAll("_"," ")).nextToken() + BitwiseOr + line;
            if (line != null && line.length() > logDateTimePatternLength) {
                String dateTimePart = line.substring(0, logDateTimePatternLength);

                Matcher appLogTypeMatcher = logTypePattern.matcher(dateTimePart);
                if (appLogTypeMatcher.find()) {
                    boolean lineStartsWithDatePattern = appLogTypeMatcher.start() == 0;
                    long dateTimeInMilliSeconds = getTimeInMilliSeconds(dateTimePart, dateTimeFormatInLogFile);
                    keyForLine = dateTimeInMilliSeconds;
                    if (fileContentsMap.get(keyForLine) != null) {
                        //Since timestamp can be duplicated in a same file
                        fileContentsMap.put(keyForLine, fileContentsMap.get(keyForLine) + NextLine + lineToBeInserted);
                    } else {
                        fileContentsMap.put(keyForLine,  lineToBeInserted);
                    }
                    lineAddedToMap = true;
                }
            }
            //if line don't have datetimepart then append to prev line
            if (!lineAddedToMap &&
                    keyForPreviousLine != null &&
                    fileContentsMap.get(keyForPreviousLine) != null) {
                fileContentsMap.put(keyForPreviousLine, fileContentsMap.get(keyForPreviousLine) + lineToBeInserted);
            }

            if (keyForLine != null) {
                keyForPreviousLine = keyForLine;
            }
            lineAddedToMap = false;
            lineCounter++;
        }
        if(logFileReader != null) {
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
        final byte[] encryptionKey = "s!yAqepesPUtrerePU@Wutr!nAgU5_#h".getBytes();
        final String ALGO = "AES";
        Cipher cipherObject = null;
        Key key = null;
            if (key == null)
                key = new SecretKeySpec(encryptionKey, "AES");
            if (cipherObject == null) {
                cipherObject = Cipher.getInstance("AES");
                cipherObject.init(Cipher.DECRYPT_MODE, key);
            }
        return cipherObject;
    }
    

//    private Key generateKey() throws Exception {
//        Key key = new SecretKeySpec(encryptionKey, "AES");
//        return key;
//    }

    private long getTimeInMilliSeconds(String dateTimeText, String dateTimeFormatInLogFile) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(dateTimeFormatInLogFile);
        LocalDateTime parsedTimeStamp = LocalDateTime.parse(dateTimeText, dateFormat);
        return ZonedDateTime.of(parsedTimeStamp, ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    private String getDateTimeFormatInLogFile(String fileName) {
        if (fileName.toLowerCase().startsWith("App_")) {
            return appLogDateTimeFormat;
        } else if (fileName.startsWith("SIP_")) {
            return sipLogDateTimeFormat;
        } else if (fileName.startsWith("SIPIS_")) {
            return sipisLogDateTimeFormat;
        } else if (fileName.startsWith("LOCALPUSH_")) {
            return localPushLogDateTimeFormat;
        }
        return appLogDateTimeFormat;
    }

    private String getDateTimeRegexPatternInLogFile(String fileName) {
        if (fileName.toLowerCase().startsWith("App_")) {
            return appLogDateTimePatternRegexText;
        } else if (fileName.startsWith("SIP_")) {
            return sipLogDateTimePatternRegexText;
        } else if (fileName.startsWith("SIPIS_")) {
            return sipsLogDateTimePatternRegexText;
        } else if (fileName.startsWith("LOCALPUSH_")) {
            return localPushLogDateTimePatternRegexText;
        }
        return appLogDateTimePatternRegexText;
    }
}
