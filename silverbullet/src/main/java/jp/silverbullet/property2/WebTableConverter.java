package jp.silverbullet.property2;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import jp.silverbullet.property.ListDetailElement;
import jp.silverbullet.web.JsonTable;

public class WebTableConverter {

	private PropertyHolder2 holder;

	public WebTableConverter(PropertyHolder2 holder) {
		this.holder = holder;
	}
	
	public JsonTable createIdTable(PropertyType2 type) {		
        Class<PropertyDef2> clazz = PropertyDef2.class;
        Iterator<PropertyDef2> iterator = holder.getProperties().iterator();
        return new Converter<PropertyDef2>() {
			@Override
			protected boolean skipRow(String fieldName, String val) {
				if (fieldName.equals("type")) {
					if (type.equals(PropertyType2.NotSpecified)) {
						return false;
					}
					else if (val.equals(type.toString())) {
						return false;
					}
					else {
						return true;
					}
				}
				return false;
			}
        }.create(iterator, type, clazz.getDeclaredFields());
	}

	class Converter<T> {
		public JsonTable create(Iterator<T> iterator, PropertyType2 type, Field[] fields) {
			
			JsonTable table = new JsonTable();
	        List<String> header = new ArrayList<>();
	        List<Field> targetFields = new ArrayList<>();
	        
	        header.add("No.");
	        for (Field field : fields) {
	            try {
	                Annotation[] annotations = field.getDeclaredAnnotations();
	                for (Annotation annotation : annotations) {
	                    if (annotation.annotationType().equals(TableColumn.class)) {
	                    	TableColumn col = (TableColumn)annotation;
	                    	
	                    	boolean enabled = false;
	                    	
	                    	if (type.equals(PropertyType2.NotSpecified) || col.targetType()[0].equals(PropertyType2.NotSpecified)) {
	                    		enabled = true;
	                    	}
	                    	else if (Arrays.asList(col.targetType()).contains(type)) {
	                    		enabled = true;
	                    	}
	                    	if (enabled) {
	                    		header.add(col.Presentation());
	                    		targetFields.add(field);
	                    	}
	                    }
	                }
	                
	            } catch (Exception e) {
	            	e.printStackTrace();
	            }
	        }
	        table.setHeader(header);
	        int index = 1;
			while(iterator.hasNext()) {
				Object object = iterator.next();
				
				List<String> row = new ArrayList<>();
				row.add(String.valueOf(index++));
				boolean skip = false;
				for (Field field : targetFields) {
					field.setAccessible(true);
					
					String val = "";
					try {
						val = String.valueOf(field.get(object));
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
					if (skipRow(field.getName(), val)) {
						skip = true;
						break;
					}
					row.add(val);
				}
				
				if (!skip) {
					table.addRow(row);
				}
			}
			return table;
		}

		protected boolean skipRow(String fieldName, String val) {
			return false;
		}	
	}

	public JsonTable createOptionTable(String id) {
		Class<ListDetailElement> cls = ListDetailElement.class;
		
		return new Converter<ListDetailElement>() {
			
		}.create(holder.get(id).getOptions().iterator(), PropertyType2.List, cls.getDeclaredFields());
	}

	public void updateOptionField(String id, String selectionId, String paramName, String value) {
		ListDetailElement element = this.holder.get(id).getOption(selectionId);
		Class<ListDetailElement> clazz = ListDetailElement.class;
		
		updateFields(paramName, value, element, clazz);
	}

	public void updateMainField(String id, String paramName, String value) {
		PropertyDef2 prop = this.holder.get(id);
		Class<PropertyDef2> clazz = PropertyDef2.class;
		updateFields(paramName, value, prop, clazz);
	}
	
	private void updateFields(String paramName, String value, Object object,
			Class clazz) {
		for (Field field : clazz.getDeclaredFields()) {
			Annotation[] annotations = field.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().equals(TableColumn.class)) {
                	TableColumn col = (TableColumn)annotation;
                	if (col.Presentation().equals(paramName)) {
	        			field.setAccessible(true);
	        			try {
	        				field.set(object, value);
	        			} catch (IllegalArgumentException e) {
	        				e.printStackTrace();
	        			} catch (IllegalAccessException e) {
	        				e.printStackTrace();
	        			}          	
                	}
                }
            }
		}
	}

}
