package jp.silverbullet.testspec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TsTestSpec {

	public List<TsTestSpecElement> spec = new ArrayList<>();
	public List<String> script = new ArrayList<>();
	
	public void add(String nodeName, String portDirection, String testSide, String portName, String testMethod) {
		this.spec.add(new TsTestSpecElement(nodeName, portDirection, testSide, portName, testMethod));
	}

	public void generate() {
		script.clear();
		String currentNode = "";
		String currentDirection = "";
		String currentSide = "";
		String currentMethod = "";
		String currentPort = "";
		
		script.add("var MT1041A = 'MT1041A';");
		script.add("var okControl = '{\"controls\":[{\"type\":\"Button\",\"title\":\"OK\",\"id\":\"ok\"}]}';");
		
		String device = null;
		for (TsTestSpecElement e : spec) {
			String message = "";
			
			if (!e.nodeName.equals(currentNode)) {
				message += "Move to equipment <b><font color=\"blue\">" + e.nodeName + "</font></b>.";
				currentNode = e.nodeName;
			}
			if (!e.portDirection.equals(currentDirection)) {
				if (!message.isEmpty()) {
					message += "<br>";
				}
				message += "Connect to port <b><font color=\"blue\">" + e.portDirection + "</font></b>.";
				currentDirection = e.portDirection;
			}
			if (!e.testSide.equals(currentSide)) {
				if (!message.isEmpty()) {
					message += "<br>";
				}
				message += "Connect to <b><font color=\"blue\">" + e.testSide + "</font></b>.";
				currentSide = e.testSide;
			}
			if (!e.testMethod.equals(currentMethod)) {
				if (!message.isEmpty()) {
					message += "<br>";
				}
				message += "Use tester <b><font color=\"blue\">" + e.testMethod + "</font></b>.";
				currentMethod = e.testMethod;
			}
			if (!e.portName.equals(currentPort)) {
				if (!message.isEmpty()) {
					message += "<br>";
				}
				message += "Change port to <b><font color=\"blue\">" + e.portName + "</font></b>.";
				currentPort = e.portName;
			}
			
			device = deviceName(e.testMethod);
			
			if (!message.isEmpty()) {
				script.add("sb.message("+ device + ", '" + message + "', okControl)");
			}
			if (e.testMethod.equals(NetworkTestConfigurationHolder.FIBER_END_FACE_INSPECTION)) {
				script.add("sb.write(" + device + ", 'ID_APPLICATION=ID_APPLICATION_VIP');");
				script.add("sb.sleep(1000);");
				script.add("sb.write(" + device + ", 'ID_VIPCONTROL=ID_VIPCONTROL_START');");
				script.add("sb.sleep(1000);");
				script.add("sb.waitEqual(" + device + ", 'ID_VIPCONTROL', 'ID_VIPCONTROL_STOP');");
			}
			else if (e.testMethod.equals(NetworkTestConfigurationHolder.OPTICAL_POWER_METER)) {
				script.add("sb.write(" + device + ", 'ID_APPLICATION=ID_APPLICATION_OLTS');");
				script.add("sb.sleep(1000);");
				script.add("sb.write(" + device + ", 'ID_LS_ENABLED=ID_LS_ENABLED_ON');");
				script.add("sb.sleep(3000);");
				script.add("sb.write(" + device + ", 'ID_LS_ENABLED=ID_LS_ENABLED_OFF');");
			}
			else if (e.testMethod.equals(NetworkTestConfigurationHolder.OTDR)) {
				script.add("sb.write(" + device + ", 'ID_APPLICATION=ID_APPLICATION_OTDR');");
				script.add("sb.sleep(1000);");
				script.add("sb.write(" + device + ", 'ID_OTDR_TESTCONTROL=ID_OTDR_TESTCONTROL_START');");
				script.add("sb.sleep(1000);");
				script.add("sb.waitEqual(" + device + ", 'ID_OTDR_TESTCONTROL', 'ID_OTDR_TESTCONTROL_STOP');");
			} 
		};
		script.add("sb.message(" + device + ", '<h1>Good Job! <br>You can go home now!</h1>', okControl );");
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

	public void sort(String fieldName) {
		Comparator<TsTestSpecElement> comparator = new Comparator<TsTestSpecElement>() {

			@Override
			public int compare(TsTestSpecElement arg0, TsTestSpecElement arg1) {
				try {
					String s1 = TsTestSpecElement.class.getField(fieldName).get(arg0).toString();
					String s2 = TsTestSpecElement.class.getField(fieldName).get(arg1).toString();
					return s1.compareTo(s2);
				} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
						| SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return 0;
			}
			
		};
		Collections.sort(spec, comparator);
		this.generate();
	}
}
