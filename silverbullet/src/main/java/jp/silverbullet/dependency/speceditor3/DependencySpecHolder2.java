package jp.silverbullet.dependency.speceditor3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="DependencySpecHolder")
public class DependencySpecHolder2 {

	private HashMap<String, DependencySpec2> specs = new HashMap<>();
	
	public void add(DependencySpec2 spec) {
		specs.put(spec.getId(), spec);
	}

	public List<DependencyProperty> findSpecsToBeChangedSpecBy(String id, DependencyTargetElement dependencyTargetElement) {
		List<DependencyProperty> ret = new ArrayList<>();
		
		for (DependencySpec2 spec : specs.values()) {
			ret.addAll(spec.findToBeChangedBy(id, dependencyTargetElement));
		}
		return ret;
	}

	public void setSpecs(HashMap<String, DependencySpec2> specs) {
		this.specs = specs;
	}

//	@XmlElement(name="DependencySpecs")
	public HashMap<String, DependencySpec2> getSpecs() {
		return specs;
	}

	public DependencySpec2 get(String id) {
		if (!this.specs.keySet().contains(id)) {
			this.specs.put(id, new DependencySpec2(id));
		}
		return this.specs.get(id);
	}

	public void clean(DependencySpec2 spec) {
		if (spec.isEmpty()) {
			this.specs.remove(spec.getId());
		}
	}
}
