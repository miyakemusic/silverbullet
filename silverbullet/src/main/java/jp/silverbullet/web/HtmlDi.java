package jp.silverbullet.web;

import java.util.List;

public interface HtmlDi {

	String getTitle(String id);

	List<HtmlOptionInfo> getOptionInfo(String id);

	String getValue(String id);

	String getSelectedId(String id);

}
