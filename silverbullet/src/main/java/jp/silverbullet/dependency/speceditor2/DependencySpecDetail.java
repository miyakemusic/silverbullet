package jp.silverbullet.dependency.speceditor2;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DependencySpecDetail implements Cloneable {


	@Override
	public DependencySpecDetail clone() {
		try {
			DependencySpecDetail ret = (DependencySpecDetail)super.clone();
			ret.specification = this.specification.clone();
			return ret;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String passiveId;
	private String passiveElement;
	public void setPassiveElement(String passiveElement) {
		this.passiveElement = passiveElement;
	}

	public void setSpecification(DependencyFormula specification) {
		this.specification = specification;
	}

	private DependencyFormula specification;
	
	public DependencySpecDetail() {}
	
	public DependencySpecDetail(String id, String element, DependencyFormula specification) {
		this.passiveId = id;
		this.passiveElement = element;
		this.specification = specification; 
	}

	public void setPassiveId(String passiveId) {
		this.passiveId = passiveId;
	}
	
	public String getPassiveId() {
		return passiveId;
	}

	public String getPassiveElement() {
		return passiveElement;
	}

	public DependencyFormula getSpecification() {
		return specification;
	}

	public String getTargetId() {
		return this.specification.getId();
	}

	@Override
	public String toString() {
		return this.passiveElement + " of " + this.passiveId;
	}

}
