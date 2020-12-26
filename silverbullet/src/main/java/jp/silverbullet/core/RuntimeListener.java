package jp.silverbullet.core;

import jp.silverbullet.core.sequncer.SystemAccessor.DialogAnswer;
import jp.silverbullet.dev.ControlObject;

public interface RuntimeListener {

	DialogAnswer dialog(String message);

	void onReply(String messageId, String reply);

	void message(String html, ControlObject controls);

}
