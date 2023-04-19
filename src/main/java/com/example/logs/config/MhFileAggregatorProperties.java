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

	
//	@Value("${secure.properties.location}")//Uncomment this line to run from command prompt
	private String mhFileAggregatorPropertiesLocation;

	@Value("${com.mhcure.logfiles.location}")
	private String logfileslocation;
}
