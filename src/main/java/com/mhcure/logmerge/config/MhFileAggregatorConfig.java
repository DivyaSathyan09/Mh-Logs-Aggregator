package com.mhcure.logmerge.config;

import com.mhcure.logmerge.filereader.MhFileReader;
import com.mhcure.logmerge.filewriter.MhFileWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MhFileAggregatorConfig {
	
	@Bean
	public MhFileReader getMhFileReader() {
		return new MhFileReader();
	}

	@Bean
	public MhFileWriter getMhFileWriter() {
		return new MhFileWriter();
	}
}
