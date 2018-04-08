package jp.silverbullet.handlers;

import java.util.List;
import java.util.Map;

import jp.silverbullet.dependency.speceditor3.ChangedItemValue2;

public abstract class AbstractSvHandler {
	abstract protected void onExecute(SvHandlerModel model2, Map<String, List<ChangedItemValue2>> changed);
	
	private SvHandlerModel model;
	public AbstractSvHandler(SvHandlerModel model) {
		this.model = model;
	}
	public void execute(Map<String, List<ChangedItemValue2>> changed) {
		onExecute(model, changed);
	}
	public SvHandlerModel getModel() {
		return model;
	}

	
	
}
