package jp.silverbullet.property2;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import jp.silverbullet.web.JsonTable;

public class WebTableConverter {

	private PropertyHolder2 holder;

	public WebTableConverter(PropertyHolder2 holder) {
		this.holder = holder;
	}
	
	public JsonTable createIdTable(PropertyType2 type) {		
        Class<PropertyDef2> clazz = PropertyDef2.class;
        Iterator<PropertyDef2> iterator = holder.getProperties().iterator();
        JsonTable ret = new Converter<PropertyDef2>() {
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
        ret.addOption("Type", getTypeList());
        ret.addOption("Persistent", getBooleanList());
        return ret;
	}

	private List<String> getBooleanList() {
		return Arrays.asList("true", "false");
	}

	private List<String> getTypeList() {
		List<String> ret = new ArrayList<>();
		for (PropertyType2 type : PropertyType2.values()) {
			if (type.equals(PropertyType2.NotSpecified)) {
				continue;
			}
			ret.add(type.toString());
		}
		return ret;
	}

	class Converter<T> {
		public JsonTable create(Iterator<T> iterator, PropertyType2 type, Field[] fields) {
			
			JsonTable table = new JsonTable();
	        List<String> header = new ArrayList<>();
	        List<Integer> widths = new ArrayList<>();
	        List<Field> targetFields = new ArrayList<>();
	        
	        header.add("No.");
	        widths.add(50);
	        
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
	                    		widths.add(col.Width());
	                    		targetFields.add(field);
	                    	}
	                    }
	                }
	                
	            } catch (Exception e) {
	            	e.printStackTrace();
	            }
	        }
	        table.setHeader(header);
	        table.setWidths(widths.toArray(new Integer[0]));
	        
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
			
		}.create(holder.get(id).getOptionValues().iterator(), PropertyType2.List, cls.getDeclaredFields());
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
                	if (col.Presentation().equals(paramName)) {;
	        			String methodName = "set" + StringUtils.capitalize(field.getName());
	        			for (Method method : clazz.getDeclaredMethods()) {
	        				if (method.getName().equals(methodName)) {
	        					Class<?> paramType = method.getParameterTypes()[0];
	        					try {
	        						if (paramType.equals(double.class) || paramType.equals(Double.class)) {
	        							method.invoke(object, Double.valueOf(value));
	        						}
	        						else if (paramType.equals(int.class) || paramType.equals(Integer.class)) {
	        							method.invoke(object, Integer.valueOf(value));
	        						}
	        						else if (paramType.equals(boolean.class) || paramType.equals(Boolean.class)) {
	        							method.invoke(object, Boolean.valueOf(value));
	        						}
	        						else if (paramType.equals(PropertyType2.class)) {
	        							method.invoke(object, PropertyType2.valueOf(value));
	        						}
	        						else {
	        							method.invoke(object, value);
	        						}
								} catch (IllegalAccessException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IllegalArgumentException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (InvocationTargetException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
	        					return;
	        				}
	        			}
//	        			try {
//	        				field.setAccessible(true);
//	        				field.set(object, value);
//	        			} catch (IllegalArgumentException e) {
//	        				e.printStackTrace();
//	        			} catch (IllegalAccessException e) {
//	        				e.printStackTrace();
//						} catch (SecurityException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}     	
                	}
                }
            }
		}
	}

}
