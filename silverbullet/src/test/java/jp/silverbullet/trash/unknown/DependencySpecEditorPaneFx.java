package jp.silverbullet.trash.speceditor2;

import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;
import jp.silverbullet.javafx.MyDialogFx;
import jp.silverbullet.property.editor.PropertyEditorPaneFx;

public class DependencySpecEditorPaneFx extends VBox {

	private DependencySpecEditorModel model;
	protected TreeItem<MyNodeItem> currentItem;
	private TreeItem<MyNodeItem> root;

	public DependencySpecEditorPaneFx(DependencySpecEditorModel model) {
		this.model = model;
		HBox hbox = createToolBar();
		this.getChildren().add(hbox);
	
		TreeView<MyNodeItem> treeView = new TreeView<>();
		root = new TreeItem<>();
		root.setExpanded(true);
		this.getChildren().add(treeView);
		treeView.setRoot(root);
		
		updateNodes();
		
		treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<MyNodeItem>>() {
			@Override
			public void changed(
					ObservableValue<? extends TreeItem<MyNodeItem>> observable,
					TreeItem<MyNodeItem> oldItem, TreeItem<MyNodeItem> newItem) {
				currentItem = newItem;
			}
			
		});
	}

	protected void updateNodes() {
		root.getChildren().clear();
		
		for (String node : model.getAllNodes()) {
			root.getChildren().add(new TreeItem<MyNodeItem>(new MyNodeItem(node)));
		}
		
		for (TreeItem<MyNodeItem> item : root.getChildren()) {
			item.setExpanded(true);
			for (DependencySpecDetail d :  model.getSpecs(item.getValue().title)) {
				item.getChildren().add(new TreeItem<MyNodeItem>(new MyNodeItem(d.getSpecification().getSample(), d)));
			}
		}
	}

	protected HBox createToolBar() {
		HBox hbox = new HBox();
		hbox.getChildren().add(createButton("Add Condition", new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				showEquationEditor(null);
			}
		}));
		hbox.getChildren().add(createButton("Remove", new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				removeEquation();
			}
		}));
		hbox.getChildren().add(createButton("Copy to others", new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				copyToOthers();
			}
		}));
		hbox.getChildren().add(createButton("Edit", new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (currentItem.getValue().type.equals(MyNodeItem.Type.Equation)) {
					showEquationEditor(currentItem.getValue());
				}
			}
		}));
		hbox.getChildren().add(createButton("Add Oposite Cond.", new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (currentItem.getValue().type.equals(MyNodeItem.Type.Equation)) {
					addOpositeCondition();
				}
			}
		}));
		hbox.getChildren().add(createButton("Set Confirm", new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				TreeItem<MyNodeItem> selectedItem = currentItem;
				if (selectedItem.getValue().type.equals(MyNodeItem.Type.Equation)) {
					selectedItem = selectedItem.getParent();
				}
				model.setConfirmRequired(selectedItem.getValue().title);
			}
		}));
		return hbox;
	}

	protected void copyToOthers() {
		final MyDialogFx dlg = new MyDialogFx("ID Selector", this);
		PropertyEditorPaneFx node = new PropertyEditorPaneFx(model.getPropertyHolder()) {
			@Override
			protected void onClose() {
				dlg.close();
			}

			@Override
			protected void onSelect(List<String> selected, List<String> subs) {
				if (currentItem.equals(root)) {
					model.copyAll(selected);
				}
				else {
					model.copy(currentItem.getValue().spec, selected);
				}
			}
		};
		dlg.showModal(node);
	}

	protected void removeEquation() {
		model.removeSpec(model.getProperty().getId(), this.currentItem.getValue().spec);
		updateNodes();
	}

	protected void addOpositeCondition() {
		if (currentItem.getValue().type.equals(MyNodeItem.Type.Equation)) {
			model.addOpositeCondition(currentItem.getValue().title, currentItem.getValue().spec);
		}
		updateNodes();
	}
	
	protected void showEquationEditor(final MyNodeItem myNodeItem) {
		final MyDialogFx dialog = new MyDialogFx("Equation Editor", this);
		dialog.setSize(800, 400);
		
		TreeItem<MyNodeItem> selectedItem = currentItem;
		if (selectedItem.getValue().type.equals(MyNodeItem.Type.Equation)) {
			selectedItem = selectedItem.getParent();
		}
		final EquationEditorModel eqModel = new EquationEditorModelImpl(model, selectedItem.getValue().title, myNodeItem);
		
		EquationEditorFx node = new EquationEditorFx(eqModel);

		dialog.setOnHidden(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent arg0) {
				if (dialog.isOkClicked()) {
					if (myNodeItem != null) {
						model.removeSpec(model.getProperty().getId(), currentItem.getValue().spec);
					}
					
					DependencyFormula formula = eqModel.getFormula();
					if (currentItem.getValue().type.equals(MyNodeItem.Type.Equation)) {
						currentItem = currentItem.getParent();
					}

					model.addSpec(model.getProperty().getId(), currentItem.getValue().title, formula);
					updateNodes();
				}
				else {
					
				}
			}
			
		});
		dialog.showModal(node);
	}

	protected Button createButton(String caption, EventHandler<ActionEvent> eventHandler) {
		Button button = new Button(caption);
		button.setOnAction(eventHandler);
		return button;
	}

	public void setModel(DependencySpecEditorModel model) {
		this.model = model;
		this.updateNodes();
	}

	public void update() {
		this.updateNodes();
	}
}
class MyNodeItem {
	public MyNodeItem(String title) {
		this.title = title;
		this.type = Type.Item;
	}
	public MyNodeItem(String title, DependencySpecDetail spec) {
		this.title = title;
		this.spec = spec;
		this.type = Type.Equation;
	}
	
	public String title;
	public enum Type {
		Item,
		Equation
	}
	public Type type;
	public DependencySpecDetail spec;
	public String toString() {
		return this.title;
	}
}