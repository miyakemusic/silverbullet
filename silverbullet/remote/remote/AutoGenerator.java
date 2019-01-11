package jp.silverbullet.remote;

import java.util.List;

import jp.silverbullet.property2.ListDetailElement;

public class AutoGenerator {
	private SvTexHolder texHolder = new SvTexHolder();
	private AutoGeneratorModel model;
	
	public AutoGenerator(AutoGeneratorModel model) {
		this.model = model;
	}
	
	public List<SvTex> generate(List<SvProperty> properties) {
		for (SvProperty prop : properties) {
			String argument = createArgument(prop);
			String parameter = createParameter(prop);
			SvTex tex = new SvTex();
			tex.setCommand(createScpi(prop.getId().toUpperCase()));
			tex.setSyntax(tex.getCommand() + " " + argument);
			tex.setDescription(prop.getComment());
			tex.setParameters(parameter);
			tex.setResponse("None.");
			tex.setExample(tex.getCommand() + " " + getCommandExample(prop));
			tex.setParams(createPARAMS(prop));
			tex.setAsync(model.containsAsyncHandler(prop.getId()));
			tex.setVlist(prop.getId());
			tex.setExecid("eSimpleSetParameter");
			tex.setNote(getNote(tex.isAsync()));
			texHolder.addTex("Section", tex);
			
			if (prop.isActionProperty()) {
				continue;
			}
			SvTex query = new SvTex();
			query.setCommand(createScpi(prop.getId().toUpperCase())+ "?");
			query.setSyntax(query.getCommand());
			query.setDescription(prop.getComment());
			query.setParameters("None.");
			query.setResponse(createResponse(prop));
			query.setExample(query.getCommand() + " -> " + getCommandExample(prop));
			query.setParams(createPARAMS(prop));
			query.setVlist(prop.getId());
			query.setExecid("eSimpleGetParameter");
			texHolder.addTex("Section", query);
		}
		return texHolder.getAllTexs();
	}

	private String getNote(boolean async) {
		if (async) {
			return "This is an asyncronous command.\nPlease wait for completion with *OPC? command";
		}
		return "";
	}

	private String createPARAMS(SvProperty prop) {
		String ret = "";
		if (prop.isListProperty()) {
			int i = 0;
			ret += "{";
			for (ListDetailElement e : prop.getListDetail()) {
				ret += this.createListElement(prop.getId(), e) + "=" + e.getId() + ",";
		//		ret += this.createScpi(e.getId().replace(prop.getId(), "")) + "=" + String.valueOf(i++) + ",";
			}
			ret = ret.substring(0, ret.length()-1);
			ret += "}";
		}
		else if (prop.isNumericProperty()) {
			ret += "{MINimum=" + prop.getMin() + ",MAXimum=" + prop.getMax() + "," + "DEFault=" + prop.getCurrentValue() + ", <value>}";
		}
		return ret;
	}
	
	private String createListElement(String id, ListDetailElement e) {
		return this.createScpi(e.getId().replace(id + "_", "")).split(":")[0];
	}

	protected String getCommandExample(SvProperty prop) {
		if (prop.isListProperty()) {
			return prop.getCurrentValue().replace(prop.getId(), "").replace("_", "");
		}
		else if (prop.isBooleanProperty()) {
			return prop.getCurrentValue().equals("true") ? "1" : "0";
		}
		else {
			return prop.getCurrentValue();
		}
	}

	private String createResponse(SvProperty prop) {
		if (prop.getType().equals("TextProperty")) {
			return "$<$CHARACTER PROGRAM DATA$>$";
		}
		else {
			return createParameter(prop);
		}
	}

	protected String createScpi(String id) {
//		String id = prop.getId().toUpperCase();
		if (id.startsWith("ID_")) {
			id = id.substring(3, id.length());
		}
		String[] tmp = id.split("_");
		String ret = "";
		for (String s : tmp) {
			if (s.length() <= 3) {
				ret += s;
			}
			else {
				String t = s.substring(0, 3);
				boolean lower = false;
				for (int i = 3; i < s.length(); i++) {
					char c = s.charAt(i);
					if ((isVowels(c) && !lower) || (i >= 4)) {
						lower = true;
					}
					if (lower) {
						t += String.valueOf(c).toLowerCase();
					}
					else {
						t += c;
					}
				}
				ret += t;
			}
			ret += ":";
		}
		ret = ret.substring(0, ret.length()-1);
		return ret;
	}

	private boolean isVowels(char c) {
		return (c == 'A') || (c == 'I') || (c == 'U') || (c == 'E') || (c == 'O');
	}

	private String createParameter(SvProperty prop) {
		String parameter = "";
		if (prop.isListProperty()) {
			parameter = "$<$CHARACTER PROGRAM DATA$>$" + "\n";
			for (ListDetailElement e : prop.getListDetail()) {
				parameter += this.createListElement(prop.getId(), e) + " : " + e.getTitle() + "\n";;
			}
		}
		else if (prop.isNumericProperty()){
			parameter += "$<$NUMERIC PROGRAM DATA$>$" + "\n";
			parameter += "Range : " + prop.getMin() + prop.getUnit() + " to " + 
					prop.getMax() + prop.getUnit() + "\n";
		}
		else if (prop.getType().equals("TextProperty")) {
			parameter = "$<$CHARACTER PROGRAM DATA$>$";
		}
		else if (prop.getType().equals("BooleanProperty")) {
			parameter = "$<$enable$>$ = $<$BOOLEAN PROGRAM DATA$>$" + "\n";
			parameter += "1 : ON" + "\n";
			parameter += "0 : OFF";
		}
		return parameter;
	}

	protected String createArgument(SvProperty prop) {
		String argument = "";
		if (prop.getType().equals("ActionProperty")) {
			
		}
		else {
			argument = "$<$param$>$";
		}
		return argument;
	}
}
