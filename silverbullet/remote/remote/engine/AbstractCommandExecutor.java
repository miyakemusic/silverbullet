package jp.silverbullet.remote.engine;

import java.util.List;

public abstract class AbstractCommandExecutor {
	abstract protected String execute(String command2, List<String> params);
	
	private String command = "";
	private RemoteCommandDi model;
	
	public AbstractCommandExecutor(RemoteCommandDi model2, String command2) {
		this.model = model2;
		this.command = command2;
	}
	public RemoteCommandDi getModel() {
		return model;
	}
	public boolean matches(String command2) {
		return this.command.equalsIgnoreCase(command2);
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}
	
}
