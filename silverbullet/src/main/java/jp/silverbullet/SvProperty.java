package jp.silverbullet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.application.Platform;
import jp.silverbullet.SvPropertyListener.Flag;
import jp.silverbullet.dependency.speceditor2.DependencyFormula;
import jp.silverbullet.property.ListDetailElement;
import jp.silverbullet.property.PropertyDef;

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
			// TODO Auto-generated catch block
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
//	private String min = "";
//	private String max = "";
	private Set<SvPropertyListener> listeners = new HashSet<SvPropertyListener>();
	private Map<String, Boolean> listMask = new HashMap<String, Boolean>();
	private boolean enabled = true;
	private Boolean visible = true;
	private int index = 0;
	
	@XmlTransient
	private boolean listenerTouching = false;
//	private ReadWriteLock lock = new ReentrantReadWriteLock();
	
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

	
	public String getCurrentValue() {
//		lock.readLock();
		return currentValue;
	}

	public String getId() {
		return this.property.getId();
	}

	public String getType() {
		return this.property.getType();
	}

	public String getTitle() {
		return this.property.getTitle();
	}
	
	public void setTitle(String title) {
		this.property.setTitle(title);
		fireTitleChanged();
	}
	
	private void fireTitleChanged() {
		listenerTouching = true;
		for (SvPropertyListener listener : listeners) {
			listener.onTitleChanged(property.getId(), this.property.getTitle());
		}
		listenerTouching = false;
	}

	public void setCurrentValue(String value) {		
//		lock.writeLock();
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
		
		if (!this.currentValue.equals(value) || value.equals(DependencyFormula.ANY)) {
			this.prevValue = currentValue;
			this.currentValue = value;
			
			listenerTouching = true;
			for (SvPropertyListener listener : listeners) {
				listener.onValueChanged(property.getId(), value);
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

	public void addListener(SvPropertyListener propertyListener) {
		this.listeners.add(propertyListener);
	}

	public List<ListDetailElement> getListDetail() {
		return this.property.getListDetail();
	}

	public void setEnabled(boolean b) {
		this.enabled = b;
		for (SvPropertyListener listener : listeners) {
			listener.onEnableChanged(property.getId(), b);
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
//		if (b && this.currentValue.equals(id)) {
//			this.setCurrentValue(this.getAvailableListDetail().get(0).getId());
//		}
		this.fireListMaskChanged(id, id + "," + String.valueOf(b));
	}
	
	
	public Map<String, Boolean> getListMask() {
		return listMask;
	}

	public void setListMask(Map<String, Boolean> listMask2) {
//		lock.writeLock();
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
			this.fireListMaskChanged(this.getId(), "");
		}
		
	}

	private void fireListMaskChanged(String id, String detail) {
		for (SvPropertyListener listener : listeners) {
			listener.onListMaskChanged(property.getId(), detail);
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
//		lock.readLock();
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

	public void removeListener(final SvPropertyListener listener) {
		if (this.listenerTouching) {
			Platform.runLater(new Runnable() {
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
		this.visible = b;
		for (SvPropertyListener listener : listeners) {
			listener.onVisibleChanged(property.getId(), b);
		}
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
		//this.min = min;
		property.updateArgument("min", min);
		this.fireFlagChanged(Flag.MIN);
	}

	public void setMax(String max) {
		property.updateArgument("max", max);
		this.fireFlagChanged(Flag.MAX);
	}

	public void fireFlagChanged(Flag flag) {
		for (SvPropertyListener listener : this.listeners) {
			listener.onFlagChanged(this.getId(), flag);
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

}
