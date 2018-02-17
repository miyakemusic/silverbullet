package jp.silverbullet.property.editor;

import javax.swing.table.AbstractTableModel;

import jp.silverbullet.property.PropertyDef;

public class ListDetailModel2 extends AbstractTableModel {
	private static final String COMMENT = "comment";
	private static final String ID = "id";
	private static final String TITLE = "title";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PropertyDef property = new PropertyDef();//new PropertyNull(null, null, null);
	private static String[] title = {ID, COMMENT, TITLE};
	
	public void setListDetail(PropertyDef property) {
		this.property = property;
		this.fireTableDataChanged();
	}
	
	@Override
	public String getColumnName(int column) {
		return title[column];
	}

	@Override
	public int getRowCount() {
		return property.getListDetail().size();
	}

	@Override
	public int getColumnCount() {
		return title.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (title[columnIndex] == ID) {
			return property.getListDetail().get(rowIndex).getId();
		}
		else if (title[columnIndex] == COMMENT) {
			return property.getListDetail().get(rowIndex).getComment();
		}
		else if (title[columnIndex] == TITLE) {
			return property.getListDetail().get(rowIndex).getTitle();
		}
		return null;
	}

	@Override
	public void setValueAt(Object arg0, int rowIndex, int columnIndex) {
		String value = arg0.toString();
		if (title[columnIndex] == ID) {
			property.replaceListId(rowIndex, value);//.getListDetail().get(rowIndex).setId(value);
		}
		else if (title[columnIndex] == COMMENT) {
			property.getListDetail().get(rowIndex).setComment(value);
		}
		else if (title[columnIndex] == TITLE) {
			property.getListDetail().get(rowIndex).setTitle(value);
		}
		this.fireTableDataChanged();
	}

}
