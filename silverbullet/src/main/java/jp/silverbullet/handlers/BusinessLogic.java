package jp.silverbullet.handlers;

import java.util.List;
import java.util.Map;

import jp.silverbullet.dependency.ChangedItemValue;

public interface BusinessLogic {
	public void handle(SvHandlerModel model, Map<String, List<ChangedItemValue>> changed);
}
