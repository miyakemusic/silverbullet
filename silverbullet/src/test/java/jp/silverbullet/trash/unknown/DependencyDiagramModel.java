package jp.silverbullet.trash.dependency.analyzer;

import java.util.List;

import jp.silverbullet.trash.speceditor2.DependencySpec;
import jp.silverbullet.trash.speceditor2.DependencySpecDetail;

public interface DependencyDiagramModel {

	List<DependencySpecDetail> getPassiveDependencySpecDetail(String id);

	List<DependencySpecDetail>  getActiveDependencySpecDetail(String passiveId);

	DependencySpec getDependencySpec();
}
