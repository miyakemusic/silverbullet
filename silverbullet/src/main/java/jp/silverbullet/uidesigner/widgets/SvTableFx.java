package jp.silverbullet.uidesigner.widgets;

import java.io.StringReader;
import java.util.List;

import javax.xml.bind.JAXB;

import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.engine.DependencyInterface;
import jp.silverbullet.uidesigner.SvRowData;
import jp.silverbullet.uidesigner.SvTableElement;
import jp.silverbullet.uidesigner.TableContent;

public class SvTableFx extends SvPropertyWidgetFx {

	private TableView<SvRowData> tableView;
	private String[] colWidths;
	public SvTableFx(SvProperty prop, DependencyInterface widgetListener, Description description) {
		super(prop, widgetListener);

		this.colWidths = description.getValue(Description.TABLE_COLUMN_WIDTH).split(",");
		tableView = new TableView<>();
		this.getChildren().add(tableView);
			
        String height = description.getValue(Description.HEIGHT);
        String width = description.getValue(Description.WIDTH);
        if (!height.isEmpty()) {
        	tableView.setPrefHeight(Double.valueOf(height));
        }
        if (!width.isEmpty()) {
        	tableView.setPrefWidth(Double.valueOf(width));
        }
        
		updateTable();
	}

	@Override
	public void onValueChanged(String id, String value) {
		updateTable();
	}

	protected void updateTable() {
//		this.tableView.getColumns().clear();
//		this.tableView.getItems().clear();
		if (getProperty().getCurrentValue().isEmpty()) {
			return;
		}
		try {
			StringReader reader = new StringReader(getProperty().getCurrentValue());
			TableContent content = JAXB.unmarshal(reader, TableContent.class);
			
			if (isTableStructureChanged(tableView.getItems(), content)) {
				double width = 0;
				width = createTable(content, width);
			}
			else {
				tableView.getItems().clear();
			}
			tableView.getItems().addAll(content.getData());
//			tableView.setMinWidth(width);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isTableStructureChanged(ObservableList<SvRowData> items, TableContent content) {
		if (items == null || items.size() == 0) {
			return true;
		}
		if (items.size() != content.getData().size()) {
			return true;
		}
		
		int col = content.getxAxisColumn();

		for (int i = 0; i < items.size(); i++) {
			try {
				if (!items.get(i).getElements().get(col).getValue().equals(content.getData().get(i).getElements().get(col).getValue())) {
					return true;
				}
			}
			catch (Exception e) {
				return true;
			}
		}
		return false;
	}

	private double createTable(TableContent content, double width) {
		for (int i = 0; i < content.getHeaders().size(); i++) {
			final int index = i;
			String header = content.getHeaders().get(i);
			TableColumn<SvRowData, SvTableElement> tableColumn = new TableColumn<>(header);
			width += getColWidth(i);
			tableColumn.setMinWidth(getColWidth(i));
			tableColumn.setCellValueFactory(new Callback<CellDataFeatures<SvRowData, SvTableElement>, ObservableValue<SvTableElement>>() {
				@Override
				public ObservableValue<SvTableElement> call(
						CellDataFeatures<SvRowData, SvTableElement> arg0) {
					//return new SimpleStringProperty(arg0.getValue().getElement().get(index));
					ObservableValue<SvTableElement> ret = new SimpleObjectProperty<SvTableElement>() {

						@Override
						public SvTableElement get() {
							return arg0.getValue().getElements().get(index);
						}
						
					};
				
					return ret;
				}
			});
			tableColumn.setCellFactory(new Callback<TableColumn<SvRowData, SvTableElement>, TableCell<SvRowData, SvTableElement>>() {
				@Override
				public TableCell<SvRowData, SvTableElement> call(TableColumn<SvRowData, SvTableElement> arg0) {
			
					return new TableCell<SvRowData, SvTableElement> ()  {
						
						@Override
						protected void updateItem(SvTableElement item, boolean empty) {
							super.updateItem(item, empty);
							if (!empty && item != null) {
								setText(item.getValue());
								if (item.getStatus().equals(SvTableElement.Status.Pass)) {
									setStyle("-fx-background-color:lightgreen;");
								}
								else if (item.getStatus().equals(SvTableElement.Status.Fail)) {
									setStyle("-fx-background-color:red;");
								}
								else if (item.getStatus().equals(SvTableElement.Status.Nothing)) {
									setStyle("-fx-background-color:white;");
								}
							}
							
						}
						
					};
				}
			});

			tableView.getColumns().add(tableColumn);
		}
		return width;
	}

	private double getColWidth(int i) {
		try {
			return Double.valueOf(this.colWidths[i]);
		}
		catch (Exception e) {
			return 100;
		}
	}

	@Override
	public void onEnableChanged(String id, boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFlagChanged(String id, Flag flag) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onListMaskChanged(String id, String string) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTitleChanged(String id, String title) {
		// TODO Auto-generated method stub
		
	}

}
