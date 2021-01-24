package jp.silverbullet.testspec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TsTestSpec {

	public List<TsTestSpecElement> spec = new ArrayList<>();
	public String script;
	
	public void add(String nodeName, String portDirection, String testSide, String portName, String testMethod) {
		this.spec.add(new TsTestSpecElement(nodeName, portDirection, testSide, portName, testMethod));
	}

	public void generate() {
		StringBuilder builder = new StringBuilder();
		this.spec.forEach(e -> {
			if (e.testMethod.equals(NetworkTestConfigurationHolder.FIBER_END_FACE_INSPECTION)) {
				builder.append("sb.write('VIP', \"ID_VIP_TESTCONTROL=ID_VIP_TESTCONTROL_START\");\n");
			}
			else if (e.testMethod.equals(NetworkTestConfigurationHolder.OPTICAL_POWER_METER)) {
				builder.append("sb.write('OPM', \"ID_OPM_TESTCONTROL=ID_OPM_TESTCONTROL_START\");\n");
			}
			else if (e.testMethod.equals(NetworkTestConfigurationHolder.OTDR)) {
				builder.append("sb.write('OTDR', \"ID_OTDR_TESTCONTROL=ID_OTDR_TESTCONTROL_START\");\n");
			} 
		});
		script = builder.toString();
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
