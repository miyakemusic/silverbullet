package jp.silverbullet.core.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.silverbullet.core.BlobStore;
import jp.silverbullet.core.property2.ChartContent;
import jp.silverbullet.core.property2.ListDetailElement;
import jp.silverbullet.core.property2.RuntimeProperty;
//import jp.silverbullet.dev.StaticInstances;

public class UiPropertyConverter {

	public static final String REQUEST_AGAIN = "REQUEST_AGAIN";

	public static UiProperty convert(RuntimeProperty property, String ext, BlobStore blobStore) {
		UiProperty ret = new UiProperty();
		ret.setId(property.getId());
		ret.setTitle(property.getTitle());
		ret.setUnit(property.getUnit());
		ret.setElements(property.getDefOptions());
		ret.setMin(property.getMin());
		ret.setMax(property.getMax());
		ret.setDecimals(property.getDecimals());
		for (ListDetailElement eid : property.getDefOptions()) {
			if (property.isOptionDisabled(eid.getId())) {
				ret.addDisabledOption(eid.getId());	
			}
		}

		ret.setEnabled(property.isEnabled());
		ret.setType(property.getType());
		
		if (property.isChartProperty()) {
			if (ext == null || ext.isEmpty()) {
				ret.setCurrentValue(REQUEST_AGAIN);
			}
			else {
				try {
					if (property.getCurrentValue().isEmpty()) {
						return ret;
					}
					
					ChartContent chartContent = null;
					try {
						chartContent = (ChartContent)blobStore.get(property.getId());
					}
					catch(Exception e) {
						e.printStackTrace();
					}
//					ChartContent chartContent = new ObjectMapper().readValue(property.getCurrentValue(), ChartContent.class);
					int point = Integer.valueOf(ext);
					int allSize = chartContent.getY().length;
					double step = (double)allSize / (double)point;
					String[] y = new String[point];
					for (int i = 0; i < point; i++) {
						y[i] = chartContent.getY()[(int)((double)i*step)];
					}
					chartContent.setY(y);
					
					
					ret.setCurrentValue(new ObjectMapper().writeValueAsString(chartContent));
				} catch (JsonParseException e) {
					e.printStackTrace();
				} catch (JsonMappingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (NumberFormatException e) {
					e.printStackTrace();
					System.out.println(property.getId());
				}
			}
		}
		else if (property.isList()) {
			ret.setCurrentValue(property.getSelectedListTitle());
			ret.setCurrentSelectionId(property.getCurrentValue());
		}
		else {
			ret.setCurrentValue(property.getCurrentValue());
		}
		
		return ret;
	}
	
	public static List<UiProperty> convert(List<RuntimeProperty> allProperties) {
		List<UiProperty> ret = new ArrayList<>();
		allProperties.forEach(prop -> ret.add(convert(prop, "", null)));
		return ret;
	}
}
