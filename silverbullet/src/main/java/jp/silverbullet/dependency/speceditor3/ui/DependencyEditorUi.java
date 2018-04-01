package jp.silverbullet.dependency.speceditor3.ui;

import javafx.scene.layout.VBox;
import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.speceditor3.DependencySpecHolder2;
import jp.silverbullet.property.PropertyHolder;

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
		});
		
		this.getChildren().add(new DependencyTreeUi(dependencyEditorModel));
		this.getChildren().add(dependencySpecEditorUi = new DependencySpecEditorUi(dependencyEditorModel));
	}
}
