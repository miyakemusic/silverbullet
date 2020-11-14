package jp.silverbullet.core.sequncer;

import java.util.List;
import java.util.Map;

import jp.silverbullet.core.dependency2.ChangedItemValue;
import jp.silverbullet.core.dependency2.Id;
import jp.silverbullet.core.dependency2.RequestRejectedException;

public interface UserSequencer {

	void handle(SvHandlerModel model, Map<String, List<ChangedItemValue>> changed, Id sourceId) throws RequestRejectedException;
	List<String> targetIds();
}