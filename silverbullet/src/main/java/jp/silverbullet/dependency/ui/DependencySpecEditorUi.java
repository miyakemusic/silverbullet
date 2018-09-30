package jp.silverbullet.dependency.ui;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.DependencyExpression;
import jp.silverbullet.dependency.DependencyExpressionHolder;
import jp.silverbullet.dependency.DependencySpec;
import jp.silverbullet.dependency.DependencySpecHolder;
import jp.silverbullet.dependency.DependencySpecTableGenerator;
import jp.silverbullet.dependency.DependencyTargetElement;
import jp.silverbullet.javafx.MyDialogFx;
import jp.silverbullet.property.ListDetailElement;
import jp.silverbullet.property.PropertyHolder;

public class DependencySpecEditorUi extends VBox {
	private ObservableList<DependencyTableRowData> data = FXCollections.observableArrayList();
	protected String targetElement = "";
	private DependencyEditorModel dependencyEditorModel;
	private PropertyHolder propertyHolder;

	public DependencySpecEditorUi(DependencyEditorModel dependencyEditorModel) {
		this.dependencyEditorModel = dependencyEditorModel;
		this.propertyHolder = dependencyEditorModel.getPropertyHolder();
				
		buildUi();
	}

	private void buildUi() {
		SvProperty property = dependencyEditorModel.getSelectedProperty();
		this.getChildren().clear();
		TableView<DependencyTableRowData> tableView = new TableView<>();

		TableColumn<DependencyTableRowData,String> targetElementCol = new TableColumn<>("Element");
		TableColumn<DependencyTableRowData,String> valueCol = new TableColumn<>("Value");
		TableColumn<DependencyTableRowData,String> conditionCol = new TableColumn<>("Condition");
		TableColumn<DependencyTableRowData,String> confirmationCol = new TableColumn<>("Confirmation");
		
		targetElementCol.setCellValueFactory(new PropertyValueFactory<DependencyTableRowData,String>("element"));
		valueCol.setCellValueFactory(new PropertyValueFactory<DependencyTableRowData,String>("value"));
		conditionCol.setCellValueFactory(new PropertyValueFactory<DependencyTableRowData,String>("condition"));
		confirmationCol.setCellValueFactory(new PropertyValueFactory<DependencyTableRowData,String>("confirmation"));
		
	    tableView.getColumns().addAll(targetElementCol, valueCol, conditionCol, confirmationCol);
	    
	    this.getChildren().add(tableView);
	    
		ContextMenu contextMenu = new ContextMenu();	
		
		EventHandler<ActionEvent> handler = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				String text = ((MenuItem)arg0.getSource()).getText();
								
				if (arg0.getSource() instanceof Menu) {
					targetElement = text;
				}
				else if (arg0.getSource() instanceof MenuItem){
					if (targetElement.isEmpty()) {
						DependencyTargetElement e = DependencyTargetElement.valueOf(text);
						showEditor(e, "");
					}
					else {
						DependencyTargetElement e = DependencyTargetElement.valueOf(targetElement);
						showEditor(e, text);
						targetElement = "";
					}
				}
			}
		};
			
		MenuItem menuItemEnabled = new MenuItem(DependencyTargetElement.Enabled.name());
		contextMenu.getItems().add(menuItemEnabled);
	
		MenuItem menuItemVisible = new MenuItem(DependencyTargetElement.Visible.name());
		contextMenu.getItems().add(menuItemVisible);
		
		MenuItem menuItemValue = new MenuItem(DependencyTargetElement.Value.name());
		contextMenu.getItems().add(menuItemValue);
		
		if (property.isNumericProperty()) {
			MenuItem elementMin = new MenuItem(DependencyTargetElement.Min.name());
			MenuItem elementMax = new MenuItem(DependencyTargetElement.Max.name());
			contextMenu.getItems().addAll(elementMin, elementMax);
		}
		else if (property.isListProperty()) {
			Menu listItemEnabledMenu = new Menu(DependencyTargetElement.ListItemEnabled.name());
			Menu listItemVisibleMenu = new Menu(DependencyTargetElement.ListItemVisible.name());
			contextMenu.getItems().addAll(listItemEnabledMenu, listItemVisibleMenu);
			
			for (ListDetailElement e: property.getListDetail()) {	
				MenuItem enabledItem = new MenuItem(e.getId());
				listItemEnabledMenu.getItems().add(enabledItem);
				
				MenuItem visibleItem = new MenuItem(e.getId());
				listItemVisibleMenu.getItems().add(visibleItem);
			}
		}
				
		registerHandler(contextMenu.getItems(), handler);
		
		tableView.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.isSecondaryButtonDown()) {
					showPopup(contextMenu, event);
				}
				event.consume();
			}
		});

		DependencySpecHolder holder = dependencyEditorModel.getDependencySpecHolder();
		DependencySpec spec = holder.get(property.getId());
		updateList(spec);
		
		tableView.setItems(data);
		
		contextMenu.getItems().add(new SeparatorMenuItem());
		
		MenuItem confirmation = new MenuItem("Confirmation");
		confirmation.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				toggleConfirmation(tableView, spec);
			}
		});
		contextMenu.getItems().add(confirmation);
		
		contextMenu.getItems().add(new SeparatorMenuItem());
		
		MenuItem remove = new MenuItem("Remove");
		remove.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				removeSpec(tableView, spec);
			}
		});
		MenuItem edit = new MenuItem("Edit");
		edit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				editSpec(tableView);
			}
		});
		contextMenu.getItems().addAll(edit, remove);
	}

	protected void toggleConfirmation(TableView<DependencyTableRowData> tableView, DependencySpec spec) {
//		String id = dependencyEditorModel.getSelectedProperty().getId();
		DependencyTableRowData rowData = tableView.getSelectionModel().getSelectedItem();
//		DependencyTargetElement element = dependencyEditorModel.convertElementFromPresentation(rowData.getElement());
//		String selection = dependencyEditorModel.getSelectionId(rowData.getElement());
//		String value = rowData.getValue();
//		String condition = rowData.getCondition();
//		DependencyExpression exp = dependencyEditorModel.getDependencySpecHolder().get(id).get(element, selection, value, condition);
		DependencyExpression exp = rowData.getPointer();
		exp.setConfirmationRequired(!exp.isConfirmationRequired());
		this.update();
	}

	protected void editSpec(TableView<DependencyTableRowData> tableView) {
		DependencyTableRowData rowData = tableView.getSelectionModel().getSelectedItem();
		
		DependencyTargetConverter  converter = this.dependencyEditorModel.getRealTargetElement(rowData.getElement());
		DependencyTargetElement e = converter.getElement();
		String selectionId = converter.getSelectionId();
		String defaultValue = rowData.getValue();
		
		String defaultCondition = rowData.getCondition();
		
		new SpecEditorCreator(e, selectionId, defaultValue, defaultCondition) {
			@Override
			protected void completed() {
				removeSpec(tableView, dependencyEditorModel.getDependencySpecHolder().getSpecs().get(dependencyEditorModel.getSelectedProperty().getId()));
			}
		};
	}

	protected void showEditor(DependencyTargetElement e, String selectionId) {
		String defaultValue = "";
		String defaultCondition = "";
		new SpecEditorCreator(e, selectionId, defaultValue, defaultCondition);
	}
	
	class SpecEditorCreator {
		protected void completed() {}
		public SpecEditorCreator(DependencyTargetElement e, String selectionId, String defaultValue,
			String defaultCondition) {
		
			MyDialogFx dialog = new MyDialogFx("Dependency", DependencySpecEditorUi.this);
			ExpressionEditorUi node = new ExpressionEditorUi(dependencyEditorModel.getSelectedProperty(), e, propertyHolder, defaultValue, defaultCondition);
			dialog.showModal(node);
			if (!dialog.isOkClicked()) {
				return;
			}
			completed();
			String value = node.getValue();
			String condition = node.getCondition();
			
			if (value.isEmpty() && condition.isEmpty()) {
				return;
			}
			DependencySpecHolder holder = dependencyEditorModel.getDependencySpecHolder();
			DependencySpec spec = holder.get(dependencyEditorModel.getSelectedProperty().getId());
			
			DependencyExpressionHolder detail = spec.getDependencyExpressionHolder(e, selectionId);//new DependencyExpressionHolder(e);
			detail.addExpression().resultExpression(value).conditionExpression(condition);

			updateList(spec);
			dependencyEditorModel.fireModelUpdated();
		}
	}

	protected void removeSpec(TableView<DependencyTableRowData> tableView, DependencySpec spec) {
		spec.remove(tableView.getSelectionModel().getSelectedItem().getPointer());
		updateList(spec);

		dependencyEditorModel.getDependencySpecHolder().clean(spec);
	
		this.dependencyEditorModel.fireModelUpdated();
	}

	private void registerHandler(List<MenuItem> items, EventHandler handler) {
		for (MenuItem menuItem : items) {
			menuItem.setOnAction(handler);
			if (menuItem instanceof Menu) {
				registerHandler( ((Menu)menuItem).getItems(), handler );
			}
		}
	}

	private void updateList(DependencySpec spec) {
		this.data.clear();
		this.data.addAll(createTable(spec));
		
//		for (DependencyTargetElement e: spec.getDepExpHolderMap().keySet()) {
//			DependencyExpressionHolderMap map = spec.getDepExpHolderMap().get(e);
//			for (String key : map.keySet()) {
//				for (DependencyExpressionHolder h : map.get(key)) {
//					for (String k : h.getExpressions().keySet()) {
//						DependencyExpressionList list = h.getExpressions().get(k);
//						for (DependencyExpression exp : list.getDependencyExpressions()) {
//							String presentation = this.dependencyEditorModel.convertPresentationElement(key, e);
//							data.add(new DependencyTableRowData(presentation, k, exp.getExpression().getExpression(),  exp.isConfirmationRequired(), exp));
//						}
//					}
//				}
//			}	
//		}
	}
	
	private List<DependencyTableRowData> createTable(DependencySpec spec) {
		return new DependencySpecTableGenerator(dependencyEditorModel).get(spec);
	}
	
	protected void showPopup(ContextMenu contextMenu,
			MouseEvent event) {
		contextMenu.show(this, event.getScreenX(), event.getScreenY());
	}


	public void update() {
		buildUi();
	}

	public void requestAdd(String id, DependencyTargetElement dependencyTargetElement, String selectionId) {
		showEditor(dependencyTargetElement, selectionId);
	}

}