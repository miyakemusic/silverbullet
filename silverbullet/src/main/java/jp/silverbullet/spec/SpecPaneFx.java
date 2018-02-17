package jp.silverbullet.spec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import jp.silverbullet.MyDialogFx;
import jp.silverbullet.spec.SpecElement.Type;

public class SpecPaneFx extends VBox {	
	private SpecElement clipboard;
	private SpecElement selected;
	private Map<SpecElement, Boolean> expanded = new HashMap<>();
	
	public SpecPaneFx(final SpecElement root) {
//		final SpecElement root = new SpecElement();
		HBox hbox = new HBox();
		this.getChildren().add(hbox);
		Button addButton = new Button("Add Node");
		hbox.getChildren().add(addButton);
		Button addMultipleButton = new Button("Add Multiple Nodes");
		hbox.getChildren().add(addMultipleButton);
		Button addSpecButton = new Button("Add Spec");
		hbox.getChildren().add(addSpecButton);
		Button editButton = new Button("Edit");
		hbox.getChildren().add(editButton);
		Button removeButton = new Button("Remove");
		hbox.getChildren().add(removeButton);
		Button upButton = new Button("Up");
		hbox.getChildren().add(upButton);
		Button downButton = new Button("Down");
		hbox.getChildren().add(downButton);
		Button cutButton = new Button("Cut");
		hbox.getChildren().add(cutButton);
		Button pasteButton = new Button("Paste");
		hbox.getChildren().add(pasteButton);
		Button storyButton = new Button("Story");
		hbox.getChildren().add(storyButton);
		Button taskButton = new Button("Task");
		hbox.getChildren().add(taskButton);		
		Button statementButton = new Button("Statement");
		hbox.getChildren().add(statementButton);	
		
		final TreeView<SpecElement> tree = new TreeView<>();
		this.getChildren().add(tree);
		tree.setPrefHeight(700);
		
		tree.selectionModelProperty().addListener(new ChangeListener<MultipleSelectionModel<TreeItem<SpecElement>>>() {
			@Override
			public void changed(
					ObservableValue<? extends MultipleSelectionModel<TreeItem<SpecElement>>> arg0,
					MultipleSelectionModel<TreeItem<SpecElement>> arg1,
					MultipleSelectionModel<TreeItem<SpecElement>> arg2) {
				selected = arg2.getSelectedItem().getValue();
			}
		});
		final TreeItem<SpecElement> rootItem = new TreeItem<>(root);
		
		addButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				showAdd(tree.getSelectionModel().getSelectedItem().getValue(), SpecElement.Type.Node);
				update(root, tree, rootItem);
			}
		});
		addMultipleButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				showAddMulti(tree.getSelectionModel().getSelectedItem().getValue(), SpecElement.Type.Node);
				update(root, tree, rootItem);
			}
		});
		
		addSpecButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				showAdd(tree.getSelectionModel().getSelectedItem().getValue(), SpecElement.Type.Spec);
				update(root, tree, rootItem);			
			}
		});
		editButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				showEdit(tree.getSelectionModel().getSelectedItem().getValue());
				update(root, tree, rootItem);
			}
		});
		removeButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				SpecElement target = getTarget(tree);
				getTargetsParent(tree).remove(target);
				update(root, tree, rootItem);
			}
		});
		abstract class NodeMover {
			public NodeMover() {
				List<SpecElement> list = getTargetsParent(tree);
				SpecElement target = getTarget(tree);
				int index = list.indexOf(target);
				index = handleIndex(index, list);
				list.remove(target);
				list.add(index, target);
				update(root, tree, rootItem);
			}

			abstract protected int handleIndex(int index, List<SpecElement> list);
		};
		
		upButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				new NodeMover() {
					@Override
					protected int handleIndex(int index, List<SpecElement> list) {
						if (index > 0) {
							index--;
						}
						return index;
					}
				};
			}
		});
		downButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				new NodeMover() {

					@Override
					protected int handleIndex(int index, List<SpecElement> list) {
						if (index < list.size()-1) {
							index++;
						}
						return index;
					}
					
				};
			}
		});
		cutButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				clipboard = getTarget(tree);
				getTargetsParent(tree).remove(clipboard);
				update(root, tree, rootItem);
			}
		});
		pasteButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if (clipboard != null) {
					getTarget(tree).getChildren().add(clipboard);
					update(root, tree, rootItem);
				}
			}
		});
		storyButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				getTarget(tree).setType(Type.Story);
				update(root, tree, rootItem);
			}
		});
		taskButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				getTarget(tree).setType(Type.Task);
				update(root, tree, rootItem);
			}
		});
		statementButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				getTarget(tree).setType(Type.Statement);
				update(root, tree, rootItem);
			}
		});
		update(root, tree, rootItem);
	}

	protected void update(final SpecElement root,
			final TreeView<SpecElement> tree,
			final TreeItem<SpecElement> rootItem) {
		rootItem.getChildren().clear();
		tree.setRoot(rootItem);
		recursive(root, rootItem, tree);
		rootItem.setExpanded(true);
	}

	protected void showEdit(SpecElement storyElement) {
		TextArea textField = new TextArea();
		textField.setText(storyElement.getName());
		MyDialogFx dialog = new MyDialogFx("Story Name", this);
		VBox vbox = new VBox();
		vbox.getChildren().add(textField);
		dialog.showModal(vbox);
		if (dialog.isOkClicked()) {
			storyElement.setName(textField.getText());
		}
	}


	protected void showAddMulti(SpecElement storyElement, Type type) {
		// TODO Auto-generated method stub
		TextArea textField = new TextArea();
		MyDialogFx dialog = new MyDialogFx("Story Names", this);
		VBox vbox = new VBox();
		vbox.getChildren().add(textField);
		dialog.showModal(vbox);
		if (dialog.isOkClicked()) {
			for (String s : textField.getText().split("\n")) {
				SpecElement element = new SpecElement();
				element.setType(type);
				String[] tmp = s.split("[\\s]");
				element.setName(tmp[0]);
				storyElement.getChildren().add(element);
			}
		}	
	}
	
	protected void showAdd(SpecElement storyElement, Type type) {
		TextArea textField = new TextArea();
		MyDialogFx dialog = new MyDialogFx("Story Names", this);
		VBox vbox = new VBox();
		vbox.getChildren().add(textField);
		dialog.showModal(vbox);
		if (dialog.isOkClicked()) {
			SpecElement element = new SpecElement();
			element.setName(textField.getText());
			element.setType(type);
			storyElement.getChildren().add(element);
		}
	}
	
	protected void recursive(SpecElement item, TreeItem<SpecElement> treeItem, TreeView<SpecElement> tree) {
		for (final SpecElement e : item.getChildren()) {
			Node icon = null;
			if (e.getType().equals(Type.Story)) {
				icon = new ImageView(
				        new Image(getClass().getResourceAsStream("book.png")));
			}
			else if (e.getType().equals(Type.Task)) {
				icon = new ImageView(
				        new Image(getClass().getResourceAsStream("task.png")));
			}
			else {
				icon = new ImageView(
				        new Image(getClass().getResourceAsStream("check.png")));			
			}
			TreeItem<SpecElement> subTreeItem = new TreeItem<>(e, icon);
			subTreeItem.expandedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> arg0,
						Boolean arg1, Boolean arg2) {
					expanded.put(e, arg2);
				}
			});
			
			if (expanded.keySet().contains(e)) {
				subTreeItem.setExpanded(expanded.get(e));
			}
			if (selected == e) {
				tree.selectionModelProperty().get().select(subTreeItem);
			}
			//if (e.getType().equals(Type.Node)) {
	//		treeItem.setExpanded(true);
			//}
			treeItem.getChildren().add(subTreeItem);
			
			recursive(e, subTreeItem, tree);
		}
	}

	protected SpecElement getTarget(final TreeView<SpecElement> tree) {
		SpecElement target = tree.getSelectionModel().getSelectedItem().getValue();
		return target;
	}

	protected List<SpecElement> getTargetsParent(
			final TreeView<SpecElement> tree) {
		return tree.getSelectionModel().getSelectedItem().getParent().getValue().getChildren();
	}
}
