package jp.silverbullet.web;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.silverbullet.BuilderFx;
import jp.silverbullet.property.ListDetailElement;
import jp.silverbullet.property.PropertyDef;
import jp.silverbullet.property.PropertyType;
import jp.silverbullet.property.editor.PropertyListModel2;

@Path("/id")
public class IdResource {
	private static final String CAPTION = "Caption";
	private static final String TYPE = "Type";
	private static final String ID = "ID";
	private static final String COMMENT = "Comment";
	
	@GET
	@Path("/selection")
	@Produces(MediaType.APPLICATION_JSON) 
	public ListDetailElement[] getSelections(@QueryParam("id") final String id) {
		return BuilderFx.getModel().getBuilderModel().getPropertyHolder().getProperty(id).getListDetail().toArray(new ListDetailElement[0]);
	}
	
	@GET
	@Path("/properties2")
	@Produces(MediaType.APPLICATION_JSON) 
	public JsonTable getProperties (@QueryParam("type") final String type) {
		JsonTable ret =  new JsonTable();
		List<String> keys = new ArrayList<>();
		keys.add(ID);
		keys.add(TYPE);
		keys.add(CAPTION);
		keys.add(COMMENT);
		
		keys.addAll(BuilderFx.getModel().getBuilderModel().getPropertyHolder().getTypes().getArguments(type));
		ret.setHeader(keys);
		for (PropertyDef prop : BuilderFx.getModel().getBuilderModel().getPropertyHolder().getProperties()) {
			if (prop.getType().equals(type)) {
				List<String> args = new ArrayList<>();
		
				for (String key : keys) {
					if (key.equals(ID)) {
						args.add(prop.getId());
					}
					else if (key.equals(TYPE)) {
						args.add(prop.getType());
					}
					else if (key.equals(CAPTION)) {
						args.add(prop.getTitle());
					}
					else if (key.equals(COMMENT)) {
						args.add(prop.getComment());
					}
					else {
						args.add(prop.getArgumentValue(key));
					}
				}
				ret.addRow(args);
			}
		}
		
		return ret;
	}
	
	@GET
	@Path("/addNew")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addNewProperty(@QueryParam("type") final String type) {
		PropertyDef prop = new PropertyDef();
		if (!type.equals("All")) {
			prop.setType(type);
		}
		prop.setId("ID_" + Calendar.getInstance().getTime().getTime());
		BuilderFx.getModel().getBuilderModel().getPropertyHolder().addProperty(prop);
		
		return "OK";
	}
	
	@GET
	@Path("/addChoice")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addNewChoice(@QueryParam("id") final String id) {
		PropertyDef prop = BuilderFx.getModel().getBuilderModel().getPropertyHolder().getProperty(id);
		ListDetailElement choice = new ListDetailElement();
		choice.setId(id + "_" + Calendar.getInstance().getTime().getTime());
		prop.addListItem(choice);
		return "OK";
	}
	
	@GET
	@Path("/updateChoice")
	@Produces(MediaType.APPLICATION_JSON) 
	public String updateChoice(@QueryParam("id") final String id, @QueryParam("selectionId") final String selectionId, @QueryParam("paramName") final String paramName, @QueryParam("value") final String value) {
		PropertyDef prop = BuilderFx.getModel().getBuilderModel().getPropertyHolder().getProperty(id);
		
		for (ListDetailElement e: prop.getListDetail()) {
			if (e.getId().equals(selectionId)) {
				if (paramName.equals("id")) {
					e.setId(value);
				}
				else if (paramName.equals("comment")) {
					e.setComment(value);
				}
				else if (paramName.equals("title")) {
					e.setTitle(value);
				}
				
				break;
			}
		}
		
		prop.updateListItems();
		return "";
	}
	
	@GET
	@Path("/properties")
	@Produces(MediaType.APPLICATION_JSON) 
	public JsonTable test(@QueryParam("type") final String type) {
		JsonTable ret =  new JsonTable();
		
		PropertyListModel2 model = new PropertyListModel2(BuilderFx.getModel().getBuilderModel().getPropertyHolder());
		
		model.setFilterProperty(type);
		
		String[] header = new String[model.getColumnCount()];
		for (int col = 0; col < model.getColumnCount(); col++) {
			header[col] = model.getColumnName(col);
		}
		ret.setHeader(header);
		
		for (int row = 0; row < model.getRowCount(); row++) {
			String[] val = new String[model.getColumnCount()];
			for (int col = 0; col < model.getColumnCount(); col++) {
				val[col] = String.valueOf(model.getValueAt(row, col));
			}		
			ret.addRow(val);
		}
		
		return ret;
	}
	
	@GET
	@Path("/allIds")
	@Produces(MediaType.APPLICATION_JSON) 
	public PropertyDefList getAllIds (@QueryParam("type") final String type) {
		PropertyDefList ret =  new PropertyDefList();
		for (PropertyDef prop : BuilderFx.getModel().getBuilderModel().getPropertyHolder().getProperties()) {
			ret.list.add(prop);
		}
		
		return ret;
	}
	
	@GET
	@Path("/arguments")
	@Produces(MediaType.APPLICATION_JSON) 
	public String[] getArguments(@QueryParam("type") final String type) {
		return BuilderFx.getModel().getBuilderModel().getPropertyHolder().getTypes().getArguments(type).toArray(new String[0]);
	}
	
	@GET
	@Path("/update")
	@Produces(MediaType.TEXT_PLAIN) 
	public String updateValue(@QueryParam("id") final String id, 
			@QueryParam("paramName") final String paramName,  
			@QueryParam("value") final String value) {

		PropertyDef prop = BuilderFx.getModel().getBuilderModel().getPropertyHolder().getProperty(id);
		
		if (paramName.equals("id")) {
			prop.setId(value);
		}
		else if (paramName.equals("type")) {
			prop.setType(value);
		}
		else if (paramName.equals("title")) {
			prop.setTitle(value);
		}
		else if (paramName.equals("comment")) {
			prop.setComment(value);
		}
		else {
			prop.updateArgument(paramName, value);
		}
		System.out.println("updateValue " + id + "," + paramName + "," + value);
		return "";
	}
	
	@GET
	@Path("/typeNames")
	@Produces(MediaType.APPLICATION_JSON) 
	public String[] getTypeNames() {
		List<String> ret = new ArrayList<String>(BuilderFx.getModel().getBuilderModel().getPropertyHolder().getTypes().getDefinitions().keySet());
		ret.add(0, "All");
		return ret.toArray(new String[0]);
	}
	
	@GET
	@Path("/types")
	@Produces(MediaType.APPLICATION_JSON) 
	public PropertyType getTypes() {
		return BuilderFx.getModel().getBuilderModel().getPropertyHolder().getTypes();
	}
}
