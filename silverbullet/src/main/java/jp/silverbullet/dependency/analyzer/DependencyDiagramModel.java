package jp.silverbullet.dependency.analyzer;

import java.util.List;

import jp.silverbullet.dependency.speceditor2.DependencySpec;
import jp.silverbullet.dependency.speceditor2.DependencySpecDetail;

public interface DependencyDiagramModel {

	List<DependencySpecDetail> getPassiveDependencySpecDetail(String id);

	List<DependencySpecDetail>  getActiveDependencySpecDetail(String passiveId);

	DependencySpec getDependencySpec();
}
