package jp.silverbullet.dev;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public abstract class ScriptManager {

	public ScriptManager() {

	}
	
	public void start(List<String> lines) {
//		try {
//			Files.write(Paths.get("tmp.js"), lines);
//		} catch (IOException e2) {
//			e2.printStackTrace();
//		}
//		File script = new File("tmp.js");
		StringBuffer buffer = new StringBuffer();
		lines.forEach(line -> buffer.append(line + "\n"));
		try {
			Reader reader = new StringReader(buffer.toString());//new FileReader(script);
			ScriptManager sb = this;
	        ScriptEngineManager factory = new ScriptEngineManager();
	        // create a JavaScript engine
	        ScriptEngine engine = factory.getEngineByName("JavaScript");
			if (engine == null) { // for Android
				engine = new ScriptEngineManager().getEngineByName("rhino");
			}
	        engine.put("sb",sb);
	        try {
				engine.eval(reader);
			} catch (ScriptException e) {
				e.printStackTrace();
			}
	        
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}
    abstract public void write(String addr, String command);
    
    abstract public  String read(String addr, String query);
    
	public String waitEqual(String addr, String id, String value) {
		for (int i = 0; i < 100; i++) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {

			}
			String ret = read(addr, id);
			if (ret.equals(value)) {
				break;
			}
		}
		return "";
	}
    
    public void sleep(int millisecond) {
    	try {
			Thread.sleep(millisecond);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    abstract public   void debug(String arg);
    
    public void request(String arg){
        System.out.println(arg);
    }
    
    abstract public String message(String addr, String message, String controls);

}
