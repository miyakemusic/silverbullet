package jp.silverbullet;

import jp.silverbullet.sequncer.SystemAccessor.DialogAnswer;

public interface RuntimeListener {

	DialogAnswer dialog(String message);

	void onReply(String messageId, String reply);

	void message(String message);

}
