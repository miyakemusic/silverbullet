package jp.silverbullet.dependency.speceditor3.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.speceditor3.DependencyTargetElement;
import jp.silverbullet.property.ListDetailElement;

public class PropertySpecUi extends VBox {

	public PropertySpecUi(DependencyEditorModel dependencyEditorModel) {
		TableView<PropertySpecData> tableView = new TableView<>();

		TableColumn<PropertySpecData,String> titileCol = new TableColumn<>("Item");
		TableColumn<PropertySpecData,String> valueCol = new TableColumn<>("Value");
		
		titileCol.setCellValueFactory(new PropertyValueFactory<PropertySpecData,String>("title"));
		valueCol.setCellValueFactory(new PropertyValueFactory<PropertySpecData,String>("value"));
	    tableView.getColumns().addAll(titileCol, valueCol);
	    
	    this.getChildren().add(tableView);
	    
	    dependencyEditorModel.addtListener(new DependecyEditorModelListener() {
			@Override
			public void onSpecUpdate() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSelectionChanged(String id) {
				updateUi(tableView, dependencyEditorModel.getProperty(id));
			}

			@Override
			public void onRequestAdd(String id, DependencyTargetElement dependencyTargetElement, String selectionId) {
				// TODO Auto-generated method stub
				
			}
	    });
	}

	protected void updateUi(TableView<PropertySpecData> tableView, SvProperty property) {
		tableView.getItems().clear();
		ObservableList<PropertySpecData> data = FXCollections.observableArrayList();
		data.add(new PropertySpecData("ID", property.getId()));
		data.add(new PropertySpecData("Caption", property.getTitle()));
		data.add(new PropertySpecData("Type", property.getType()));
		data.add(new PropertySpecData("Comment", property.getComment()));
		if (property.isListProperty()) {
			for (ListDetailElement e: property.getListDetail()) {
				String value = e.getTitle();
				if (!e.getComment().isEmpty()) {
					value +=" (" + e.getComment() + ")";
				}
				data.add(new PropertySpecData(e.getId(), value));
			}
		}
		else if (property.isNumericProperty()) {
			data.add(new PropertySpecData("Min.", property.getMin()));
			data.add(new PropertySpecData("Max.", property.getMax()));
		}
		tableView.getItems().addAll(data);
	}

}
