package jp.silverbullet.web;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.silverbullet.StaticInstances;
import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency2.DependencySpec;
import jp.silverbullet.dependency2.DependencySpecAnalyzer;
import jp.silverbullet.dependency2.DependencySpecHolder;
import jp.silverbullet.dependency2.Expression;
import jp.silverbullet.dependency2.GenericLinks;
import jp.silverbullet.dependency2.LinkGenerator;
import jp.silverbullet.dependency2.LinkGenerator.LinkLevel;
import jp.silverbullet.dependency2.WebDataConverter;
import jp.silverbullet.dependency2.WebDependencySpec;
import jp.silverbullet.web.ui.PropertyGetter;

@Path("/dependencySpec2")
public class DependencySpecResource2 {

	@GET
	@Path("/getSpec")
	@Produces(MediaType.APPLICATION_JSON) 
	public WebDependencySpec getSpec(@QueryParam("id") final String id) {
		DependencySpecHolder holder = StaticInstances.getInstance().getBuilderModel().getDependencySpecHolder2();
		WebDataConverter converter = new WebDataConverter(holder, new PropertyGetter() {
			@Override
			public SvProperty getProperty(String id) {
				return StaticInstances.getInstance().getBuilderModel().getProperty(id);
			}			
		});
		return converter.getSpec(id);
	}
	
	@GET
	@Path("/updateSpec")
	@Produces(MediaType.TEXT_PLAIN) 
	public String updateSpec(@QueryParam("id") final String id, @QueryParam("element") String element, 
			@QueryParam("row") final Integer row, @QueryParam("col") final String col, @QueryParam("value") final String value) {
	
		DependencySpecHolder holder = StaticInstances.getInstance().getBuilderModel().getDependencySpecHolder2();
		DependencySpec spec = holder.getSpec(id);
		
//		List<Expression> expressions = spec.getExpression(element);
		
		spec.update(element, row, col, value);
//		Expression exp = null;
//		if (expressions.size() <= row) {
//			
//			exp = new Expression();
//			expressions.add(exp);
//		}
//		else {
//			exp = expressions.get(row);
//		}
//		if (col.equals(DependencySpec.Value)) {
//			exp.setValue(value);
//		}
//		else if (col.equals(Expression.Trigger)) {
//			exp.setTrigger(value);
//		}
//		else if (col.equals(Expression.Condition)) {
//			exp.setCondition(value);
//		}
//		else {
//			
//		}
		return "OK";
	}
	
	@GET
	@Path("/getIds")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<String> getIds() {
		return StaticInstances.getInstance().getBuilderModel().getPropertyStore().getAllIds();
	}
	
	@GET
	@Path("/getLinks")
	@Produces(MediaType.APPLICATION_JSON)
	public GenericLinks getLinks(@QueryParam("id") final String id) {
		LinkGenerator linkGenerator = new DependencySpecAnalyzer(StaticInstances.getInstance().getBuilderModel().getDependencySpecHolder2()).getLinkGenerator();//.generateLinks(LinkLevel.Detail);//.filter(id).getLinks();
		if ((id != null) && !id.isEmpty()) {
			return linkGenerator.generateLinks(LinkLevel.Detail, id);
		}
		else {
			return linkGenerator.generateLinks(LinkLevel.Detail);
		}
	}
	
	@GET
	@Path("/getPriority")
	@Produces(MediaType.APPLICATION_JSON)
	public Integer getPriority(@QueryParam("id") final String id) {
		return StaticInstances.getInstance().getBuilderModel().getDependencySpecHolder2().getPriority(id);
	}
	
	@GET
	@Path("/setPriority")
	@Produces(MediaType.TEXT_PLAIN)
	public String getPriority(@QueryParam("id") final String id, @QueryParam("priority") final Integer priority) {
		StaticInstances.getInstance().getBuilderModel().getDependencySpecHolder2().setPriority(id, priority);
		return "OK";
	}
	
	
	@GET
	@Path("/setAlternative")
	@Produces(MediaType.TEXT_PLAIN)
	public String setAlternative(@QueryParam("enabled") final Boolean enabled) {
		String type = "";
		if (enabled) {
			type = "Alternative";
		}
		else {
			type = "Normal";
		}
		StaticInstances.getInstance().getBuilderModel().switchDependency(type);
		return "OK";
	}
}
