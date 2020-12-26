package jp.silverbullet.dev;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JOptionPane;

public abstract class ScriptManager {

	public ScriptManager() {

	}
	
	public void start(List<String> lines) {
//		File script = new File("C:\\Users\\miyak\\git\\openti\\openti\\target\\script2.js");
		try {
			Files.write(Paths.get("tmp.js"), lines);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		File script = new File("tmp.js");
		try {
			Reader reader = new FileReader(script);
			ScriptManager sb = this;
	        ScriptEngineManager factory = new ScriptEngineManager();
	        // create a JavaScript engine
	        ScriptEngine engine = factory.getEngineByName("JavaScript");
	        engine.put("sb",sb);
	        try {
				engine.eval(reader);
			} catch (ScriptException e) {
				e.printStackTrace();
			}
	        
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

	}
    abstract public void write(String addr, String command);
    
    abstract public  String read(String addr, String query);
    
    abstract public  String waitEqual(String addr, String id, String value);
    
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
