package com.mhcure.logmerge.filewriter;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;

@Data
public class MhFileWriter {

	@Value("${com.mhcure.logfiles.location}")
	private String logFilesLocation;
	
	@Value("${com.mhcure.logfiles.output.location}")
	private String logFilesOutPutLocatiobn;

	@Value("${com.mhcure.logfiles.output.filename}")
	private String logFilesOutPutName;

	@Value("${com.mhcure.logfiles.APP.log.dateTime.pattern}")
	private String appLogDateTimePatternRegex;
	
	@Value("${com.mhcure.logfiles.APP.log.dateTime.format}")
	private String appLOgDateTimeFormat;
	@Value("${com.mhcure.logfiles.backslash}")
	private String backslash;
	@Value("${com.mhcure.userInfo.message.buffersize}")
	private String bufferSize;
	@Value("${com.mhcure.logfiles.output.location.for.decrypted.file}")
	private String decryptedFileLocation;


	private static final double MEG = (Math.pow(1024, 2));

	public void writeToFile(TreeMap<Long, String> fileContentsTreeMap) throws IOException {
		int bufSize =4 * (int) MEG;
		File file = new File(logFilesOutPutLocatiobn + backslash + logFilesOutPutName);
		// Display the TreeMap which is naturally sorted
		TreeMap<Long, String> sortedTreeMapWithFileLines = fileContentsTreeMap;
		FileWriter writer = new FileWriter(file);
		BufferedWriter bufferedWriter = new BufferedWriter(writer, bufSize);
		for (Map.Entry<Long, String> entry : sortedTreeMapWithFileLines.entrySet()) {
			writeLineToFile(entry.getValue(), bufferedWriter);
		}
		bufferedWriter.close();

		System.out.println(bufferSize + bufSize + ")... ");

	}

	public void writeToFile(String destinationFileName, Map<Long, String> singleFileContentsMap) throws IOException {
		int bufSize = 4 * (int) MEG;
		File file = new File(decryptedFileLocation + backslash + destinationFileName);
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

	// Function to sort map by Key
	public TreeMap<String, String> sortbykey(Map<String, String> fileContentsMap) {
		// TreeMap to store values of HashMap
		TreeMap<String, String> sortedTreeMapWithFileLines = new TreeMap<>();

		// Copy all data from hashMap into TreeMap
		sortedTreeMapWithFileLines.putAll(fileContentsMap);
		return sortedTreeMapWithFileLines;

	}
}
