package jp.silverbullet.dependency.speceditor2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class DependencySpec implements Cloneable {

	@Override
	protected DependencySpec clone() {
		try {
			DependencySpec ret = (DependencySpec) super.clone();
			ret.specs = new ArrayList<DependencySpecDetail>();
			for (DependencySpecDetail spec : specs) {
				ret.specs.add(spec.clone());
			}
			return ret;
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private List<DependencySpecDetail> specs = new ArrayList<DependencySpecDetail>();
	private String id;
	private Set<String> confirmRequired = new HashSet<>();
	
	public Set<String> getConfirmRequired() {
		return confirmRequired;
	}

	public void setConfirmRequired(Set<String> confirmRequired) {
		this.confirmRequired = confirmRequired;
	}

	public DependencySpec(){}
	
	public DependencySpec(String id) {
		this.id = id;
	}

	public void add(String element, DependencyFormula specification) {
		DependencySpecDetail detail = new DependencySpecDetail(id, element, specification);
		specs.add(detail);
		//specs.add(id + "." + element + ";" + specification);
	}

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
		for (DependencySpecDetail d : this.specs) {
			d.setPassiveId(id);
		}
	}

	public List<DependencySpecDetail> getSpecs() {
		return specs;
	}

	public void setSpecs(List<DependencySpecDetail> specs) {
		this.specs = specs;
	}

	public List<DependencySpecDetail> findRelations(String id) {
		List<DependencySpecDetail> ret = new ArrayList<DependencySpecDetail>();
		for (DependencySpecDetail spec : specs) {
			String targetId = spec.getTargetId();
			if (id.equals(targetId)) {
				ret.add(spec);
			}
		}
		return ret;
	}

	public void removeSpec(DependencySpecDetail detail) {
		this.specs.remove(detail);
	}

	public List<DependencySpecDetail> getSpecs(String element) {
		List<DependencySpecDetail> ret = new ArrayList<DependencySpecDetail>();
		for (DependencySpecDetail spec : specs) {
			if (spec.getPassiveElement().equals(element)) {
				ret.add(spec);
			}
		}
		return ret;
	}

	public void setConfirmEnabled(String element, boolean enabled) {
		if (enabled) {
			this.confirmRequired.add(element);
		}
		else {
			this.confirmRequired.remove(element);
		}
	}
	
	public boolean isConfirmEnabled(String element) {
		return this.confirmRequired.contains(element);
	}
}
