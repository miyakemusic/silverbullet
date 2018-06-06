package jp.silverbullet.web;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.silverbullet.BuilderFx;
import jp.silverbullet.SvProperty;
import jp.silverbullet.SvPropertyStore;
import jp.silverbullet.dependency.speceditor3.DependencySpec2;
import jp.silverbullet.dependency.speceditor3.DependencySpecHolder2;
import jp.silverbullet.dependency.speceditor3.DependencySpecTableGenerator;
import jp.silverbullet.dependency.speceditor3.DependencyTargetElement;
import jp.silverbullet.dependency.speceditor3.ui.DependencyEditorModel;
import jp.silverbullet.dependency.speceditor3.ui.DependencyTableRowData;
import jp.silverbullet.property.PropertyHolder;

@Path("/dependencySpec")
public class DependencySpecResource {

	@GET
	@Path("/elements")
	@Produces(MediaType.APPLICATION_JSON) 
	public JsonTable getElements(@QueryParam("id") final String id) {
		SvProperty prop = BuilderFx.getModel().getBuilderModel().getProperty(id);
		DependencySpecHolder2 specHolder = BuilderFx.getModel().getBuilderModel().getDependencySpecHolder2();
		PropertyHolder porpHolder = BuilderFx.getModel().getBuilderModel().getPropertyHolder();
		SvPropertyStore propHolder = BuilderFx.getModel().getBuilderModel().getPropertyStore();
		DependencyEditorModel model = new DependencyEditorModel(prop, specHolder, porpHolder, propHolder);
		
		JsonTable ret = new JsonTable();
		ret.setHeader(Arrays.asList("Element", "Value", "Condition"));
		for (String element :  model.getAllElements(id)) {
			String[] row = new String[3];
			row[0] = element;
			ret.addRow(row);
		}
		return ret;
	}
	
	@GET
	@Path("/specTable")
	@Produces(MediaType.APPLICATION_JSON) 
	public DependencyTableRowData[] getSpecTable(@QueryParam("id") final String id) {
//		JsonTable ret = new JsonTable();
		
		SvProperty property = BuilderFx.getModel().getBuilderModel().getProperty(id);
		DependencySpec2 spec = BuilderFx.getModel().getBuilderModel().getDependencySpecHolder2().get(id);
		
		DependencyEditorModel dependencyEditorModel = new DependencyEditorModel(property, BuilderFx.getModel().getBuilderModel().getDependencySpecHolder2(),
				BuilderFx.getModel().getBuilderModel().getPropertyHolder(), BuilderFx.getModel().getBuilderModel().getPropertyStore());
		List<DependencyTableRowData> table = new DependencySpecTableGenerator(dependencyEditorModel).get(spec);
		
//		ret.setHeader(Arrays.asList("Element","Value", "Condition", "Confirm"));
//		for (DependencyTableRowData row : table) {
//			ret.addRow(Arrays.asList(row.getElement(), row.getValue(), row.getCondition(), row.get));
//		}
		return table.toArray(new DependencyTableRowData[0]);
	}
	
	@GET
	@Path("/addSpec")
	@Produces(MediaType.TEXT_PLAIN)
	public String addSpec(@QueryParam("id") final String id, @QueryParam("element") final String element, 
			@QueryParam("value") final String value, @QueryParam("condition") final String condition) {
		
		DependencySpec2 spec = BuilderFx.getModel().getBuilderModel().getDependencySpecHolder2().get(id);
		spec.add(DependencyTargetElement.valueOf(element), value, condition);
		return "OK";
	}
	
	
	@GET
	@Path("/removeSpec")
	@Produces(MediaType.TEXT_PLAIN)
	public String removeSpec(@QueryParam("id") final String id, @QueryParam("element") final String element, 
			@QueryParam("value") final String value) {
		
		DependencySpec2 spec = BuilderFx.getModel().getBuilderModel().getDependencySpecHolder2().get(id);
		spec.remove(DependencyTargetElement.valueOf(element), value);
		return "OK";
	}
	
	@GET
	@Path("/spec")
	@Produces(MediaType.APPLICATION_JSON) 
	public DependencySpec2 getSpec(@QueryParam("id") final String id) {
		DependencySpec2 spec = BuilderFx.getModel().getBuilderModel().getDependencySpecHolder2().get(id);
		return spec;
	}
}
