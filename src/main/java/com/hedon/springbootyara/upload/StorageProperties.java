package com.hedon.springbootyara.upload;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;
// import org.springframework.stereotype.Component;

@ConfigurationProperties("storage")
// @Component
@Getter
@Setter
public class StorageProperties  {

    /**
	 * Folder location for storing files
	 */
	private String location = "upload-dir";
    
}
