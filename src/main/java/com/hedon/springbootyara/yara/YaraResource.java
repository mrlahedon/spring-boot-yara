package com.hedon.springbootyara.yara;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

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

    // private ClassLoader classLoader = getClass().getClassLoader();
    // private final File RULE = new File(classLoader.getResource("example").getFile());
    // private final File cmd = new File(classLoader.getResource("yara64.exe").getFile());
    private final File RULE = new File("rules/example");
    private final File cmd = new File("yara64.exe");

    @Autowired
    private Yara yara;

    @GetMapping("/scan/{file}")
    public List<Map<String, String>> resultScan(@PathVariable String file) throws URISyntaxException
    {
        // System.out.println(file);
        // System.out.println(cmd.getAbsolutePath());
		yara.addRule(RULE);
		List<Map<String, String>> result = yara.scan(storageService.load(file).toFile(), cmd);
        // storageService.deleteFile(file);
        return result;
    }
}
