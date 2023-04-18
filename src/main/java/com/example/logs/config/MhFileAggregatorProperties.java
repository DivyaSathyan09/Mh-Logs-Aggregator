package com.example.logs.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties
//@PropertySource("file:${secure.properties.location}/MhFileAggregator.properties") //Uncomment this line to run from command prompt
@PropertySource("classpath:MhFileAggregator.properties")//Uncomment this line to run from Eclipse
public class MhFileAggregatorProperties {

    //    @Value("${com.mhcure.logfiles.appfiletype}")
//    private final String typeAppFiles;
//    @Value("${COM.mhcure.logfiles.sipfiletype}")
//    private final String typeSipFiles;
//    @Value("${com.mhcure.logfiles.sipisfiletype}")
//    private final String typeSipisFiles;
//    @Value("${com.mhcure.logfiles.localpushfiletype}")
//    private final String typeLocalPushFiles;
    //	@Value("${secure.properties.location}")//Uncomment this line to run from command prompt
    private String mhFileAggregatorPropertiesLocation;
    @Value("${com.mhcure.logfiles.location}")
    private String logFilesLocation;

}
