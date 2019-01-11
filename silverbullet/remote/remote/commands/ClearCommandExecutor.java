package jp.silverbullet.remote.commands;

import java.util.List;

import jp.silverbullet.remote.engine.AbstractCommandExecutor;
import jp.silverbullet.remote.engine.RemoteCommandDi;

public class ClearCommandExecutor extends AbstractCommandExecutor {

	public ClearCommandExecutor(RemoteCommandDi model2) {
		super(model2, "*CLS");
	}

	@Override
	protected String execute(String command, List<String> params) {
		getModel().getErrors().clear();
		return "";
	}

}
