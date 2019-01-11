package jp.silverbullet.remote.commands;

import java.util.List;

import jp.silverbullet.remote.engine.AbstractCommandExecutor;
import jp.silverbullet.remote.engine.RemoteCommandDi;
import jp.silverbullet.remote.engine.RemoteError;

public class ErrorCommandExecutor extends AbstractCommandExecutor {

	public ErrorCommandExecutor(RemoteCommandDi model2) {
		super(model2, "*ERR?");
	}

	@Override
	protected String execute(String command, List<String> params) {
		RemoteError ret = getModel().getErrors().poll();
		if (ret != null) {
			return ret.getMessage();
		}
		else {
			return "NO ERROR";
		}
	}

}
