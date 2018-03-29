package jp.silverbullet.dependency.speceditor3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DependencySpecHolder2 {

	Map<String, DependencySpec2> specs = new HashMap<>();
	
	public void add(DependencySpec2 spec) {
		specs.put(spec.getId(), spec);
	}

	public List<DependencyProperty> findSpecsToBeChangedSpecBy(String id) {
		List<DependencyProperty> ret = new ArrayList<>();
		
		for (DependencySpec2 spec : specs.values()) {
			ret.addAll(spec.findToBeChangedBy(id));
		}
		return ret;
	}
	
}
