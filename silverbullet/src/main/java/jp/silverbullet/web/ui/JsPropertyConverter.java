package jp.silverbullet.web.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.silverbullet.property2.ChartContent;
import jp.silverbullet.property2.ListDetailElement;
import jp.silverbullet.property2.RuntimeProperty;

public class JsPropertyConverter {

	public static JsProperty convert(RuntimeProperty property, String ext) {
		JsProperty ret = new JsProperty();
		ret.setId(property.getId());
		ret.setTitle(property.getTitle());
		ret.setUnit(property.getUnit());
		ret.setElements(property.getDefOptions());
		for (String eid : property.getListMask().keySet()) {
			if (property.getListMask().get(eid)) {
				ret.addDisabledOption(eid);	
			}
		}

		ret.setEnabled(property.isEnabled());
		ret.setType(property.getType());
		
		if (property.isChartProperty()) {
			if (ext == null || ext.isEmpty()) {
				ret.setCurrentValue("REQUEST_AGAIN");
			}
			else {
				try {
					if (property.getCurrentValue().isEmpty()) {
						return ret;
					}
					ChartContent chartContent = new ObjectMapper().readValue(property.getCurrentValue(), ChartContent.class);
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
	
	public static List<JsProperty> convert(List<RuntimeProperty> allProperties) {
		List<JsProperty> ret = new ArrayList<>();
		allProperties.forEach(prop -> ret.add(convert(prop, "")));
		return ret;
	}
}
