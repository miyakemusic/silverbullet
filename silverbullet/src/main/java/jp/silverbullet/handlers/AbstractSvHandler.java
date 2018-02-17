package jp.silverbullet.handlers;

import java.util.List;

import jp.silverbullet.ChangedItemValue;

public abstract class AbstractSvHandler {
	abstract protected void onExecute(List<ChangedItemValue> list, SvHandlerModel model2);
//	abstract public boolean matches(String id);
	
	private SvHandlerModel model;
	public AbstractSvHandler(SvHandlerModel model) {
		this.model = model;
	}
	public void execute(List<ChangedItemValue> list) {
		onExecute(list, model);
	}
	public SvHandlerModel getModel() {
		return model;
	}

	
	
}
