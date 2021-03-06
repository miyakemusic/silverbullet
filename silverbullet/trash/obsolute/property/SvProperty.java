package obsolute.property;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.silverbullet.property2.ListDetailElement;
import jp.silverbullet.property2.RuntimePropertyListener;
import jp.silverbullet.property2.RuntimePropertyListener.Flag;

import javax.swing.SwingUtilities;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
public class SvProperty implements Cloneable {

	public PropertyDef getProperty() {
		return property;
	}

	@Override
	public SvProperty clone() {
		try {
			SvProperty ret = (SvProperty)super.clone();
			ret.property = this.property.clone();
			Map<String, Boolean> tmpListMask = new HashMap<>();
			for (String key : this.listMask.keySet()) {
				tmpListMask.put(key, this.listMask.get(key));
			}
			ret.listeners = new HashSet<>();
			ret.listMask = tmpListMask;
			return ret;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static final String DOUBLE_NONE_PROPERTY = "DoubleNoneProperty";
	public static final String LABEL_PROPERTY = "LabelProperty";
	public static final String SERVER_STATE_PROPERTY = "ServerStateProperty";
	public static final String ACTION_PROPERTY = "ActionProperty";
	public static final String BOOLEAN_PROPERTY = "BooleanProperty";
	public static final String FREQ_WL_PROPERTY = "FreqWLProperty";
	public static final String DOUBLE_PROPERTY = "DoubleProperty";
	public static final String LONG_PROPERTY = "LongProperty";
	public static final String TEXT_PROPERTY = "TextProperty";
	public static final String LIST_PROPERTY = "ListProperty";
	public static final String OSA_TABLE_PROPERTY = "OsaTableProperty";
	public static final String TABLE_PROPERTY = "TableProperty";
	public static final String CHART_PROPERTY = "ChartProperty";
	public static final String SWEEP_BAND_PROPERTY = "SweepBandProperty";
	public static final String TRACE_LIST_PROPERTY = "TraceListProperty";
	public static final String DOUBLE_FREEWORD_NONE_PROPERTY = "DoubleFreeWordNoneProperty";
	public static final String IMAGE_PROPERTY = "ImageProperty";
	
	private PropertyDef property;
	private String currentValue = "";
	private String prevValue = "";
	private Set<RuntimePropertyListener> listeners = new HashSet<RuntimePropertyListener>();
	private Map<String, Boolean> listMask = new HashMap<String, Boolean>();
	private boolean enabled = true;
	private Boolean visible = true;
	private int index = 0;
	private String privateTitle = null;
	
	@XmlTransient
	private boolean listenerTouching = false;

	public SvProperty(PropertyDef prop) {
		initialize(prop, 0);
	}
	
	public SvProperty(PropertyDef prop, int index) {
		initialize(prop, index);
	}

	private void initialize(PropertyDef property, int index2) {
		this.property = property;
		this.index = index2;
		if (this.isListProperty()) {
			try {
				this.currentValue = property.getArgumentValue("defaultKey");
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			this.setCurrentValue(property.getArgumentValue("defaultValue"));
		}
	}

	
	public void setPrivateTitle(String privateTitle) {
		this.privateTitle = privateTitle;
	}

	public String getCurrentValue() {
		return currentValue;
	}

	public String getId() {
		return this.property.getId();
	}

	public String getType() {
		return this.property.getType();
	}

	public String getTitle() {
		if (this.privateTitle != null) {
			return privateTitle;
		}
		return this.property.getTitle();
	}
	
	public void setTitle(String title) {
		this.property.setTitle(title);
		fireTitleChanged();
	}
	
	private void fireTitleChanged() {
//		listenerTouching = true;
//		for (SvPropertyListener listener : listeners) {
//			listener.onTitleChanged(property.getId(), this.getIndex(), this.property.getTitle());
//		}
//		listenerTouching = false;
	}

	public void setCurrentValue(String value) {		
		if (this.isDoubleProperty()) {
			if (value.isEmpty()) {
				value = "0";
			}
			try {
				if (getDecimal().endsWith("e")) {
					value = String.format("%." + getDecimal().replaceAll("e", "") + "e", Double.parseDouble(value));
				}
				else {
					value = String.format("%." + getDecimal() + "f", Double.parseDouble(value));
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (!this.currentValue.equals(value)) {
			this.prevValue = currentValue;
			this.currentValue = value;
			
			listenerTouching = true;
			for (RuntimePropertyListener listener : listeners) {
				listener.onValueChange(property.getId(), this.getIndex(), value);
			}
			listenerTouching = false;
		}
	}

	private boolean isDoubleProperty() {
		return this.property.getType().equals(DOUBLE_PROPERTY);
	}

	private String getDecimal() {
		String ret = property.getArgumentValue("decimal");
		if (ret.isEmpty()) {
			ret = "0";
		}
		return ret;
	}

	public void addListener(RuntimePropertyListener propertyListener) {
		this.listeners.add(propertyListener);
	}

	public List<ListDetailElement> getListDetail() {
		return this.property.getListDetail();
	}

	public void setEnabled(boolean b) {
		this.enabled = b;
		for (RuntimePropertyListener listener : listeners) {
			listener.onEnableChange(property.getId(), this.getIndex(), b);
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean isListProperty() {
		return this.property.getType().equals(LIST_PROPERTY) || 
				this.property.getType().equals(SWEEP_BAND_PROPERTY)||
				this.getType().equals(SERVER_STATE_PROPERTY)  || 
				this.getType().equals(TRACE_LIST_PROPERTY);
	}

	public void addListMask(String id, boolean b) {
		listMask.put(id, b);

		this.fireListMaskChanged(id, id, b);
	}
	
	public Map<String, Boolean> getListMask() {
		return listMask;
	}

	public void setListMask(Map<String, Boolean> listMask2) {
		boolean changed = false;
		for (String id : listMask2.keySet()) {
			if (!listMask2.get(id).equals(this.listMask.get(id))) {
				changed = true;
				break;
			}
		}
		changed |= !this.listMask.keySet().containsAll(listMask2.keySet());
		
		this.listMask = listMask2;
		if (changed) {
			this.fireListMaskChanged(this.getId(), "", false);
		}
		
	}

	private void fireListMaskChanged(String id, String listId, boolean mask) {
		for (RuntimePropertyListener listener : listeners) {
			listener.onListMaskChange(property.getId(), this.getIndex(), listId, mask);
		}
	}

	public boolean isListElementMasked(String id) {
		Boolean ret = this.listMask.get(id);
		if (ret == null) {
			return false;
		}
		return ret;
	}

	public List<ListDetailElement> getAvailableListDetail() {
		List<ListDetailElement> ret = new ArrayList<ListDetailElement>();
		for (ListDetailElement e : this.property.getListDetail()) {
			if (!isListElementMasked(e.getId())) {
				ret.add(e);
			}
		}
		return ret;
	}

	public boolean isTextProperty() {
		return this.property.getType().equals(TEXT_PROPERTY);
	}

	public void removeListener(final RuntimePropertyListener listener) {
		if (this.listenerTouching) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					listeners.remove(listener);
				}
			});
		}
		else {
			this.listeners.remove(listener);
		}
	}

	public void setVisible(Boolean b) {
//		this.visible = b;
//		for (SvPropertyListener listener : listeners) {
//			listener.onVisibleChanged(property.getId(), this.getIndex(), b);
//		}
	}

	public boolean isNumericProperty() {
		return this.getType().equals(LONG_PROPERTY) || this.getType().equals(DOUBLE_PROPERTY) ||
				this.getType().equals(FREQ_WL_PROPERTY);
	}

	public String getDefaultMin() {
		return this.property.getArgumentValue("min");
	}
	
	public String getDefaultMax() {
		return this.property.getArgumentValue("max");
	}

	public String getMin() {
		return property.getArgumentValue("min");
	}

	public String getMax() {
		return property.getArgumentValue("max");
	}

	public void setMin(String min) {
		property.updateArgument("min", min);
		this.fireFlagChanged(Flag.MIN);
	}

	public void setMax(String max) {
		property.updateArgument("max", max);
		this.fireFlagChanged(Flag.MAX);
	}

	public void fireFlagChanged(Flag flag) {
		for (RuntimePropertyListener listener : this.listeners) {
			listener.onFlagChange(this.getId(), this.getIndex(), flag);
		}
	}

	public boolean isBooleanProperty() {
		return this.property.getType().equals(BOOLEAN_PROPERTY);
	}

	public boolean isActionProperty() {
		return this.property.getType().equals(ACTION_PROPERTY);
	}

	public boolean isTableProperty() {
		return this.property.getType().equals(OSA_TABLE_PROPERTY) || this.property.getType().equals(TABLE_PROPERTY);
	}

	public boolean isLabelProperty() {
		return this.property.getType().equals(LABEL_PROPERTY);
	}

	public String getUnit() {
		return property.getArgumentValue("unit");
	}

	public boolean isNumericNoneProperty() {
		return this.property.getType().equals(DOUBLE_NONE_PROPERTY);
	}

	public boolean isVisible() {
		return this.visible;
	}

	public void undoValue() {
		this.setCurrentValue(this.prevValue);
	}

	public String getComment() {
		return this.property.getComment();
	}

	public String getSelectedListTitle() {
		for (ListDetailElement e: this.getListDetail()) {
			if (e.getId().equals(this.getCurrentValue())) {
				return e.getTitle();
			}
		}
		return "";
	}

	public List<String> getListIds() {
		List<String> ret = new ArrayList<String>();
		for (ListDetailElement e: this.getListDetail()) {
			ret.add(e.getId());
		}
		return ret;
	}

	public boolean isImageProperty() {
		return this.property.getType().equals(IMAGE_PROPERTY);
	}

	public boolean isChartProperty() {
		return this.property.getType().equals(CHART_PROPERTY);
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getPersistentData() {
		return this.getCurrentValue();
	}

	public void setPersistentData(String value) {
		this.setCurrentValue(value);
	}

	public void resetMask() {
		this.listMask.clear();
	}

	public void setSize(Integer size) {
		this.property.setSize(size);
		this.fireFlagChanged(Flag.SIZE);
	}
}
