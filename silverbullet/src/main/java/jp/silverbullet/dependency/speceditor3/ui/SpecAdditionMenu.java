package jp.silverbullet.dependency.speceditor3.ui;

import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import jp.silverbullet.MyDialogFx;
import jp.silverbullet.property.editor.PropertyEditorPaneFx;

public class SpecAdditionMenu {

//	private DependencyEditorModel dependencyEditorModel;

	public SpecAdditionMenu(Pane pane, ContextMenu contextMenu, DependencyEditorModel dependencyEditorModel) {
		//this.dependencyEditorModel = dependencyEditorModel;
		MenuItem targetMenu = new MenuItem("Add target");
		targetMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				showIdSelector(pane, dependencyEditorModel);
			}
		});
		contextMenu.getItems().addAll(targetMenu);
		pane.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getButton().equals(MouseButton.SECONDARY)) {
					showPopup(pane, contextMenu, event);
				}
			}
		});
	}
	
	protected void showIdSelector(Pane pane, DependencyEditorModel dependencyEditorModel) {
		MyDialogFx dialog = new MyDialogFx("ID", pane);
		final PropertyEditorPaneFx node = new PropertyEditorPaneFx(dependencyEditorModel.getPropertyHolder()) {
			@Override
			protected void onClose() {
				removeListener();
				dialog.close();
			}

			@Override
			protected void onSelect(List<String> selected, List<String> subs) {
				removeListener();
				String id = selected.get(0);
				showElementSelector(dependencyEditorModel, pane, id);
			}
		};

		dialog.showModal(node);
	}

	protected void showElementSelector(DependencyEditorModel dependencyEditorModel, Pane pane, String id) {
		MyDialogFx dialog = new MyDialogFx("Target Element", pane);
		dialog.setMaxHeight(200);
		dialog.setMaxWidth(400);
		VBox vbox = new VBox();
		ComboBox<String> combo = new ComboBox<>();
		combo.getItems().addAll(dependencyEditorModel.getAllElements(id));
		vbox.getChildren().add(combo);
		dialog.showModal(vbox);
		if (dialog.isOkClicked()) {
			DependencyTargetConverter e = dependencyEditorModel.getRealTargetElement(combo.getSelectionModel().getSelectedItem());

			dependencyEditorModel.requestAddingSpec(id, e.getElement(), e.getSelectionId());
		}
	}
	protected void showPopup(Pane pane, ContextMenu contextMenu,
			MouseEvent event) {
		contextMenu.show(pane, event.getScreenX(), event.getScreenY());
	}
}
