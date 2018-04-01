package jp.silverbullet.dependency.speceditor3.ui;

import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import jp.silverbullet.MyDialogFx;
import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.speceditor3.DependencyExpression;
import jp.silverbullet.dependency.speceditor3.DependencyExpressionHolder;
import jp.silverbullet.dependency.speceditor3.DependencyExpressionHolderMap;
import jp.silverbullet.dependency.speceditor3.DependencyExpressionList;
import jp.silverbullet.dependency.speceditor3.DependencySpec2;
import jp.silverbullet.dependency.speceditor3.DependencySpecHolder2;
import jp.silverbullet.dependency.speceditor3.DependencyTargetElement;
import jp.silverbullet.property.ListDetailElement;
import jp.silverbullet.property.PropertyHolder;

public class DependencySpecEditorUi extends VBox {
	private ObservableList<DependencyTableRowData> data = FXCollections.observableArrayList();
	protected String targetElement = "";
	private DependecyEditorModel dependencyEditorModel;
	private PropertyHolder propertyHolder;

	public DependencySpecEditorUi(DependecyEditorModel dependencyEditorModel) {
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
				
		targetElementCol.setCellValueFactory(new PropertyValueFactory<DependencyTableRowData,String>("element"));
		valueCol.setCellValueFactory(new PropertyValueFactory<DependencyTableRowData,String>("value"));
		conditionCol.setCellValueFactory(new PropertyValueFactory<DependencyTableRowData,String>("condition"));
	    tableView.getColumns().addAll(targetElementCol, valueCol, conditionCol);
	    
	    this.getChildren().add(tableView);
	    
		ContextMenu contextMenu = new ContextMenu();	
		
		EventHandler handler = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				String text = ((MenuItem)arg0.getSource()).getText();
								
				if (arg0.getSource() instanceof Menu) {
					targetElement = text;
				}
				else if (arg0.getSource() instanceof MenuItem){
					
					if (targetElement.isEmpty()) {
						DependencyTargetElement e = DependencyTargetElement.valueOf(text);
						showEditor(propertyHolder, dependencyEditorModel, e, "");
					}
					else {
						DependencyTargetElement e = DependencyTargetElement.valueOf(targetElement);
						showEditor(propertyHolder, dependencyEditorModel, e, text);
						targetElement = "";
					}
				}
			}
		};
		MenuItem enabledMenu = new MenuItem(DependencyTargetElement.Enabled.name());
		//enabledMenu.setUserData(DependencyTargetElement.Enabled);
		
		MenuItem visibleMenu = new MenuItem(DependencyTargetElement.Visible.name());
		//visibleMenu.setUserData(DependencyTargetElement.Visible);
		MenuItem valueMenu = new MenuItem(DependencyTargetElement.Value.name());	
		contextMenu.getItems().addAll(enabledMenu, visibleMenu, valueMenu);
		
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

		DependencySpecHolder2 holder = dependencyEditorModel.getDependencySpecHolder();
		DependencySpec2 spec = holder.get(property.getId());
		updateList(spec);
		
		tableView.setItems(data);
		
		MenuItem remove = new MenuItem("Remove");
		remove.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				removeSpec(tableView, spec);
			}
		});
		contextMenu.getItems().add(remove);
	}

	protected void removeSpec(TableView<DependencyTableRowData> tableView, DependencySpec2 spec) {
		spec.remove(tableView.getSelectionModel().getSelectedItem().getPointer());
		updateList(spec);

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

	private void updateList(DependencySpec2 spec) {
		this.data.clear();
		for (DependencyTargetElement e: spec.getDepExpHolderMap().keySet()) {
			DependencyExpressionHolderMap map = spec.getDepExpHolderMap().get(e);
			for (String key : map.keySet()) {
				for (DependencyExpressionHolder h : map.get(key)) {
					for (String k : h.getExpressions().keySet()) {
						DependencyExpressionList list = h.getExpressions().get(k);
						for (DependencyExpression exp : list.getDependencyExpressions()) {
							if (key.equals(DependencySpec2.DefaultItem)) {
								data.add(new DependencyTableRowData(e.name(), k, exp.getExpression().getExpression(), exp));
							}
							else {
								data.add(new DependencyTableRowData(key + "." + e.name().replace("ListItem", "").toLowerCase(), k, exp.getExpression().getExpression(), exp));
							}
						}
					}
				}
			}
			
		}
	}

	protected void showEditor(PropertyHolder propertyHolder, DependecyEditorModel dependencyEditorModel, DependencyTargetElement e, String selectionId) {
		MyDialogFx dialog = new MyDialogFx("Dependency", this);

		ExpressionEditorUi node = new ExpressionEditorUi(propertyHolder);
		dialog.showModal(node);
		
		String value = node.getValue();
		String condition = node.getCondition();
		
		if (value.isEmpty() && condition.isEmpty()) {
			return;
		}
		DependencySpecHolder2 holder = dependencyEditorModel.getDependencySpecHolder();
		DependencySpec2 spec = holder.get(dependencyEditorModel.getSelectedProperty().getId());
		holder.add(spec);
		DependencyExpressionHolder detail = new DependencyExpressionHolder(e);
		detail.addExpression().resultExpression(value).conditionExpression(condition);
		
		if (selectionId.isEmpty()) {
			spec.add(detail);
		}
		else {
			spec.add(selectionId, detail);
		}
		
		updateList(spec);
		
		dependencyEditorModel.fireModelUpdated();
	}
	
	protected void showPopup(ContextMenu contextMenu,
			MouseEvent event) {
		contextMenu.show(this, event.getScreenX(), event.getScreenY());
	}


	public void update() {
		buildUi();
	}

}
