package jp.silverbullet.dev.sourcegenerator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.silverbullet.core.dependency2.RequestRejectedException;
import jp.silverbullet.core.property2.PropertyDef2;
import jp.silverbullet.core.property2.PropertyHolder2;
import jp.silverbullet.core.property2.RuntimeProperty;
import jp.silverbullet.core.sequncer.EasyAccessInterface;

public class PropertySourceGenerator {

	private PropertyHolder2 properties;
	private Set<String> exists = new HashSet<String>();
	public PropertySourceGenerator(PropertyHolder2 propertyHolder2) {
		this.properties = propertyHolder2;
	}

	public void generate(String baseFolder, String packageName, String app){
		String idClassName = app.substring(0, 1).toUpperCase() + app.substring(1);
		
		List<String> lines = new ArrayList<String>();
		lines.add("package " + packageName + ";");
		lines.add("public class " + createID(idClassName) + "{");
		for (PropertyDef2 prop : properties.getProperties()) {
		//	if (prop.getIndex() > 0) { // This is tentative code
		//		continue;
		//	}
			lines.add(createLine(prop.getId()));
			if (prop.isList()) {
				prop.getOptionIds().forEach(optionId -> lines.add(createLine(optionId)));
			}
		}
		lines.add("}");
		try {
			String path2 = baseFolder + "/" + packageName.replace(".", "/");
			if (!Files.exists(Paths.get(path2))) {
				Files.createDirectories(Paths.get(path2));
			}
 			Files.write(Paths.get(path2 + "/" + createID(idClassName) + ".java"), lines, StandardCharsets.UTF_8);
			
			Files.write(Paths.get(path2 + "/" + idClassName + "UserEasyAccess.java"), generateSimple(packageName, idClassName), StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private String createID(String idClassName) {
		return idClassName + "ID";
	}

	public List<String> generateSimple(String packageName, String idClassName) {
		List<String> source = new ArrayList<String>();
		source.add("package " + packageName +";");
		source.add("import " + RequestRejectedException.class.getName() + ";");
		source.add("import " + EasyAccessInterface.class.getName() + ";");
		
		source.add("public class " + idClassName + "UserEasyAccess {");
		source.add("    private EasyAccessInterface model;");
		source.add("    public " + idClassName + "UserEasyAccess(EasyAccessInterface model2) {");
		source.add("        this.model = model2;");
		source.add("    }");
		for (PropertyDef2 prop : properties.getProperties()) {
		//	if (prop.getIndex() > 0) { // This is tentative code
		//		continue;
		//	}
			boolean array = prop.getArraySize() > 1;
			if (prop.isNumeric() && prop.getDecimals() > 0) {
				source.addAll(this.createSource(prop, "Double", idClassName));
			}
			else if (prop.isNumeric() && prop.getDecimals() == 0) {
				source.addAll(this.createSource(prop, "Long", idClassName));
			}
			else if (prop.isText() || prop.isTable()) {
				source.addAll(this.createSource(prop, "String", idClassName));
			}
			else if (prop.isList()) {
				String methodName = this.getMethodName(prop.getId());
				source.add("    public enum Enum" + methodName + "{");
				prop.getOptionIds().forEach(i -> source.add("        " + i + ","));
				source.add("    };");	
				source.add("    public void set" + getMethodName(prop.getId()) + "(Enum" + methodName + " value) throws RequestRejectedException {");
				source.add("        model.requestChange(" + createID(idClassName) + "." + prop.getId() + ", value.toString());");
				source.add("    }");
				source.add("    public Enum" + methodName + " get" + getMethodName(prop.getId()) + "() {");
				source.add("        return Enum" + methodName + ".valueOf(model.getCurrentValue(" + createID(idClassName) + "." + prop.getId() + "));");
				source.add("    }");
				
				if (array) {
					source.add("    public void set" + getMethodName(prop.getId()) + "(Enum" + methodName + " value, int index) throws RequestRejectedException {");
					source.add("        model.requestChange(" + createID(idClassName) + "." + prop.getId() + ", index, value.toString());");
					source.add("    }");
					source.add("    public Enum" + methodName + " get" + getMethodName(prop.getId()) + "(int index) {");
					source.add("        return Enum" + methodName + ".valueOf(model.getCurrentValue(" + createID(idClassName) + "." + prop.getId() + " + \"" + RuntimeProperty.INDEXSIGN + "\" + index));");
					source.add("    }");
				}
			}
			else if (prop.isBoolean()) {
				source.addAll(this.createSource(prop, "Boolean", idClassName));
			}
		}
		source.add("}");
		
		return source;
	}
	
	private List<String> createSource(PropertyDef2 prop, String type, String idClassName) {
		List<String> source = new ArrayList<>();
		boolean array = prop.getArraySize() > 1;
		source.add("    public void set" + getMethodName(prop.getId()) + "(" + type + " value) throws RequestRejectedException {");
		source.add("        model.requestChange(" + createID(idClassName) + "." + prop.getId() + ", String.valueOf(value));");
		source.add("    }");
		source.add("    public " + type + " get" + getMethodName(prop.getId()) + "() {");
		source.add("        return " + type + ".valueOf(model.getCurrentValue(" + createID(idClassName) + "." + prop.getId() + "));");
		source.add("    }");
		if (array) {
			source.add("    public void set" + getMethodName(prop.getId()) + "(" + type + " value, int index) throws RequestRejectedException {");
			source.add("        model.requestChange(" + createID(idClassName) + "." + prop.getId() + ", index, String.valueOf(value));");
			source.add("    }");
		}
		return source;
	}
	private String getMethodName(String id) {
		if (id.startsWith("ID_")) {
			id = id.substring(3, id.length());
		}
		String ret = "";
		for (String s : id.split("_")) {
			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				if (i == 0) {
					ret += String.valueOf(c);
				}
				else {
					ret += String.valueOf(c).toLowerCase();
				}
			}
		}
		return ret;
	}

	protected String createLine(String id) {
		if (exists.contains(id)) {
			return "";
		}
		exists .add(id);
		return "\tpublic static final String " + id + "=" + "\"" + id + "\";";
	}

}
