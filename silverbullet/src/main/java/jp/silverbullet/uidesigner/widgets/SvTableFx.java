package jp.silverbullet.uidesigner.widgets;

import java.io.StringReader;
import java.util.List;

import javax.xml.bind.JAXB;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.engine.DependencyInterface;
import jp.silverbullet.uidesigner.RowData;
import jp.silverbullet.uidesigner.TableContent;

public class SvTableFx extends SvPropertyWidgetFx {

	private TableView<RowData> tableView;
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
		this.tableView.getColumns().clear();
		this.tableView.getItems().clear();
		if (getProperty().getCurrentValue().isEmpty()) {
			return;
		}
		try {
			StringReader reader = new StringReader(getProperty().getCurrentValue());
			TableContent content = JAXB.unmarshal(reader, TableContent.class);
			double width = 0;
			for (int i = 0; i < content.getHeaders().size(); i++) {
				final int index = i;
				String header = content.getHeaders().get(i);
				TableColumn<RowData, String> tableColumn = new TableColumn<>(header);
				width += getColWidth(i);
				tableColumn.setMinWidth(getColWidth(i));
				tableColumn.setCellValueFactory(new Callback<CellDataFeatures<RowData, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(
							CellDataFeatures<RowData, String> arg0) {
						return new SimpleStringProperty(arg0.getValue().getElement().get(index));
					}
				});
				tableView.getColumns().add(tableColumn);
			}
			tableView.getItems().addAll(content.getData());
//			tableView.setMinWidth(width);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
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
