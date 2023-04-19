package com.example.logs.filereader;

import com.example.logs.config.MhFileAggregatorProperties;
import com.example.logs.helper.MhFileAggregatoHelper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

<<<<<<< HEAD
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.Key;
=======
import java.io.*;
>>>>>>> 11b81af9446b9aee3f131ccf96db026d1bc6c76a
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

<<<<<<< HEAD
    @Autowired
    MhFileAggregatorProperties mhFileAggregatorProperties;

    @Value("${com.mhcure.logfiles.location}")
    private String logFilesLocation;
    @Value("${com.mhcure.decrypted.logfiles.location}")
    private String decryptedFileLocation;

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

    public Map<Long, String> readFileUsingBufferedReader(String fileName) throws IOException, ParseException {
        FileReader logFileReader;
        if (fileName.toLowerCase().endsWith(".encrypted")) {
            decryptFile(fileName, fileName.substring(0, fileName.lastIndexOf(".")));
            logFileReader = new FileReader(decryptedFileLocation + backslash + fileName.substring(0, fileName.lastIndexOf(".")));
        } else {
            logFileReader = new FileReader(logFilesLocation + backslash + fileName);
        }
        String dateTimeFormatInLogFile = getDateTimeFormatInLogFile(fileName);
        String dateTimeRegexPatternInLogFile = getDateTimeRegexPatternInLogFile(fileName);
        Map<Long, String> fileContentsMap = new HashMap<>();
        int logDateTimePatternLength = dateTimeFormatInLogFile.length();
        Pattern logTypePattern = Pattern.compile(dateTimeRegexPatternInLogFile);
        BufferedReader br = new BufferedReader(logFileReader);
        int lineCounter = 0;
        Long keyForPreviousLine = null;
        for (String line; (line = br.readLine()) != null; ) {
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
        logFileReader.close();
        br.close();
        return fileContentsMap;
    }

    public String performDecryption(String encryptedData) {
        final byte[] encryptionKey = "s!yAqepesPUtrerePU@Wutr!nAgU5_#h".getBytes();
        final String ALGO = "AES";
        Cipher c = null;
        Key key = null;
        try {
            if (key == null)
                key = new SecretKeySpec(encryptionKey, "AES");
            if (c == null) {
                c = Cipher.getInstance("AES");
                c.init(Cipher.DECRYPT_MODE, key);
            }
        } catch (Exception e) {
            System.out.println("Exception initializing encryption code - check if you have Java JCE installed correctly!");
            e.printStackTrace();

        }
        try {
            byte[] decodedValue = (Base64.getDecoder().decode(encryptedData));
            byte[] decValue = c.doFinal(decodedValue);
            return new String(decValue);
        } catch (Exception exception) {
            return encryptedData;
        }
    }

    public void decryptFile(String sourceFileName, String destinationFileName) {
        BufferedReader br = null;
        PrintWriter pw = null;
        try {
            br = new BufferedReader(new FileReader(logFilesLocation + backslash + sourceFileName));
            pw = new PrintWriter(new FileWriter(decryptedFileLocation + backslash + destinationFileName, false));
            String line;
            while ((line = br.readLine()) != null)
                pw.println(performDecryption(line));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (Exception e) {
            }
            try {
                pw.close();
            } catch (Exception e) {
            }
        }
        //return destinationFileName;
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
=======
	@Autowired
	MhFileAggregatorProperties mhFileAggregatorProperties;

	@Value("${com.mhcure.logfiles.location}")
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
	@Value("${com.mhcure.logfiles.backslash}")
	private String backslash;
	@Value("${com.mhcure.logfiles.appfiletype}")
	private String appfiletype;
	@Value("${com.mhcure.logfiles.sipfiletype}")
	private String sipfiletype;
	@Value("${com.mhcure.logfiles.sipisfiletype}")
	private String sipisfiletype;
	@Value("${com.mhcure.logfiles.localpushfiletype}")
	private String localpushfiletype;
	@Value("${com.mhcure.logfiles.newLineChar")
	private String newlinechar;
	@Value("${com.mhcure.userPrompt.message.locationis}")
	private String logfilelocation;
	@Value("${com.mhcure.userInfo.message.validlocation}")
	private String validlocation;
	@Value("${com.mhcure.userInfo.message.notfound}")
	private String nologfilesfound;
	@Value("${com.mhcure.logfiles.bitwiseor}")
	private String seperator;

	public List<String> getFilesList() {
		List<String> fileList = new ArrayList<>();
		File logFilesLocationFile = new File(logFilesLocation);
		System.out.println(logfilelocation + logFilesLocation);
		if (!logFilesLocationFile.exists()) {
			MhFileAggregatoHelper.printInstructionsOnConsole(logFilesLocation + validlocation);
			return fileList;
		}
		String[] logFilesArray = logFilesLocationFile.list();
		if (logFilesArray != null) {
			fileList = Arrays.asList(logFilesArray);
		} else {
			System.out.println(nologfilesfound + logFilesLocation);

		}
		return fileList;
	}

	public Map<Long, String> readFileUsingBufferedReader(String fileName) throws IOException, ParseException {
		Map<Long, String> fileContentsMap = new HashMap<>();
		String dateTimeFormatInLogFile = getDateTimeFormatInLogFile(fileName);
		String dateTimeRegexPatternInLogFile = getDateTimeRegexPatternInLogFile(fileName);

		int logDateTimePatternLength = dateTimeFormatInLogFile.length();
		Pattern logTypePattern = Pattern.compile(dateTimeRegexPatternInLogFile);
		FileReader logFileReader = new FileReader(new File(logFilesLocation + backslash + fileName));
		BufferedReader br = new BufferedReader(logFileReader);
		int lineCounter = 0;
		Long keyForPreviousLine = null;
		for (String line; (line = br.readLine()) != null; ) {
			Long keyForLine = null;
			boolean lineAddedToMap = false;
//				System.out.println(line);
			String lineToBeInserted = fileName + seperator + line;
			if (line != null && line.length() > logDateTimePatternLength) {
				String dateTimePart = line.substring(0, logDateTimePatternLength);

				Matcher appLogTypeMatcher = logTypePattern.matcher(dateTimePart);
				if (appLogTypeMatcher.find()) {
					boolean lineStartsWithDatePattern = appLogTypeMatcher.start() == 0;
					long dateTimeInMilliSeconds = getTimeInMilliSeconds(dateTimePart, dateTimeFormatInLogFile);
					keyForLine = dateTimeInMilliSeconds;
					if (fileContentsMap.get(keyForLine) != null) {
						//Since timestamp can be duplicated in a same file
						fileContentsMap.put(keyForLine, fileContentsMap.get(keyForLine) + newlinechar + lineToBeInserted);
					} else {
						fileContentsMap.put(keyForLine, newlinechar + lineToBeInserted);
					}
					lineAddedToMap = true;
				}
			}
			//if line don't have datetimepart then append to prev line
			if (lineAddedToMap == false &&
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
		logFileReader.close();
		br.close();
		return fileContentsMap;
	}

	private long getTimeInMilliSeconds(String dateTimeText, String dateTimeFormatInLogFile) {
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(dateTimeFormatInLogFile);
		LocalDateTime parsedTimeStamp = LocalDateTime.parse(dateTimeText, dateFormat);
		return ZonedDateTime.of(parsedTimeStamp, ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

	private String getDateTimeFormatInLogFile(String fileName) {
		return switch (fileName) {
			case "App" -> appLogDateTimeFormat;
			case "SIP_" -> sipLogDateTimeFormat;
			case "SIPIS_" -> sipisLogDateTimeFormat;
			case "LOCALPUSH_" -> localPushLogDateTimeFormat;
			default -> appLogDateTimeFormat;
		};
	}

	private String getDateTimeRegexPatternInLogFile(String fileName) {
		return switch (fileName) {
			case "App" -> appLogDateTimePatternRegexText;
			case "SIP_" -> sipLogDateTimePatternRegexText;
			case "SIPIS_" -> sipsLogDateTimePatternRegexText;
			case "LOCALPUSH_" -> localPushLogDateTimePatternRegexText;
			default -> appLogDateTimePatternRegexText;
		};
	}
}
>>>>>>> 11b81af9446b9aee3f131ccf96db026d1bc6c76a
