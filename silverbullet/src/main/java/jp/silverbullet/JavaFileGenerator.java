package jp.silverbullet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.silverbullet.dependency2.RequestRejectedException;
import jp.silverbullet.handlers.EasyAccessInterface;
import jp.silverbullet.property.SvProperty;
import jp.silverbullet.property2.ListDetailElement;

public class JavaFileGenerator {

	private List<SvProperty> properties;
	private Set<String> exists = new HashSet<String>();
	public JavaFileGenerator(List<SvProperty> properties) {
		this.properties = properties;
	}

	public void generate(String path) {
		List<String> lines = new ArrayList<String>();
		lines.add("package " + path + ";");
		lines.add("public class ID {");
		for (SvProperty prop : properties) {
			if (prop.getIndex() > 0) { // This is tentative code
				continue;
			}
			lines.add(createLine(prop.getId()));
			if (prop.isListProperty()) {
				for (ListDetailElement e : prop.getListDetail()) {
					lines.add(createLine(e.getId()));
				}
			}
		}
		lines.add("}");
		try {
			
			//String path = this.getClass().getPackage().getName();
			String path2 = "src/main/java/" + path.replace(".", "/");// + "/handlers/user/";
			Files.write(Paths.get(path2 + "/ID.java"), lines, StandardCharsets.UTF_8);
			
			Files.write(Paths.get(path2 + "/UserEasyAccess.java"), generateSimple(path), StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public List<String> generateSimple(String path) {
		List<String> source = new ArrayList<String>();
		source.add("package " + path +";");
//		source.add("import " + EasyAccessModel.class.getName() + ";");
		source.add("import " + RequestRejectedException.class.getName() + ";");
		source.add("import " + EasyAccessInterface.class.getName() + ";");
		
		source.add("public class UserEasyAccess {");
		source.add("    private EasyAccessInterface model;");
		source.add("    public UserEasyAccess(EasyAccessInterface model2) {");
		source.add("        this.model = model2;");
		source.add("    }");
		for (SvProperty prop : properties) {
			if (prop.getIndex() > 0) { // This is tentative code
				continue;
			}
			boolean array = prop.getProperty().getSize() > 1;
			if (prop.getType().equals("DoubleProperty")) {
				source.add("    public void set" + getMethodName(prop.getId()) + "(double value) throws RequestRejectedException {");
				source.add("        model.requestChange(ID." + prop.getId() + ", String.valueOf(value));");
				source.add("    }");
				source.add("    public double get" + getMethodName(prop.getId()) + "() {");
				source.add("        return Double.valueOf(model.getProperty(ID." + prop.getId() + ").getCurrentValue());");
				source.add("    }");
				if (array) {
					source.add("    public void set" + getMethodName(prop.getId()) + "(double value, int index) throws RequestRejectedException {");
					source.add("        model.requestChange(ID." + prop.getId() + ", index, String.valueOf(value));");
					source.add("    }");
	
				}
			}
			else if (prop.getType().equals("LongProperty")) {
				source.add("    public void set" + getMethodName(prop.getId()) + "(long value) throws RequestRejectedException {");
				source.add("        model.requestChange(ID." + prop.getId() + ", String.valueOf(value));");
				source.add("    }");
				source.add("    public long get" + getMethodName(prop.getId()) + "() {");
				source.add("        return Long.valueOf(model.getProperty(ID." + prop.getId() + ").getCurrentValue());");
				source.add("    }");
				if (array) {
					source.add("    public void set" + getMethodName(prop.getId()) + "(long value, int index) throws RequestRejectedException {");
					source.add("        model.requestChange(ID." + prop.getId() + ", index, String.valueOf(value));");
					source.add("    }");

				}
			}
			else if (prop.getType().equals("IntProperty")) {
				source.add("    public void set" + getMethodName(prop.getId()) + "(int value" + ") throws RequestRejectedException {");
				source.add("        model.requestChange(ID." + prop.getId() + ", String.valueOf(value));");
				source.add("    }");
				source.add("    public int get" + getMethodName(prop.getId()) + "() {");
				source.add("        return Integer.valueOf(model.getProperty(ID." + prop.getId() + ").getCurrentValue());");
				source.add("    }");	
			}
			else if (prop.isTextProperty() || prop.isTableProperty()) {
				source.add("    public void set" + getMethodName(prop.getId()) + "(String value" + ") throws RequestRejectedException {");
				source.add("        model.requestChange(ID." + prop.getId() + ", value);");
				source.add("    }");
				source.add("    public String get" + getMethodName(prop.getId()) + "() {");
				source.add("        return model.getProperty(ID." + prop.getId() + ").getCurrentValue();");
				source.add("    }");	
			}
			else if (prop.getType().equals("ListProperty") || prop.getType().equals("ServerStateProperty") || 
					prop.getType().equals("MessageProperty")) {
				String methodName = this.getMethodName(prop.getId());
				source.add("    public enum Enum" + methodName + "{");
				for (ListDetailElement e : prop.getListDetail()) {
					source.add("        " + e.getId() + ",");
				}
				source.add("    };");	
				source.add("    public void set" + getMethodName(prop.getId()) + "(Enum" + methodName + " value) throws RequestRejectedException {");
				source.add("        model.requestChange(ID." + prop.getId() + ", value.toString());");
				source.add("    }");
				source.add("    public Enum" + methodName + " get" + getMethodName(prop.getId()) + "() {");
				source.add("        return Enum" + methodName + ".valueOf(model.getProperty(ID." + prop.getId() + ").getCurrentValue());");
				source.add("    }");

				
				if (array) {
					source.add("    public void set" + getMethodName(prop.getId()) + "(Enum" + methodName + " value, int index) throws RequestRejectedException {");
					source.add("        model.requestChange(ID." + prop.getId() + ", index, value.toString());");
					source.add("    }");
					source.add("    public Enum" + methodName + " get" + getMethodName(prop.getId()) + "(int index) {");
					source.add("        return Enum" + methodName + ".valueOf(model.getProperty(ID." + prop.getId() + " + \"@\" + index).getCurrentValue());");
					source.add("    }");
				}
			}
		}
		source.add("}");
		
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
