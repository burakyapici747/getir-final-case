package com.burakyapici.library;

import com.burakyapici.library.config.LibraryRulesConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties(LibraryRulesConfig.class)
@EnableAsync
@EnableScheduling
public class LibraryServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(LibraryServiceApplication.class, args);
	}
}
