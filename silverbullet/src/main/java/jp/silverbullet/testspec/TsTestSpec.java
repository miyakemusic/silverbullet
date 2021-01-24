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
			
			if (!message.isEmpty()) {
				script.add("sb.message('"+ message + "')");
			}
			if (e.testMethod.equals(NetworkTestConfigurationHolder.FIBER_END_FACE_INSPECTION)) {
				script.add("sb.write('VIP', \"ID_VIP_TESTCONTROL=ID_VIP_TESTCONTROL_START\");");
			}
			else if (e.testMethod.equals(NetworkTestConfigurationHolder.OPTICAL_POWER_METER)) {
				script.add("sb.write('OPM', \"ID_OPM_TESTCONTROL=ID_OPM_TESTCONTROL_START\");");
			}
			else if (e.testMethod.equals(NetworkTestConfigurationHolder.OTDR)) {
				script.add("sb.write('OTDR', \"ID_OTDR_TESTCONTROL=ID_OTDR_TESTCONTROL_START\");");
			} 
		};
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
