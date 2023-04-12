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
		FileReader logFileReader = new FileReader(new File(logFilesLocation + "/" + fileName));
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
					  //System.out.println("Pattern: " + appLogTypeMatcher.pattern());
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
				} else {
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

	private long getTimeInMilliSeconds(String dateTimeText, String dateTimeFormatInLogFile) throws ParseException {
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(dateTimeFormatInLogFile);
		LocalDateTime parsedTimeStamp = LocalDateTime.parse(dateTimeText,dateFormat);
		return ZonedDateTime.of(parsedTimeStamp, ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

	private String getDateTimeFormatInLogFile(String fileName) {
		switch (fileName) {
			case "App" -> {
				checkLogFileFormat(fileName, "App");
				return appLogDateTimeFormat;
			}
			case "SIP_" -> {
				checkLogFileFormat(fileName, "SIP_");
				return sipLogDateTimeFormat;
			}
			case "SIPIS_" -> {
				checkLogFileFormat(fileName, "SIPIS_");
				return sipisLogDateTimeFormat;
			}
			case "LOCALPUSH_" -> {
				checkLogFileFormat(fileName, "LOCALPUSH_");
				return localPushLogDateTimeFormat;
			}
		}
		return appLogDateTimeFormat;
	}

	private String getDateTimeRegexPatternInLogFile(String fileName) {
		switch (fileName) {
			case "App" -> {
				checkLogFileFormat(fileName, "App");
				return appLogDateTimePatternRegexText;
			}
			case "SIP_" -> {
				checkLogFileFormat(fileName, "SIP_");
				return sipLogDateTimePatternRegexText;
			}
			case "SIPIS_" -> {
				checkLogFileFormat(fileName, "SIPIS_");
				return sipsLogDateTimePatternRegexText;
			}
			case "LOCALPUSH_" -> {
				checkLogFileFormat(fileName, "LOCALPUSH_");
				return localPushLogDateTimePatternRegexText;
			}
		}
		return appLogDateTimePatternRegexText;
	}

	private boolean checkLogFileFormat(String fileName, String fileType) {
		if(fileName.toUpperCase().startsWith(fileType)) {
			return true;
		}
		return false;
	}
}
