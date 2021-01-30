package jp.silverbullet.testspec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TsSelectionConfig {

	public List<String> connetros = new ArrayList<>();
	public Map<String, TsTesterInfo> testMethods = new HashMap<>();
	
	public TsSelectionConfig() {

		connetros.add("LC_UPC");
		connetros.add("LC_APC");
		connetros.add("SC_UPC");
		connetros.add("SC_APC");
		connetros.add("FC_UPC");
		connetros.add("FC_APC");
		connetros.add("MPO_8");
		connetros.add("MPO_12");
		connetros.add("MPO_16");
		connetros.add("ODC_2");
		connetros.add("ODC_4");
		connetros.add("Q_ODC_12");
		connetros.add("Q_ODC_24");
		connetros.add("Corning_OptiTip");
		connetros.add("Corning_OptiTap");
		
		String folder = "C:/Users/miyak/OneDrive/openti/results/";
		
		String device = "MT1041A";
		{
			List<String> script = new ArrayList<>();
			script.add("sb.write(" + device + ", 'ID_APPLICATION=ID_APPLICATION_VIP');");
			script.add("sb.sleep(1000);");
			script.add("sb.write(" + device + ", 'ID_VIPCONTROL=ID_VIPCONTROL_START');");
			script.add("sb.sleep(1000);");
			script.add("sb.waitEqual(" + device + ", 'ID_VIPCONTROL', 'ID_VIPCONTROL_STOP');");
			script.add("sb.write(" + device + ", 'ID_AUTO_FILENAME=false');");
			script.add("sb.write(" + device + ", 'ID_FILE_NAME=" + folder + "%NODEID%.%PORTID%.%DIRECTION%.%SIDE%.%METHOD%');");
			script.add("sb.write(" + device + ", 'ID_SAVE_VIP=true');");
			String message = "<div><img src=\"https://keytech.ntt-at.co.jp/optic1/images/p0030_4.jpg\" width=\"400\" height=\"200\" ></div>";			
			
			testMethods.put("Fiber end-face inspection", new TsTesterInfo(script, message));
		}
		{
			List<String> script = new ArrayList<>();
			script.add("sb.write(" + device + ", 'ID_APPLICATION=ID_APPLICATION_OLTS');");
			script.add("sb.sleep(1000);");
			script.add("sb.write(" + device + ", 'ID_LS_ENABLED=ID_LS_ENABLED_ON');");
			script.add("sb.sleep(3000);");
			script.add("sb.write(" + device + ", 'ID_LS_ENABLED=ID_LS_ENABLED_OFF');");
			script.add("sb.write(" + device + ", 'ID_AUTO_FILENAME=false');");
			script.add("sb.write(" + device + ", 'ID_FILE_NAME=" + folder + "%NODEID%.%PORTID%.%DIRECTION%.%SIDE%.%METHOD%');");
			script.add("sb.write(" + device + ", 'ID_SAVE_OLTS=true');");
			
			String message = "";
			testMethods.put("Optical Power Meter", new TsTesterInfo(script, message));
		}
		{
			List<String> script = new ArrayList<>();
			script.add("sb.write(" + device + ", 'ID_APPLICATION=ID_APPLICATION_OTDR');");
			script.add("sb.sleep(1000);");
			script.add("sb.write(" + device + ", 'ID_OTDR_TESTCONTROL=ID_OTDR_TESTCONTROL_START');");
			script.add("sb.sleep(1000);");
			script.add("sb.waitEqual(" + device + ", 'ID_OTDR_TESTCONTROL', 'ID_OTDR_TESTCONTROL_STOP');");
//			script.add("sb.write(" + device + ", 'ID_FILE_FOLDER=" + folder + "');");
			script.add("sb.write(" + device + ", 'ID_AUTO_FILENAME=false');");
			script.add("sb.write(" + device + ", 'ID_FILE_NAME=" + folder + "%NODEID%.%PORTID%.%DIRECTION%.%SIDE%.%METHOD%');");
			script.add("sb.write(" + device + ", 'ID_FILE_SAVE=true');");
			
			String message = "<img src=\"https://dl.cdn-anritsu.com/images/products/tm-mt9085a-b-c/mt9085-e.png?h=310&w=420\">";
			
			testMethods.put("OTDR", new TsTesterInfo(script, message));
		}
	}

	public String message(String testMethod) {
		return this.testMethods.get(testMethod).getMessage();
	}

	public List<String> script(String testMethod) {
		return this.testMethods.get(testMethod).getScript();
	}

	public List<String> testMethods() {
		return new ArrayList<String>(testMethods.keySet());
	}
}
