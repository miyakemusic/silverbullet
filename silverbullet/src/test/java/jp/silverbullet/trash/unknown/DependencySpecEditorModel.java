package jp.silverbullet.trash.speceditor2;

import java.util.List;

import jp.silverbullet.SvProperty;
import jp.silverbullet.SvPropertyStore;
import jp.silverbullet.property.PropertyHolder;

public interface DependencySpecEditorModel {

	public SvProperty getProperty();

	public List<DependencySpecDetail> getSpecs(String element);

	public SvPropertyStore getPropertyStore();

	public PropertyHolder getPropertyHolder();

	public DependencySpecHolder getDependencySpecHolder();

	public List<String> getAllNodes();

	public void removeSpec(String id, DependencySpecDetail spec);

	public void addSpec(String id, String title, DependencyFormula formula);

	public void copy(DependencySpecDetail spec, List<String> selected);

	public void copyAll(List<String> selected);

	public void setConfirmRequired(String element);

	public void addOpositeCondition(String title, DependencySpecDetail spec);

}
