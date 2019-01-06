package jp.silverbullet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.silverbullet.handlers.RegisterAccess;
import jp.silverbullet.register.RangeGetter;
import jp.silverbullet.register.SvSimulator;
import jp.silverbullet.register2.RegisterBit;
import jp.silverbullet.register2.RegisterSpecHolder;
import jp.silverbullet.register2.SvRegister;
import jp.silverbullet.register2.RegisterBit.ReadWriteType;

public class RegisterIoGenerator {

	private RegisterSpecHolder registerProperty;
	private Map<String, String> addressNameMap = new HashMap<>();
	private String userPackage;
	
	public RegisterIoGenerator(RegisterSpecHolder registerProperty, String userPackage) {
		this.registerProperty =registerProperty;
		this.userPackage = userPackage;
	}

	abstract class AbstractSourceGenerator {
		public void generate(String className, String path) {
			List<String> source = new ArrayList<String>();
			
			source.add("package " + path + ";");
			source.add("import " + RegisterAccess.class.getName() + ";");
			source.add("import jp.silverbullet.register.RegisterControl;");
			source.add("public class " + className + " extends RegisterControl {");
			for (SvRegister register : registerProperty.getRegisterList()) {
				String registerAddressDef = "ADDR_" + convertName(register.getName()).toUpperCase();
				addressNameMap.put(register.getAddress(), registerAddressDef);
				source.add("    public static int " + registerAddressDef + " = " + convertAddress(register.getAddress()) + ";");
				for (RegisterBit bit : register.getBits().getBits()) {
					if (bit.isSingle() && !bit.getType().equals(ReadWriteType.UNUSED)) {
						//source.add("    public static int BIT_" + convertName(register.getName().toUpperCase() + "_" + convertName(bit.getName()).toUpperCase()+ " = " + bit.getBit() + ";"));						
						source.add("    public static int BIT_" + convertName(register.getName() + "_" + bit.getName()).toUpperCase()+ " = " + convertName(bit.getBit()) + ";");						
						
					}
				}
			}
			source.add("    private RegisterAccess registerAccess = null;");
			source.add("    public RegisterAccess getRegisterAccess() {");
			source.add("    	return registerAccess;");
			source.add("    }");
			source.add("    public " + className + "(RegisterAccess registerAccess2) {");
			source.add("         super(registerAccess2);");
			source.add("         this.registerAccess = registerAccess2;");
			source.add("    }");
			
			for (SvRegister register : registerProperty.getRegisterList()) {
				if (register.isBlock()) {
					continue;
				}
				
				String regClassName = getClassName(register.getName());
				source.add("    public class " + regClassName + "{");

				for (RegisterBit bit : register.getBits().getBits()) {
					if (convertName(bit.getName()).equals("Reserve")) {
						continue;
					}
					if (isReadEnabled(bit)) {
						generateRead(source, bit, register, isWriteEnabled(bit));
					}
					if (isWriteEnabled(bit)) {
						generateWrite(source, bit, register);
					}
				}
				
				source.add("    }");
				source.add("    public " + regClassName + " " + 
						regClassName.replaceFirst(String.valueOf(regClassName.charAt(0)), String.valueOf(regClassName.charAt(0)).toLowerCase())
						+ " = new " + getClassName(register.getName()) + "();");
			}

			source.add("}");
			
			try {
				String path2 = "src/main/java/" + path.replace(".", "/");// + "/handlers/user/";
				Files.write(Paths.get(path2 + "/" + className + ".java"), source, StandardCharsets.UTF_8);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		abstract protected boolean isWriteEnabled(RegisterBit bit);

		abstract protected boolean isReadEnabled(RegisterBit bit);
	}
	
	public void generate(String path) {
		new AbstractSourceGenerator() {
			@Override
			protected boolean isWriteEnabled(RegisterBit bit) {
				return bit.getType().equals(RegisterBit.ReadWriteType.WO) || bit.getType().equals(RegisterBit.ReadWriteType.RW);
			}
			
			@Override
			protected boolean isReadEnabled(RegisterBit bit) {
				return bit.getType().equals(RegisterBit.ReadWriteType.RO) || bit.getType().equals(RegisterBit.ReadWriteType.RW);
			}	
		}.generate("UserRegisterControl", path);
		
		new AbstractSourceGenerator() {
			@Override
			protected boolean isWriteEnabled(RegisterBit bit) {
				return true;
			}
			
			@Override
			protected boolean isReadEnabled(RegisterBit bit) {
				return true;
			}	
		}.generate("SimRegisterControl", path);
		
		generateSimulator(path);
	}

	public String convertAddress(String address) {
		if (address.contains("-")) {
			return address.split("-")[0];
		}
		
		return address;
	}

	private String getClassName(String name) {
		String s = name.replace("(", "").replace(")", "").replace("/", "").replace(".", "").replace("[", "").replace("]", "");
		String[] tmp = s.split("[\\s_+]");
		String ret = "";
		for (String ss : tmp) {
			for (int i = 0; i < ss.length(); i++) {
				Character c = ss.charAt(i);
				if (i == 0) {
					ret += String.valueOf(c).toUpperCase();
				}
				else {
					ret += String.valueOf(c).toLowerCase();
				}
			}
		}
		return ret;
	}

	private void generateWrite(List<String> source, RegisterBit bit, SvRegister register) {
		createComment(source, bit);
		source.add("        public void write_" + createMethodName(register, bit) + "(" + getType(bit) + " value) {");
		String[] tmp = getBitRange(bit);
		String bit2 = tmp[0];
		String bit1 = "";
		if (tmp.length > 1) {
			bit1 = tmp[1] + ", ";
		}
		source.add("            writeIo(" + getAddressDef(register.getAddress()) + ", value, " + bit1 + bit2 + ");");
		source.add("        }");
	}

	private String getAddressDef(String address) {
		String ret = this.addressNameMap.get(address);
		if (ret == null) {
			ret = address;
		}
		return ret;
	}

	private void generateRead(List<String> source, RegisterBit bit, SvRegister register, boolean writable) {
		createComment(source, bit);
		source.add("        public " + getType(bit) + " read_" + createMethodName(register, bit) + "() {");
		source.add("    	    return " + readIo(bit, getAddressDef(register.getAddress())) + ";");
		source.add("        }");
		
		if (writable) {
			String type = getType(bit);
			String value = "";
			if (type.equals("boolean")) {
				value = "false";
			}
			else if (type.equals("int")) {
				value = "0";
			}
			source.add("        public " + type + " read_and_reset_" + createMethodName(register, bit) + "() {");
			source.add("    	    " + type + " ret = " + readIo(bit, getAddressDef(register.getAddress())) + ";");
			source.add("             write_" + createMethodName(register, bit) + "(" + value + ");");
			source.add("    	    return ret;");
			source.add("        }");
		}
	}

	protected String createMethodName(SvRegister register, RegisterBit bit) {
		String s = /*convertName(register.getName()) + "_" +*/ convertName(bit.getName());
		return s.replace("[", "_").replace("]", "_").replace(":", "_").replace(".", "_").replace(" ", "");
	}
	
	protected void createComment(List<String> source, RegisterBit bit) {
		source.add("    /**");
		for (String s : bit.getDescription().split(";")) {
			source.add("    *   " + s.replace("\n", ""));		
		}
		source.add("    **/");
	}


	private String readIo(RegisterBit bit, String address) {
		if (getType(bit).equals("boolean")) {
			return "readIoBoolean(" + getAddressDef(address) + ", " + bit.getBit().replace("[", "").replace("]", "")+ ")";
		}
		else {
			String[] tmp = getBitRange(bit);
			return "readIoInteger(" + getAddressDef(address) + ", " + tmp[0] + "," + tmp[1] + ")";
		}
	}

	protected String[] getBitRange(RegisterBit bit) {
		String[] tmp = bit.getBit().replace("[", "").replace("]", "").split(":");
		return tmp;
	}

	private String getType(RegisterBit bit) {
		String[] tmp = bit.getDefinition().replace("\n", "").split(";");
		String argument = "";
		if (tmp[0].equals("<BOOLEAN>") || !bit.getBit().contains(":")) {
			argument = "boolean";
		}
		else if (tmp[0].equals("<FLOAT>")) {
			argument = "float";
		}
		else /* if (tmp[0].equals("<INTEGER>")) */{
			argument = "int";
		}
		return argument;
	}
	
	abstract class SimSourceGenerator {
		public List<String> generate(String method) {
			List<String> source = new ArrayList<String>();
			//source.add("	public void writeIo(long address, BitSet data, BitSet mask) {");
			source.add("	public void " + method + " {");
			int regCount = 0;
			for (SvRegister register : registerProperty.getRegisterList()) {
				if (regCount++ == 0) {
					source.add("		if (address == SimRegisterControl." + getAddressDef(register.getAddress()) + ") {");
				}
				else {
					source.add("		else if (address == SimRegisterControl." + getAddressDef(register.getAddress()) + ") {");
				}
				int bitCount = 0;
				getAddressMethod(source, register);
				getBitMethod(source, register, bitCount);
				source.add("		}");
			}
			source.add("	}");
			return source;
		}

		abstract protected void getBitMethod(List<String> source, SvRegister register,
				int bitCount);

		abstract protected void getAddressMethod(List<String> source, SvRegister register);

		abstract protected boolean isEnabled(RegisterBit bit);
	}
	private void generateSimulator(String path) {
		List<String> source = new ArrayList<String>();
		source.add("package " + userPackage + ";");
		source.add("import java.util.BitSet;");
		source.add("import " + SvSimulator.class.getName() + ";");
		source.add("abstract public class AbstractUserSimulator extends SvSimulator {");
		for (SvRegister register : registerProperty.getRegisterList()) {
			for (RegisterBit bit : register.getBits().getBits()) {
//				if (bit.getName().equals("Reserve")) {
//					bit.setType(ReadWriteType.UNUSED);
//				}
				if (bit.isWriteEnabled()) {
					source.add("    public void write_" + convertName(register.getName()) + "_" + convertName(bit.getName()) + "(int value) {};");
				}
//				if (bit.isReadEnabled()) {
//					source.add("    public void read_" + convertName(bit.getName()) + "();");
//				}
			}
			source.add("    public void read_" + convertName(register.getName()) + "_" + convertName(register.getName()) + "() {};");
		}
			
		source.addAll(new SimSourceGenerator() {
			protected void getElementMethod(List<String> source, SvRegister register, RegisterBit bit,
					String index) {
				source.add("				int value = getValue(" + index  + ", data, mask);");
				source.add("				write_" + convertName(register.getName()) + "_" + convertName(bit.getName()) + "(" + "value" + ");");
			}
			
			@Override
			protected boolean isEnabled(RegisterBit bit) {
				return bit.isWriteEnabled();
			}
			
			@Override
			protected void getBitMethod(List<String> source, SvRegister register,
					int bitCount) {
				for (RegisterBit bit : register.getBits().getBits()) {
					if (isEnabled(bit)) {
						String index  = getBit(bit.getBit());
						if (bitCount++ == 0) {
							source.add("			if (mask.get(" + index + ")) {");
						}
						else {
							source.add("			else if (mask.get(" + index + ")) {");
						}
						getElementMethod(source, register, bit, index);
						
						source.add("			}");
					}
				}
			}

			@Override
			protected void getAddressMethod(List<String> source,
					SvRegister register) {
				// TODO Auto-generated method stub
				
			}
		}.generate("writeIo(long address, BitSet data, BitSet mask)"));
		
		source.addAll(new SimSourceGenerator() {
			@Override
			protected boolean isEnabled(RegisterBit bit) {
				return bit.isReadEnabled();
			}

			@Override
			protected void getAddressMethod(List<String> source,
					SvRegister register) {
				source.add("				read_" + convertName(register.getName()) + "_" + convertName(register.getName()) + "(" + "" + ");");
				
			}

			@Override
			protected void getBitMethod(List<String> source,
					SvRegister register, int bitCount) {
				// TODO Auto-generated method stub
				
			}
		}.generate("readIo(long address)"));
		
		source.add("}");
		try {
			path = "src/main/java/" + path.replace(".", "/");// + "/handlers/user/";
			Files.write(Paths.get(path + "/AbstractUserSimulator.java"), source, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getBit(String bit) {
		return String.valueOf(new RangeGetter(bit).getStart());
	}

	protected String convertName(String name) {
		return name.replace(" ", "").replace(".", "").replace("[", "").replace("]", "").replace(":", "_").replace("/", "_").replace("(", "_").replace(")", "_");
	}
}
