package jp.silverbullet.dev;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DeviceUiEntry {
	private static final String filename = "DeviceUiEntry.json";
	public Map<String, String> map = new HashMap<>(); // key = device, value = uiEntry
	public void set(String device, String ui) {
		this.map.put(device, ui);
	}

	public String get(String device) {
		return this.map.get(device);
	}

	public static DeviceUiEntry load(String folder) {
		try {
			return new ObjectMapper().readValue(new File(folder + "/" + filename), DeviceUiEntry.class);
		} catch (IOException e) {
			return new DeviceUiEntry();
		}
	}

	public void save(String folder) {
		try {
			new ObjectMapper().writeValue(new File(folder + "/" + filename), this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
