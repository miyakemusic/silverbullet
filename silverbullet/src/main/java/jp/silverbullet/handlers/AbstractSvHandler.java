package jp.silverbullet.handlers;

import java.util.List;
import java.util.Map;

import jp.silverbullet.ChangedItemValue;

public abstract class AbstractSvHandler {
	abstract protected void onExecute(SvHandlerModel model2, Map<String, List<ChangedItemValue>> changed);
	
	private SvHandlerModel model;
	public AbstractSvHandler(SvHandlerModel model) {
		this.model = model;
	}
	public void execute(Map<String, List<ChangedItemValue>> changed) {
		onExecute(model, changed);
	}
	public SvHandlerModel getModel() {
		return model;
	}

	
	
}
