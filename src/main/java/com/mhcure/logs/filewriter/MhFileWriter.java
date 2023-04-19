package com.example.logs.filewriter;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;

@Data
public class MhFileWriter {

<<<<<<< HEAD
    @Value("${com.mhcure.logfiles.location}")
    private String logFilesLocation;

    @Value("${com.mhcure.logfiles.output.location}")
    private String logFilesOutputLocation;

    @Value("${com.mhcure.logfiles.output.filename}")
    private String logFilesOutputName;

    //@Value("${com.mhcure.logfiles.APPlogType.dateTime.pattern}")
    private String appLogDateTimePaternRegexText;

    @Value("${com.mhcure.logfiles.APP.log.dateTime.format}")
    private String appLogDateTimeFormat;
    @Value("${com.mhcure.logfiles.backslach}")
    private String backslash;
    @Value("${com.mhcure.logfiles.buffersize}")
    private String bufferSize;


    public void writeBufferedUsingTreeMap(TreeMap<Long, String> fileContentsTreeMap, int bufSize) throws IOException {
     int countLines = 0;
        File file = new File(logFilesOutputLocation + backslash + logFilesOutputName);
        // Display the TreeMap which is naturally sorted
        TreeMap<Long, String> sortedTreeMapWithFileLines = fileContentsTreeMap;//sortbykey(fileContentsTreeMap);
		FileWriter writer = new FileWriter(file);
		BufferedWriter bufferedWriter = new BufferedWriter(writer, bufSize);
		for (Map.Entry<Long, String> entry : sortedTreeMapWithFileLines.entrySet()) {
            countLines++;
            if (countLines > 20000){
                createNewOutputFile(fileContentsTreeMap);
            }
			writeLineToFile(entry.getValue(), bufferedWriter);
		}
		bufferedWriter.close();

		System.out.println(bufferSize + bufSize + ")... ");
=======
	@Value("${com.mhcure.logfiles.location}")
	private String logfileslocation;
	
	@Value("${com.mhcure.logfiles.output.location}")
	private String logfilesoutputlocation;

	@Value("${com.mhcure.logfiles.output.filename}")
	private String logfilesoutputname;

	@Value("${com.mhcure.logfiles.APP.log.dateTime.pattern}")
	private String applogdatetimepaternregextext;
	
	@Value("${com.mhcure.logfiles.APP.log.dateTime.format}")
	private String applogdatetimeformat;
	@Value("${com.mhcure.logfiles.backslash}")
	private String backslash;
	@Value("${com.mhcure.userInfo.message.buffersize}")
	private String buffersize;


	public void writeBufferedUsingTreeMap( TreeMap<Long, String> fileContentsTreeMap , int bufSize) throws IOException {
	    File file = new File(logfilesoutputlocation + backslash + logfilesoutputname);
	    // Display the TreeMap which is naturally sorted
	    TreeMap<Long, String> sortedTreeMapWithFileLines = fileContentsTreeMap;//sortbykey(fileContentsTreeMap);
	    try {
	        FileWriter writer = new FileWriter(file);
	        BufferedWriter bufferedWriter = new BufferedWriter(writer, bufSize);
	        for (Map.Entry<Long, String> entry : sortedTreeMapWithFileLines.entrySet()) {
		        writeLineToFile(entry.getValue(), bufferedWriter);
		    }
	        bufferedWriter.close();
		    
		        System.out.println(buffersize + bufSize + ")... ");
>>>>>>> 11b81af9446b9aee3f131ccf96db026d1bc6c76a

	}
    private TreeMap<Long, String> createNewOutputFile(TreeMap<Long, String> fileContentsTreeMap){

<<<<<<< HEAD
       return fileContentsTreeMap;
=======
	private void writeLineToFile(String record, Writer writer) throws IOException {
	    long start = System.currentTimeMillis();
	        writer.write(record);
	    long end = System.currentTimeMillis();
	}
	  // Function to sort map by Key
    public  TreeMap<String, String> sortbykey( Map<String, String> fileContentsMap)
    {
        // TreeMap to store values of HashMap
        TreeMap<String, String> sortedTreeMapWithFileLines = new TreeMap<>();
 
        // Copy all data from hashMap into TreeMap
        sortedTreeMapWithFileLines.putAll(fileContentsMap);
        return sortedTreeMapWithFileLines;
>>>>>>> 11b81af9446b9aee3f131ccf96db026d1bc6c76a
    }

    private void writeLineToFile(String record, Writer writer) throws IOException {
        long start = System.currentTimeMillis();
        writer.write(record);
        long end = System.currentTimeMillis();
    }

}
