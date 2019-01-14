package jp.silverbullet.sequncer;

import java.util.List;
import java.util.Map;

import jp.silverbullet.dependency2.ChangedItemValue;
import jp.silverbullet.dependency2.RequestRejectedException;

public interface UserSequencer {

	void handle(SvHandlerModel model, Map<String, List<ChangedItemValue>> changed) throws RequestRejectedException;
	List<String> targetIds();

}