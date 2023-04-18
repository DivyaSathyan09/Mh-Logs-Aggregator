package com.example.logs.filereader;

import com.example.logs.config.MhFileAggregatorProperties;
import com.example.logs.helper.MhFileAggregatoHelper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
//	@Value("${com.mhcure.logfiles.appfiletype}")
//	private static final String typeAppFiles = null;
//	@Value("${COM.mhcure.logfiles.sipfiletype}")
//	private static final String typeSipFiles = null;
//	@Value("${com.mhcure.logfiles.sipisfiletype}")
//	private static final String typeSipisFiles = null;
//	@Value("${com.mhcure.logfiles.localpushfiletype}")
//	private static final String typeLocalPushFiles = null;

	public List<String> getFilesList() {
		List<String> fileList = new ArrayList<>();
		File logFilesLocationFile = new File(logFilesLocation);
		System.out.println("Log files location is " + logFilesLocation);
		if(!logFilesLocationFile.exists()) {
			MhFileAggregatoHelper.printInstructionsOnConsole(logFilesLocation + " is invalid. Pls specify a valid location");
			return fileList;
		}
		String[] logFilesArray = logFilesLocationFile.list();
		if(logFilesArray != null) {
			fileList = Arrays.asList(logFilesArray);
		} else {
			System.out.println("No log files found at " + logFilesLocation );
			
		}
		return fileList;
	}

	public Map<Long, String> readFileUsingBufferedReader(String fileName) throws IOException, ParseException {
		Map<Long, String> fileContentsMap  = new HashMap<>();
		String dateTimeFormatInLogFile = getDateTimeFormatInLogFile(fileName);
		String dateTimeRegexPatternInLogFile = getDateTimeRegexPatternInLogFile(fileName);
		
		int logDateTimePatternLength = dateTimeFormatInLogFile.length();
		Pattern logTypePattern = Pattern.compile(dateTimeRegexPatternInLogFile);
		FileReader logFileReader = new FileReader(new File(logFilesLocation + backslash+ fileName));
		BufferedReader br = new BufferedReader(logFileReader);
		 int lineCounter = 0;
		 Long keyForPreviousLine = null;
			for (String line; (line = br.readLine()) != null;) {
				 Long keyForLine = null;
				 boolean lineAddedToMap = false;
				// System.out.println(line);
				 String lineToBeInserted = fileName + " | " + line;
				if(line != null && line.length() > logDateTimePatternLength) {
					String dateTimePart = line.substring(0, logDateTimePatternLength);
					
					 Matcher appLogTypeMatcher = logTypePattern.matcher(dateTimePart);
						if (appLogTypeMatcher.find()) {
							boolean lineStartsWithDatePattern = appLogTypeMatcher.start() == 0;
							//System.out.println(lineStartsWithDatePattern);
							long dateTimeInMilliSeconds = getTimeInMilliSeconds(dateTimePart, dateTimeFormatInLogFile);
							keyForLine = dateTimeInMilliSeconds ;
							//System.out.println(dateTimePart);
							if(fileContentsMap.get(keyForLine) != null) {
								//Since timestamp can be duplicated in a same file
								fileContentsMap.put(keyForLine, fileContentsMap.get(keyForLine) + "\n" + lineToBeInserted);
							} else {
                   								fileContentsMap.put(keyForLine, "\n" + lineToBeInserted );
							}
							lineAddedToMap = true;
						}
				}
				 //if line don't have datetimepart then append to prev line
				if(lineAddedToMap == false && 
						keyForPreviousLine != null && 
						fileContentsMap.get(keyForPreviousLine) != null) {
					fileContentsMap.put(keyForPreviousLine, fileContentsMap.get(keyForPreviousLine) + lineToBeInserted);
				}

				if(keyForLine != null) {
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
		LocalDateTime parsedTimeStamp = LocalDateTime.parse(dateTimeText,dateFormat);
		return ZonedDateTime.of(parsedTimeStamp, ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

	private String getDateTimeFormatInLogFile(String fileName) {
		return switch (fileName) {
			case "App" -> appLogDateTimeFormat;
			case "SIP_" -> sipLogDateTimeFormat;
			case "SIPIS_" -> sipisLogDateTimeFormat;
			case "LOCALPUSH_" -> localPushLogDateTimeFormat;
			default ->appLogDateTimeFormat;
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
