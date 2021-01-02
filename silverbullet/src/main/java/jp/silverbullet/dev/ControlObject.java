package jp.silverbullet.dev;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ControlObject {
	public static void main(String[] arg) {
		ControlObject controlObject = new ControlObject();
		controlObject.add(new ControlElement(ControlElement.Type.Button, "Close", "close"));
		controlObject.add(new ControlElement(ControlElement.Type.Button, "Next", "next"));
		controlObject.add(new ControlElement(ControlElement.Type.Button, "Prev", "prev"));
		
		try {
			String string = new ObjectMapper().writeValueAsString(controlObject);
			System.out.println(string);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void add(ControlElement controlElement) {
		this.controls.add(controlElement);
	}

	public List<ControlElement> controls = new ArrayList<>();

	public ControlObject ok() {
		this.add(new ControlElement(ControlElement.Type.Button, "OK", "ok"));
		return this;
	}
	public ControlObject canel() {
		this.add(new ControlElement(ControlElement.Type.Button, "Cancel", "cancel"));
		return this;
	}
}
