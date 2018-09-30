package jp.silverbullet.trashdependency.engine;

import java.util.List;

import jp.silverbullet.SvProperty;

public interface SvCalculatorModel {
	List<String> getAllIds();

	String getCurrentValue(String id);

}