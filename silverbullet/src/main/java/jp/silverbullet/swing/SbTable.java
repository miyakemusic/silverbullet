package jp.silverbullet.swing;

import java.awt.Container;
import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.fasterxml.jackson.databind.ObjectMapper;

import jp.silverbullet.core.property2.JsTableContent;
import jp.silverbullet.core.ui.UiProperty;
import jp.silverbullet.core.ui.part2.Pane;

public class SbTable extends SbWidget {

	private AbstractTableModel model;
	private JTable table;
	private JsTableContent tableData = new JsTableContent();

	public SbTable(Pane pane, UiModel uiModel, Container parent) {
		super(pane, uiModel, parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onSize(int width, int height) {
		table.setPreferredSize(new Dimension(width, height));
	}

	@Override
	protected void onPosition(int left, int top) {
		// TODO Auto-generated method stub
		
	}

	@Override
	void onInit(Pane pane, UiProperty uiProp, Container parent) {
		tableData = new JsTableContent();
		model = new SbTableModel();
		table = new JTable(model);
		parent.add(table);
	}

	@Override
	protected void onUpdate(UiProperty uiProp) {
		try {
			this.tableData = new ObjectMapper().readValue(uiProp.getCurrentValue(), JsTableContent.class);
			model.fireTableStructureChanged();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	class SbTableModel extends AbstractTableModel {

		@Override
		public int getColumnCount() {
			return tableData.headers.size();
		}

		@Override
		public int getRowCount() {
			return tableData.getData().size();
		}

		@Override
		public Object getValueAt(int row, int col) {
			return tableData.getDataAt(row).get(col);
		}

		@Override
		public String getColumnName(int column) {
			return tableData.headers.get(column);
		}
		
	}
}
