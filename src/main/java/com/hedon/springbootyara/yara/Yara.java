package com.hedon.springbootyara.yara;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedon.springbootyara.upload.StorageException;

@Component
public class Yara {
    private static final Logger log = LoggerFactory.getLogger(Yara.class);
	private Path rulesPath = Paths.get("rules");
	private ProcessBuilder processBuilder = new ProcessBuilder();
	private List<File> rules = new ArrayList<File>();

	public Yara addRule(File rule) {
		log.info("add rule: " + rule.getName());
		rules.add(rule);
		return this;
	}

	public List<Path> loadAllRules() {
        try {
			System.out.println("Lokasi file loadALLRUles : "+ this.rulesPath.toString());

			List<Path> result = Files.walk(this.rulesPath, 1)
				.filter(Files::isRegularFile)
				// .map(this.rulesPath::relativize)
				.collect(Collectors.toList());

			return result;
		}
		catch (IOException e) {
			throw new StorageException("Failed to read files", e);
		}
    }

	public Map<String, String>/* List<String> */ scan(File file, File cmd) {
		log.info("start scan file: " + file.getName());
		List<Path> paths = loadAllRules();
        // paths.forEach(x -> System.out.println(x));
		
		List<String> command = new ArrayList<String>();
        command.add(cmd.getAbsolutePath());
		command.add("-m");
		paths.forEach(x -> command.add(x.toAbsolutePath().toString()));
		// command.add(rules.get(0).getAbsolutePath());
		command.add(file.getAbsolutePath());

		log.info("execute: " + command);

		// processBuilder.command("/bin/bash", "-c",
		// processBuilder.command(cmd.getAbsolutePath() + " " + rules.get(0).getAbsolutePath() + " " + file.getAbsolutePath());
		// ProcessBuilder builder = new ProcessBuilder(command);
		processBuilder.command(command);

		// return execute(file, cmd);
		return execute();
	}

	private Map<String, String>/* List<String> */ execute() {
	// private String execute(File file, File cmd) {
		StringBuilder sb = new StringBuilder();
		List<String> result = new ArrayList<String>();
		List<String[]> res = new ArrayList<String[]>();
		
		try {
			Process process = processBuilder.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			String[] trimAndSplitUnicode;
			
			while ((line = reader.readLine()) != null) {
				String replace = line.replace(" [", ";");
				replace = replace.replace("] ", ";");
				trimAndSplitUnicode = replace.trim().split(";");
				// for (String string : trimAndSplitUnicode) {
				// 	System.out.println(string);
				// }
				
				// System.out.println(replace);
				res.add(trimAndSplitUnicode);
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

		Map<String, String> hashMap = new HashMap<String, String>();
		for (String[] res2 : res) {
			// String jsonString = "{\"phonetype\":\"N95\",\"cat\":\"WP\"}";
			// String jsonString = "{\"author\":\"hedon\",\"date\":\"11\\/06\\/2022\",\"description\":\"This is basic YARA rule for Ascii example\",\"version\" :\"1\",\"block\":\"true\"}"; 
			// JsonNode jsonNode = stringToJSONObject(res2[1].replace("=", ":"));
			// JsonNode jsonNode = stringToJSONObject(jsonString);
			hashMap.put("rulename", res2[0]);
			System.out.println("Panjang res2[1] : "+ res2[1].length());
			if(res2[1].length() > 0) {
				String[] res3 = res2[1].trim().split(",");
				for (String s : res3) {
					System.out.println(s);
					String[] metaElement = s.trim().split("=");
					System.out.println("Panjang metaElement : "+ metaElement.length);
					String metaKey = metaElement[0].trim();
					String metaValue = metaElement[1].trim();
					hashMap.put(metaKey, metaValue);
				}
			}
			
			// System.out.println(jsonNode);
			// System.out.println("{"+res2[1].replace("=", ":")+"}");
		}

		log.info("Program terminated!");
		// return sb.toString();
		return hashMap;
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

	public static JsonNode stringToJSONObject(String jsonString) {
		ObjectMapper jacksonObjMapper = new ObjectMapper();
		JsonNode jsonNode = null;
		try {
			jsonNode = jacksonObjMapper.readTree(jsonString);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return jsonNode;
	}
}
