package jp.silverbullet.dependency.speceditor3.ui;

import javafx.scene.layout.VBox;
import jp.silverbullet.dependency.speceditor3.DependencyTargetElement;
import jp.silverbullet.dependency.speceditor3.GlobalMap;

public class DependencyEditorUi extends VBox {
	DependencySpecEditorUi dependencySpecEditorUi = null;
	public DependencyEditorUi(DependecyEditorModel dependencyEditorModel) {
		dependencyEditorModel.addtListener(new DependecyEditorModelListener() {
			@Override
			public void onSpecUpdate() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSelectionChanged(String id) {
				dependencyEditorModel.setProperty(dependencyEditorModel.getProperty(id));
				dependencySpecEditorUi.update();
			}

			public void onRequestAdd(String id, DependencyTargetElement dependencyTargetElement, String selectionId) {
				dependencySpecEditorUi.requestAdd(id, dependencyTargetElement, selectionId);
			}
		});
		
		this.getChildren().add(new DependencyTreeUi(dependencyEditorModel));
		this.getChildren().add(dependencySpecEditorUi = new DependencySpecEditorUi(dependencyEditorModel));
		this.getChildren().add(new GlobalMapUi(new GlobalMap(dependencyEditorModel.getDependencySpecHolder())));
	}
}
