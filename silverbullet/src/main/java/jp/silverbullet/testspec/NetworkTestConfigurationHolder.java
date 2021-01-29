package jp.silverbullet.testspec;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

public class NetworkTestConfigurationHolder {
//	public static final String OPTICAL_POWER_METER = "Optical Power Meter";
//	public static final String OTDR = "OTDR";
//	public static final String FIBER_END_FACE_INSPECTION = "Fiber end-face inspection";
	
	private NetworkConfiguration active = new NetworkConfiguration();
	private TsTestSpec testSpec;
	private static final String filename = "networktestconfig.json";
	public TsSelectionConfig selections = new TsSelectionConfig();
	
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
		Comparator<TsTestSpecElement> comparator = new Comparator<TsTestSpecElement>() {

			@Override
			public int compare(TsTestSpecElement arg0, TsTestSpecElement arg1) {
				try {
					String s1 = TsTestSpecElement.class.getField(sortBy).get(arg0).toString();
					String s2 = TsTestSpecElement.class.getField(sortBy).get(arg1).toString();
					return s1.compareTo(s2);
				} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
						| SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return 0;
			}
			
		};
		Collections.sort(this.testSpec.spec, comparator);
		generate3();
		return this.testSpec;
	}
	
	private void generate3() {
		this.testSpec.script.clear();
		String currentNode = "";
		String currentDirection = "";
		String currentSide = "";
		String currentMethod = "";
		String currentPort = "";
		
		this.testSpec.script.add("var MT1041A = 'MT1041A';");
		this.testSpec.script.add("var okControl = '{\"controls\":[{\"type\":\"Button\",\"title\":\"OK\",\"id\":\"ok\"},{\"type\":\"Button\",\"title\":\"Abort\",\"id\":\"abort\"}]}';");
		
		this.testSpec.script.add("function func() {");
		String device = null;
		for (TsTestSpecElement e : this.testSpec.spec) {
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
			
			if (!message.isEmpty()) {
				this.testSpec.script.add("if (sb.message("+ device + ", '" + message + "', okControl) == 'abort')return;");
			}
			
			this.testSpec.script.addAll(this.selections.script(e.testMethod));
			
		};
		this.testSpec.script.add("if (sb.message(" + device + ", '<h1>Good Job! <br>You can go home now!</h1>', okControl) =='abort')return;");
		
		this.testSpec.script.add("}");
		this.testSpec.script.add("func();");
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

	public TsTestSpec testSpec() {
		return this.testSpec;
	}
}
