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
	
	public void test() {
		//ScriptManager es = new ScriptManager();
        // create a script engine manager
		ScriptManager sb = this;
        ScriptEngineManager factory = new ScriptEngineManager();
        // create a JavaScript engine
        ScriptEngine engine = factory.getEngineByName("JavaScript");
        // evaluate JavaScript code from String
  //     engine.eval("println('Welcome to Java world')");

        // add the Java object into the engine.
        engine.put("sb",sb);

        ScriptEngineFactory sef = engine.getFactory();
        String s = sef.getMethodCallSyntax("sb", "request('AAAA')", new String[0]);
        // show the correct way to call the Java method
        System.out.println(s);
        try {
			engine.eval(s);
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
    
    abstract public void message(String addr, String message);
}
