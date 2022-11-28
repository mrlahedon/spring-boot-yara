package com.hedon.springbootyara;

// import java.io.File;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.hedon.springbootyara.upload.StorageProperties;
import com.hedon.springbootyara.upload.FileSystemStorageService;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class SpringBootYaraApplication {
	// uncomment for development
	// private ClassLoader classLoader = getClass().getClassLoader();
	// private final File cmd = new File(classLoader.getResource("yara64.exe").getFile());

	public static void main(String[] args) {
		SpringApplication.run(SpringBootYaraApplication.class, args);
	}

	@Bean
	CommandLineRunner init(FileSystemStorageService storageService) {
		return (args) -> {
			// storageService.deleteAll();
			storageService.init();
			// uncomment for development
			// storageService.copyFile(cmd.getAbsolutePath());
		};
	}

}
