package jp.silverbullet.handlers;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class HandlerProperty {
	public HandlerProperty(String name2, String description2, Boolean async2, String id) {
		this.name = name2;
		this.description = description2;
		this.async = async2;
		this.ids.add(id);
	}
	
	public HandlerProperty() {}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<String> getIds() {
		return ids;
	}
	public void setIds(List<String> ids) {
		this.ids = ids;
	}
	private String name = "";
	private String description = "";
	private Boolean async = false;
	private String externalClass = "";
	public String getExternalClass() {
		return externalClass;
	}

	public void setExternalClass(String externalClass) {
		this.externalClass = externalClass;
	}

	public Boolean getAsync() {
		return async;
	}

	public void addId(String id) {
		this.ids.add(id);
	}
	
	public void setAsync(Boolean async) {
		this.async = async;
	}
	private List<String> ids = new ArrayList<String>();
}
