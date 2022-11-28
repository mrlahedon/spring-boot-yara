package com.hedon.springbootyara.yara;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Yara {
    private static final Logger log = LoggerFactory.getLogger(Yara.class);
	private ProcessBuilder processBuilder = new ProcessBuilder();
	private List<File> rules = new ArrayList<File>();

	public Yara addRule(File rule) {
		log.info("add rule: " + rule.getName());
		rules.add(rule);
		return this;
	}

	public List<String> scan(File file, File cmd) {
		log.info("start scan file: " + file.getName());
		log.info("execute: " + cmd.getAbsolutePath() + " " + rules.get(0).getAbsolutePath() + " " + file.getAbsolutePath());

		List<String> command = new ArrayList<String>();
        command.add(cmd.getAbsolutePath());
		command.add(rules.get(0).getAbsolutePath());
		command.add(file.getAbsolutePath());

		// processBuilder.command("/bin/bash", "-c",
		// processBuilder.command(cmd.getAbsolutePath() + " " + rules.get(0).getAbsolutePath() + " " + file.getAbsolutePath());
		// ProcessBuilder builder = new ProcessBuilder(command);
		processBuilder.command(command);

		// return execute(file, cmd);
		return execute();
	}

	private List<String> execute() {
	// private String execute(File file, File cmd) {
		StringBuilder sb = new StringBuilder();
		List<String> result = new ArrayList<String>();
		
		try {
			Process process = processBuilder.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			
			while ((line = reader.readLine()) != null) {
				log.info(line);
				result.add(line);
				sb.append(line + System.lineSeparator());
			}
			
			reader.close();
			int exitCode = process.waitFor();

			if (sb != null && exitCode == 0) {
				sb.append(System.lineSeparator());
				printResult("result", sb.toString());
			}

			log.info("Exited with error code : " + exitCode);
		} catch (IOException e) {
			log.error("failed to execute yara", e);
		} catch (InterruptedException e) {
			log.error("execute interrupted", e);
		}
		

		/* Process process = null;
		try {
            process = Runtime.getRuntime().exec(cmd.getAbsolutePath() + " " + rules.get(0).getAbsolutePath() + " " + file.getAbsolutePath());
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;

			while ((line = br.readLine()) != null) {
				log.info(line);
				sb.append(line);
			}

			br.close();
			int exitCode = process.waitFor();
			log.info("Exited with error code : " + exitCode);
		} catch (IOException ex) {
			log.error("failed to execute yara", ex);
		} catch (InterruptedException ex) {
			log.error("execute interrupted", ex);
		} */
		
		log.info("Program terminated!");
		// return sb.toString();
		return result;
	}

	private static void printResult(String fileName, String result) {
		try {
			// File file = new File("src/main/resources/"+ fileName +".txt");
			File file = new File(fileName +".txt");
			FileWriter writer = new FileWriter(file, true);
			writer.write(result);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
