package com.mhcure.logs.config;

import com.mhcure.javatools.filewriter.MhFileWriter;
import com.mhcure.logmerge.filereader.MhFileReader;
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
