package jp.silverbullet.web;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import obsolute.property.PropertyDef;

@XmlRootElement
public class PropertyDefList {
	public List<PropertyDef> list = new ArrayList<>();
	
}
