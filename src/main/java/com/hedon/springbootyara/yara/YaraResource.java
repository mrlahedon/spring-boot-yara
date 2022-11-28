package com.hedon.springbootyara.yara;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hedon.springbootyara.upload.FileSystemStorageService;

@RestController
@RequestMapping("/api")
public class YaraResource {

    @Autowired
    private FileSystemStorageService storageService;

    private ClassLoader classLoader = getClass().getClassLoader();
    // private static final File RULE = new File("src/main/resources/my_first_rule");
    // private static final File RULE = new File("src/main/resources/example");
	// private static final File TARGET = new File("src/main/resources/scan_dummy");
    // private static final File RULE = new File("E:\\Yara\\my_first_rule");
	// private static final File TARGET = new File("E:\\Yara\\scan_dummy");
    private final File RULE = new File(classLoader.getResource("example").getFile());
	// private final File TARGET = new File(classLoader.getResource("scan_dummy").getFile());
    // private final File cmd = new File("src/main/resources/yara64.exe");
    private final File cmd = new File(classLoader.getResource("yara64.exe").getFile());

    @Autowired
    private Yara yara;

    @GetMapping("/scan/{file}")
    public List<String> resultScan(@PathVariable String file) throws URISyntaxException
    {
        System.out.println(file);
        // Yara yara = new Yara();
		yara.addRule(RULE);
		List<String> result = yara.scan(storageService.load(file).toFile(), cmd);
		// assertThat(result, CoreMatchers.startsWith("dummy"));
        storageService.deleteFile(file);
        return result;
    }
}
