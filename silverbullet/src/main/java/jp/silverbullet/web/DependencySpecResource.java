package jp.silverbullet.web;

import java.util.Arrays;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.silverbullet.BuilderFx;
import jp.silverbullet.SvProperty;
import jp.silverbullet.SvPropertyStore;
import jp.silverbullet.dependency.speceditor3.DependencySpecHolder2;
import jp.silverbullet.dependency.speceditor3.ui.DependencyEditorModel;
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
}
