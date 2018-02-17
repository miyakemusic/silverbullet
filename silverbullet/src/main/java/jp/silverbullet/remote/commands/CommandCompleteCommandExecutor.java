package jp.silverbullet.remote.commands;

import java.util.List;

import jp.silverbullet.remote.engine.AbstractCommandExecutor;
import jp.silverbullet.remote.engine.RemoteCommandDi;

public class CommandCompleteCommandExecutor extends AbstractCommandExecutor {

	public CommandCompleteCommandExecutor(RemoteCommandDi model2) {
		super(model2, "*OPC?");
	}

	@Override
	protected String execute(String command, List<String> params) {
		getModel().waitComplete();
		clearSyncCondition();
		return "1";
	}

	protected void clearSyncCondition() {
		getModel().clearSyncCondition();
	}

}
