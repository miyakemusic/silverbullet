package jp.silverbullet.testspec;

import java.util.ArrayList;
import java.util.List;

public class TsPortConfig {
	public String connector = TsConnectorType.NOT_SPECIFIED.name();
	public List<String> leftSideTest = new ArrayList<>();
	public List<String> rightSideTest = new ArrayList<>();
}
