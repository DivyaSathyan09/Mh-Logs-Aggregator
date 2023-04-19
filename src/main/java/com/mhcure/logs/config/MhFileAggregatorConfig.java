package com.mhcure.javatools.config;

import com.mhcure.javatools.filereader.MhFileReader;
import com.mhcure.javatools.filewriter.MhFileWriter;
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
