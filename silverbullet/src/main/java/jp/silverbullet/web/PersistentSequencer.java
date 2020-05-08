package jp.silverbullet.web;

import java.util.List;
import java.util.Map;

import jp.silverbullet.core.dependency2.ChangedItemValue;
import jp.silverbullet.core.dependency2.RequestRejectedException;
import jp.silverbullet.core.sequncer.SvHandlerModel;
import jp.silverbullet.core.sequncer.UserSequencer;

abstract public class PersistentSequencer implements UserSequencer {

	@Override
	public void handle(SvHandlerModel model, Map<String, List<ChangedItemValue>> changed)
			throws RequestRejectedException {

		new GoogleDrivePost().filename(getFile()).post(getAccessToken(), getPath());

	}

	protected abstract String getPath();

	protected abstract String getAccessToken();

	protected abstract String getFile();

}
