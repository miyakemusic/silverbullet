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
	
}
