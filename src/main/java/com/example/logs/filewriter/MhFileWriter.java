package com.example.logs.filewriter;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;

@Data
public class MhFileWriter {

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

	    } finally {
	        // comment this out if you want to inspect the files afterward
	       // file.delete();
	    }
	}

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
    }
}
