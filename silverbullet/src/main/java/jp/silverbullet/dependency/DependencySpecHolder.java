package jp.silverbullet.dependency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="DependencySpecHolder")
public class DependencySpecHolder {

	private HashMap<String, DependencySpec> specs = new HashMap<>();
	
	public void add(DependencySpec spec) {
		specs.put(spec.getId(), spec);
	}

	public List<DependencyProperty> findSpecsToBeChangedSpecBy(String id, DependencyTargetElement dependencyTargetElement) {
		List<DependencyProperty> ret = new ArrayList<>();
		
		for (DependencySpec spec : specs.values()) {
			ret.addAll(spec.findToBeChangedBy(id, dependencyTargetElement));
		}
		return ret;
	}

	public void setSpecs(HashMap<String, DependencySpec> specs) {
		this.specs = specs;
	}

//	@XmlElement(name="DependencySpecs")
	public HashMap<String, DependencySpec> getSpecs() {
		return specs;
	}

	public DependencySpec get(String id) {
		if (!this.specs.keySet().contains(id)) {
			this.specs.put(id, new DependencySpec(id));
		}
		return this.specs.get(id);
	}

	public void clean(DependencySpec spec) {
		if (spec.isEmpty()) {
			this.specs.remove(spec.getId());
		}
	}

	public void changeId(String prevId, String newId) {
		for (String id : this.specs.keySet()) {
			DependencySpec spec = this.specs.get(id);
			spec.changeId(prevId, newId);
		}
		if (this.specs.keySet().contains(prevId)) {
			DependencySpec spec = this.specs.get(prevId);
			this.specs.put(newId, spec);
			this.specs.remove(prevId);
		}
	}
}
