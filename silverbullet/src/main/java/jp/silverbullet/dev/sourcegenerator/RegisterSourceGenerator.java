package jp.silverbullet.dev.sourcegenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import jp.silverbullet.core.register2.RegisterBit;
import jp.silverbullet.core.register2.RegisterCommon;
import jp.silverbullet.core.register2.RegisterSpecHolder;

public class RegisterSourceGenerator {

	private RegisterSpecHolder holder;
	private String className;
	private String idClassName;

	public RegisterSourceGenerator(RegisterSpecHolder holder, String app) {
		this.holder = holder;
		idClassName = app.substring(0, 1).toUpperCase() + app.substring(1);
		this.className = idClassName + "UserRegister";
	}

	public void exportFile(String filename, String packageName) {
		List<String> lines = this.generate(packageName);
		String path = combineFolderPackage(filename, packageName);
		try {
			String name = path + "/" + this.className + ".java";
			if (Files.exists(Paths.get(name))) {
				Files.delete(Paths.get(name));
			}
			Files.write(Paths.get(name), lines, StandardOpenOption.CREATE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String combineFolderPackage(String filename, String packageName) {
		String path = filename + "/" + packageName.replace(".", "/");
		return path;
	}
	
	public List<String> generate(String packageName) {
		List<String> lines = new ArrayList<>();
		// package
		lines.add("package " + packageName + ";"); 
		
		// import
		lines.add("import jp.silverbullet.core.register2.RegisterAccessor;");
		lines.add("import jp.silverbullet.core.register2.RuntimeRegister;");
		lines.add("import jp.silverbullet.core.register2.RuntimeRegisterHolder;");
		
		lines.add("public class " + className + " extends RuntimeRegisterHolder {");
		//  constructor
		lines.add("	public " + className + "(RegisterAccessor registerAccessor) {");
		lines.add("		super(registerAccessor);");
		lines.add("	}");
		
		// Register name enum
		lines.add("	public enum Register {" );
		holder.getRegisterList().forEach(register -> {
			lines.add("		" + RegisterCommon.createClassName(register.getName()) + ", ");
		});
		lines.add("	}");
		
		// each register
		holder.getRegisterList().forEach(register -> {
			String name = RegisterCommon.createClassName(register.getName());
			lines.add("	public enum " + name + " {");
			for (RegisterBit bit : register.getBits().getBits()) {
				lines.add("		" + RegisterCommon.createClassName(bit.getName()) + ", ");
			}
			lines.add("	}");
			
			lines.add("	public RuntimeRegister<" + name + "> " + RegisterCommon.createInstanceName(register.getName()) + " = new RuntimeRegister<>(Register." + name + ", accessor);");
			
		});

		lines.add("}");
		return lines;
	}


}
