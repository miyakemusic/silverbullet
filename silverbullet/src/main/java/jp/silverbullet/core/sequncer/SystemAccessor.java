package jp.silverbullet.core.sequncer;

import jp.silverbullet.core.property2.SvFileException;
import jp.silverbullet.dev.ControlObject;

public interface SystemAccessor {

	void saveProperties(String fileName) throws SvFileException;

	void loadProperties(String fileName) throws SvFileException;

	public enum DialogAnswer {
		OK,
		Cancel
	}
	DialogAnswer dialog(String string);

	void message(String string, ControlObject controls);

}
