package jp.silverbullet.handlers;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class HandlerPropertyHolder {

	private List<HandlerProperty> handlers = new ArrayList<HandlerProperty>();

	public List<HandlerProperty> getHandlers() {
		return handlers;
	}

	public void setHandlers(List<HandlerProperty> handlers) {
		this.handlers = handlers;
	}

	public HandlerProperty addHandler(String name, String description, Boolean async, String id) {
		HandlerProperty handler = new HandlerProperty(name, description, async, id);
		this.handlers.add(handler);
		return handler;
	}

	public void removeHanlder(HandlerProperty handler) {
		this.handlers.remove(handler);
	}

	public void addAll(HandlerPropertyHolder tmpHandler) {
		this.handlers.addAll(tmpHandler.getHandlers());
	}

	public List<HandlerProperty> remove(int row) {
		this.handlers.remove(row);
		return this.handlers;
	}

	public void addHandler(boolean async, String id, Class<?> class1) {
		HandlerProperty handler = new HandlerProperty();
		handler.setAsync(async);
		handler.addId(id);
		String classMethod = class1.getSimpleName().replaceAll(".class", ".handle");
		
		handler.setExternalClass(classMethod);
		this.handlers.add(handler);
	}
	
}
