package jp.silverbullet.testspec;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

public class NetworkTestConfigurationHolder {
	public static final String OPTICAL_POWER_METER = "Optical Power Meter";
	public static final String OTDR = "OTDR";
	public static final String FIBER_END_FACE_INSPECTION = "Fiber end-face inspection";
	
	private NetworkConfiguration active = new NetworkConfiguration();
	private TsTestSpec testSpec;
	private static final String filename = "networktestconfig.json";
	
	public NetworkConfiguration get() {
		return this.active;
	}
	
	public void load(String folder) {
		try {
			PrsNetworkConfiguration cfg = new ObjectMapper().readValue(new File(folder + "/" + filename ), PrsNetworkConfiguration.class);
			this.active = new NodeConverter().programData(cfg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void save(String folder) {
		PrsNetworkConfiguration cfg = new NodeConverter().persistentData(this.active);
		try {
			new ObjectMapper().writeValue(new File(folder + "/" + filename), cfg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setPortConfig(String id, TsPortConfig config) {
		this.active.findPort(id).config(config);
	}

	public TsPortConfig getPortConfig(String id) {
		return this.active.findPort(id).config();
	}

	public void createDemo() {
		this.active = new NetworkConfiguration().createDemo();
	}

	public void copyPortConfig(String id, TsPortConfig config) {
		this.active.copyConfig(id, config);
	}

	public TsTestSpec createScript(List<String> ids) {
		testSpec = new TestSpecGenerator().generate(this.active, ids);
		return testSpec;
	}

	public TsTestSpec sortBy(String sortBy) {
		this.testSpec.sort(sortBy);
		return this.testSpec;
	}

	public List<String> getTestMethods() {
		return Arrays.asList(FIBER_END_FACE_INSPECTION, OTDR, OPTICAL_POWER_METER);
	}
}
