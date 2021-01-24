package jp.silverbullet.testspec;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class PrsNode {

	public String id;
	public String name;
	public Map<String, PrsPort> inputs = new LinkedHashMap<>();
	public Map<String, PrsPort> outputs = new LinkedHashMap<>();
}
