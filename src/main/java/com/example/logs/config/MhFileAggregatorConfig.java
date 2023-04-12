package com.example.logs.config;

import com.example.logs.filereader.MhFileReader;
import com.example.logs.filewriter.MhFileWriter;
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
