package jp.silverbullet.testspec;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;

public class NetworkTestConfigurationHolder {
	public TsSelectionConfig selections = new TsSelectionConfig();
	private Map<String, NetworkConfiguration> configs = new HashMap<>();
	private Map<String, TsTestSpec> testSpecs = new HashMap<>();
	
	private static final String EXTENSION = ".netcfg";
	public NetworkConfiguration get(String projectName) {
		return this.configs.get(projectName);
	}
	
	public void load(String folder) {
		try {

			try (Stream<Path> walk = Files.walk(Paths.get(folder))) {
				walk
					.filter(p -> !Files.isDirectory(p))   // not a directory
					.map(p -> p.toString().toLowerCase()) // convert path to string
					.filter(f -> f.endsWith(EXTENSION))       // check end with
					.forEach(file -> {
			  				try {
								PrsNetworkConfiguration cfg = new ObjectMapper().readValue(new File(file), PrsNetworkConfiguration.class);
								
								NetworkConfiguration networkConfiguration = new NodeConverter().programData(cfg);
								
								configs.put(new File(file).getName().replace(EXTENSION, ""), networkConfiguration);
								
							} catch (IOException e) {
								e.printStackTrace();
							}	
			              });
			  }

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	public void save(String folder) {
		this.configs.forEach((k,v) -> {
			PrsNetworkConfiguration cfg = new NodeConverter().persistentData(v);
			try {
				new ObjectMapper().writeValue(new File(folder + "/" + k + EXTENSION), cfg);
			} catch (IOException e) {
				e.printStackTrace();
			}			
		});
		

	}

	public void setPortConfig(String projectName, String id, TsPortConfig config) {
		this.configs.get(projectName).findPort(id).config(config);
	}

	public TsPortConfig getPortConfig(String projectName, String id) {
		return this.configs.get(projectName).findPort(id).config();
	}

	public void createDemo() {
		NetworkConfiguration demo = new NetworkConfiguration().createDemo();
		this.configs.put("demo", demo);
	}

	public void copyPortConfig(String projectName, String id, TsPortConfig config) {
		this.configs.get(projectName).copyConfig(id, config);
	}

	public TsTestSpec createScript(String projectName, List<String> ids) {
		TsTestSpec testSpec = new TestSpecGenerator().generate(this.configs.get(projectName), ids, projectName);
		this.testSpecs.put(projectName, testSpec);
		return testSpec;
	}

	public TsTestSpec sortBy(String projectName, String sortBy) {
		Comparator<TsTestSpecElement> comparator = new Comparator<TsTestSpecElement>() {

			@Override
			public int compare(TsTestSpecElement arg0, TsTestSpecElement arg1) {
				try {
					String s1 = TsTestSpecElement.class.getField(sortBy).get(arg0).toString();				
					String s2 = TsTestSpecElement.class.getField(sortBy).get(arg1).toString();
					return s1.compareTo(s2);
				} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
						| SecurityException e) {

					e.printStackTrace();
				}
				
				return 0;
			}
			
		};
		TsTestSpec testSpec = this.testSpecs.get(projectName);
		Collections.sort(testSpec.spec, comparator);
		generate(testSpec, projectName);
		return testSpec;
	}
	
	private void generate(TsTestSpec testSpec, String projectName) {
		testSpec.clear();
		String currentNode = "";
		String currentDirection = "";
		String currentSide = "";
		String currentMethod = "";
		String currentPort = "";
		
		testSpec.add("var MT1041A = 'MT1041A';");
		testSpec.add("var okControl = '{\"controls\":[{\"type\":\"Button\",\"title\":\"OK\",\"id\":\"ok\"},{\"type\":\"Button\",\"title\":\"Abort\",\"id\":\"abort\"}]}';");
		
		testSpec.add("function func() {");
		String device = null;
		for (TsTestSpecElement e : testSpec.spec) {
			String message = "";

			if (!e.testMethod.equals(currentMethod)) {
				message += "<div>Use tester <b><font color=\"blue\">" + e.testMethod + "</font></b>.</div>";			
				message += this.selections.message(e.testMethod);
				currentMethod = e.testMethod;
			}
			if (!e.nodeName.equals(currentNode)) {
				message += "<div>Move to equipment <b><font color=\"blue\">" + e.nodeName + "</font></b></div>";
				currentNode = e.nodeName;
			}

			if (!e.portName.equals(currentPort)) {
				message += "<div>Change port to <b><font color=\"blue\">" + e.portName + "</font></b>.</div>";
				currentPort = e.portName;
			}

			if (!e.testSide.equals(currentSide)) {
				message += "<div>Connect to <b><font color=\"blue\">" + e.testSide + "</font></b>.</div>";
				currentSide = e.testSide;
			}
			
			device = deviceName(e.testMethod);
			
			testSpec.add("\tif (sb.requires('" + e.portId + "', '" + e.testMethod + "')) {");
			if (!message.isEmpty()) {
				testSpec.add("\t\tif (sb.message("+ device + ", '" + message + "', okControl) == 'abort')return;");
			}

			for (String line : replaceValues(this.selections.script(projectName, e.testMethod), e)) {
				testSpec.add("\t\t" + line);
			}
//			this.testSpec.script.addAll(replaceValues(this.selections.script(e.testMethod), e));
			testSpec.add("\t}");
			
		};
		testSpec.add("if (sb.message(" + device + ", '<h1>Good Job! <br>You can go home now!</h1>', okControl) =='abort')return;");
		
		testSpec.add("}");
		testSpec.add("func();");
	}
	
	private List<String> replaceValues(List<String> script, TsTestSpecElement e) {
		List<String> ret = new ArrayList<>();
		for (String line : script) {
			ret.add(line.replace("%NODEID%", e.nodeId).replace("%PORTID%", e.portId).replace("%DIRECTION%", e.portDirection).replace("%PORT%", e.portName).replace("%METHOD%", e.testMethod).replace("%SIDE%", e.testSide));
		}
		return ret;
	}

	private String deviceName(String testMethod) {
//		if (testMethod.equals(NetworkTestConfigurationHolder.FIBER_END_FACE_INSPECTION) ){
//			return "VIP";
//		}
//		else if (testMethod.equals(NetworkTestConfigurationHolder.OPTICAL_POWER_METER) ){
//			return "OPM";
//		}
//		else if (testMethod.equals(NetworkTestConfigurationHolder.OTDR) ){
//			return "OTDR";
//		}
		return "MT1041A";
	}
	
	public List<String> getTestMethods() {
		return this.selections.testMethods();
	}

	public TsTestSpec testSpec(String projectName) {
		return this.testSpecs.get(projectName);
	}

	public TsNode getNode(String projectName, String nodeId) {
		return this.configs.get(projectName).findNode(nodeId);
	}

	public Set<String> getAllPorts(String projectName) {
		return this.configs.get(projectName).allPorts.keySet();
	}

	public List<String> getProjectList() {
		return new ArrayList<String>(this.configs.keySet());
	}
}
