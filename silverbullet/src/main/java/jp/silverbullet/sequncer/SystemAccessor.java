package jp.silverbullet.sequncer;

import jp.silverbullet.property2.SvFileException;

public interface SystemAccessor {

	void saveProperties(String fileName) throws SvFileException;

	void loadProperties(String fileName) throws SvFileException;

	public enum DialogAnswer {
		OK,
		Cancel
	}
	DialogAnswer dialog(String string);

	void message(String string);

}
