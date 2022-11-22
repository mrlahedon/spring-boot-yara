package com.hedon.springbootyara.yara;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class YaraResource {
    // private static final File RULE = new File("src/main/resources/my_first_rule");
    private static final File RULE = new File("src/main/resources/example");
	private static final File TARGET = new File("src/main/resources/scan_dummy");
    // private static final File RULE = new File("E:\\Yara\\my_first_rule");
	// private static final File TARGET = new File("E:\\Yara\\scan_dummy");
    private static final File cmd = new File("src/main/resources/yara64.exe");

    @Autowired
    private Yara yara;

    @GetMapping("/scan")
    public String resultScan()
    {
        // Yara yara = new Yara();
		yara.addRule(RULE);
		String result = yara.scan(TARGET, cmd);
		// assertThat(result, CoreMatchers.startsWith("dummy"));
        return result;
    }
}
