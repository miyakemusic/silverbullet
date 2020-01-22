package jp.silverbullet.web;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonPersistent {
	public void saveJson(Object object, String filename) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		String s = mapper.writeValueAsString(object);
		Files.write(Paths.get(filename), Arrays.asList(s));
	}
	
	public <T> T loadJson(Class<T> clazz, String filename) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
	
		T ret = mapper.readValue(new File(filename), clazz);
		if (ret == null) {
			try {
				ret = clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}
}
