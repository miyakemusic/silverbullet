package jp.silverbullet.dependency.ui;

import javafx.scene.layout.VBox;
import jp.silverbullet.dependency.DependencyTargetElement;
import jp.silverbullet.dependency.GlobalMap;

public class DependencyEditorUi extends VBox {
	DependencySpecEditorUi dependencySpecEditorUi = null;
	private GlobalMap globalMap;
	public DependencyEditorUi(DependencyEditorModel dependencyEditorModel) {
		dependencyEditorModel.addtListener(new DependecyEditorModelListener() {
			@Override
			public void onSpecUpdate() {
				globalMap.update();
			}

			@Override
			public void onSelectionChanged(String id) {
				dependencyEditorModel.setProperty(dependencyEditorModel.getProperty(id));
				dependencySpecEditorUi.update();
				globalMap.setSelectedId(id);
			}

			public void onRequestAdd(String id, DependencyTargetElement dependencyTargetElement, String selectionId) {
				dependencySpecEditorUi.requestAdd(id, dependencyTargetElement, selectionId);
			}
		});
		
		this.getChildren().add(new GlobalMapUi(globalMap = new GlobalMap(dependencyEditorModel)));
		this.getChildren().add(new DependencyTreeUi(dependencyEditorModel));
		
		this.getChildren().add(dependencySpecEditorUi = new DependencySpecEditorUi(dependencyEditorModel));
		this.getChildren().add(new PropertySpecUi(dependencyEditorModel));
		
		
		globalMap.addListener(new GlobalMapListener() {
			@Override
			public void onIdChange(String id) {
				dependencyEditorModel.setProperty(dependencyEditorModel.getProperty(id));
				dependencySpecEditorUi.update();
			}

			@Override
			public void onUpdated() {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
}
