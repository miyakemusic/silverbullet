package jp.silverbullet.property.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.table.AbstractTableModel;

import jp.silverbullet.property.PropertyDef;
import jp.silverbullet.property.PropertyHolder;
import jp.silverbullet.property.PropertyHolderListener;

public class PropertyListModel2 extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String TITLE = "title";
	private static final String COMMENT = "comment";
	private static final String TYPE = "type";
	private static final String ID = "id";
	private static final String NO = "No.";
	private PropertyHolder holder;
	private String filterProperty = "";
	private List<String> titles = new ArrayList<>();
	private List<PropertyDef> properties = new ArrayList<>();
	private String filterKeyWord = "";
	private static final List<String> commonTitles = Arrays.asList(NO, ID, TYPE, COMMENT, TITLE);
	private PropertyHolderListener listener = new PropertyHolderListener() {

		@Override
		public void onAdded(PropertyDef newProperty) {
			fireTableDataChanged();
		}

		@Override
		public void onRemoved(PropertyDef property) {
			fireTableDataChanged();
		}

		@Override
		public void onPropertyUpdated(PropertyDef propertyDef) {
			fireTableDataChanged();
		}

		@Override
		public void onIdChanged(String oldId, String newId) {
			// TODO Auto-generated method stub
			
		}
		
	};
	public PropertyListModel2(PropertyHolder holder) {
		this.holder = holder;
		holder.addPropertyHolderListener(listener);
		updatePropertyList();
		setFilterProperty("");
	}
	
	public void removeListener() {
		this.holder.removePropertyHolderListener(listener);
	}
	
	public void setFilterProperty(String filterProperty) {
		if (filterProperty.equals(this.getAllText())) {
			this.filterProperty = "";
		}
		else {
			this.filterProperty = filterProperty;
		}
		this.titles.clear();
		this.titles.addAll(commonTitles);
		this.titles.addAll(this.holder.getTypes().getArguments(filterProperty));
		this.updatePropertyList();
		this.fireTableStructureChanged();
	}
	
	@Override
	public String getColumnName(int col) {
		return titles.get(col);
	}


	@Override
	public void setValueAt(Object arg0, int row, int col) {
		String value = arg0.toString();
		PropertyDef prop = this.properties.get(row);
		String key = this.titles.get(col);
		if (key.equals(ID)) {
			prop.setId(value);
		}
		else if (key.equals(TYPE)) {
			//prop.getType();
			prop.setType(value);
			prop.initArgumentValues();
		}
		else if (key.equals(COMMENT)) {
			prop.setComment(value);
		}
		else if (key.equals(TITLE)) {
			prop.setTitle(value);
		}
		else {
			int index = holder.getTypes().getIndex(prop.getType(), key);
			prop.updateArgument(index, value);
		}
	}


	@Override
	public int getColumnCount() {
		return this.titles.size();
	}

	@Override
	public int getRowCount() {
		return this.properties.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		PropertyDef prop = this.properties.get(row);
		String key = this.titles.get(col);
		if (key.equals(ID)) {
			return prop.getId();
		}
		else if (key.equals(TYPE)) {
			return prop.getType();
		}
		else if (key.equals(COMMENT)) {
			return prop.getComment();
		}
		else if (key.equals(TITLE)) {
			return prop.getTitle();
		}
		else if (key.equals(NO)) {
			return String.valueOf(row + 1);
		}
		else {
			int index = holder.getTypes().getIndex(prop.getType(), key);
			//index -= commonTitles.size();
			try {
				return prop.getOthers().get(index);
			}
			catch (Exception e) {
			//	e.printStackTrace();
				return "";
			}
		}
	}

	private void updatePropertyList() {
		boolean all = filterProperty.isEmpty();
		this.properties.clear();
		for (PropertyDef prop : this.holder.getProperties()) {
			if (prop.getType().equals(filterProperty) || all) {
				this.properties.add(prop);
			}
		}
		
		List<PropertyDef> ret2 = new ArrayList<>();
		if (!filterKeyWord.isEmpty()) {
		    Pattern pattern = Pattern.compile(filterKeyWord, Pattern.CASE_INSENSITIVE);
			if (!this.filterKeyWord.isEmpty()) {
				for (PropertyDef p : properties) {
					Matcher m = pattern.matcher(p.toString());
					if (m.find()){
						ret2.add(p);
					}
				}
			}
			this.properties = ret2;
		}
		this.fireTableDataChanged();
	}

	public PropertyDef getPropertyRowAt(int row) {
		return this.properties.get(row);
	}

	public int getIdColumn() {
		return commonTitles.indexOf(ID);
	}


	public void addNew(int selectedRow) {
		PropertyDef newProperty = new PropertyDef();
		newProperty.initId("ID_NEW_PROPERRTY");
		if (selectedRow == 0) {
			this.holder.addProperty(newProperty);
		}
		else {
			PropertyDef property = this.properties.get(selectedRow);
			this.holder.addPropertyAfter(newProperty, property);
		}
		this.updatePropertyList();
	}
	
	public void duplicate(int selectedRow) {
		PropertyDef property = this.properties.get(selectedRow);
		PropertyDef newProperty = property.clone();
		newProperty.initId("CopyOf" + property.getId());
		this.holder.addPropertyAfter(newProperty, property);
		this.updatePropertyList();
	}

	public void remove(int rowIndex) {
		PropertyDef property = this.properties.get(rowIndex);
		this.holder.remove(property);
		this.updatePropertyList();
	}

	public void addListItem(int selectedRow, int subSelectedRow) {
		PropertyDef property = this.properties.get(selectedRow);
		property.addListItem(subSelectedRow);
		this.updatePropertyList();
	}

	public void removeListItem(int selectedRow, int subSelectedRow) {
		PropertyDef property = this.properties.get(selectedRow);
		property.removeListItem(subSelectedRow);
		this.updatePropertyList();
	}

	public List<String> getUsedPropertyType() {
		return this.holder.getAllTypes();
	}

	public void setFilterKeyword(String text) {
		this.filterKeyWord  = text;
		updatePropertyList();
	}

	public String getAllText() {
		return "All";
	}

	public void replaceText(String current, String newtext) {
		this.holder.replaceText(current, newtext);
	}

}
