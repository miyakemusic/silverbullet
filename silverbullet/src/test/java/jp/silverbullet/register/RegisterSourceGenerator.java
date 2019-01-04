package jp.silverbullet.register;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class RegisterSourceGenerator {

	private RegisterSpecHolder holder;
	private String packageName;
	private String className;

	public RegisterSourceGenerator(RegisterSpecHolder holder, String packageName, String className) {
		this.holder = holder;
		this.packageName = packageName;
		this.className = className;
	}

	public List<String> generate() {
		List<String> lines = new ArrayList<>();
		// package
		lines.add("package " + this.packageName + ";"); 
		
		// import
		lines.add("import jp.silverbullet.register2.RegisterAccessor;");
		lines.add("import jp.silverbullet.register2.RuntimeRegister;");
		lines.add("import jp.silverbullet.register2.RuntimeRegisterHolder;");
		
		lines.add("public class " + className + " extends RuntimeRegisterHolder {");
		//  constructor
		lines.add("	public " + className + "(RegisterAccessor registerAccessor) {");
		lines.add("		super(registerAccessor);");
		lines.add("	}");
		
		// Register name enum
		lines.add("	public enum Register {" );
		holder.getRegisterList().forEach(register -> {
			lines.add("		" + createClassName(register.getName()) + ", ");
		});
		lines.add("	}");
		
		// each register
		holder.getRegisterList().forEach(register -> {
			String name = createClassName(register.getName());
			lines.add("	public enum " + name + " {");
			for (RegisterBit bit : register.getBits().getBits()) {
				lines.add("		" + createClassName(bit.getName()) + ", ");
			}
			lines.add("	}");
			
			lines.add("	public RuntimeRegister<" + name + "> " + createInstanceName(register.getName()) + " = new RuntimeRegister<>(\"" + name + "\", accessor);");
			
		});

		lines.add("}");
		return lines;
	}

	private String createInstanceName(String name) {
		return StringUtils.uncapitalize(name.replaceAll("[^A-Za-z0-9]", "_"));
	}

	private String createClassName(String name) {
		return StringUtils.capitalize(name.replaceAll("[^A-Za-z0-9]", "_"));
	}
}
