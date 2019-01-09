package jp.silverbullet.web;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.silverbullet.StaticInstances;
import jp.silverbullet.dependency2.DependencySpec;
import jp.silverbullet.dependency2.DependencySpecAnalyzer;
import jp.silverbullet.dependency2.DependencySpecHolder;
import jp.silverbullet.dependency2.GenericLinks;
import jp.silverbullet.dependency2.LinkGenerator;
import jp.silverbullet.dependency2.LinkGenerator.LinkLevel;
import jp.silverbullet.dependency2.WebDataConverter;
import jp.silverbullet.dependency2.WebDependencySpec;
import jp.silverbullet.dependency2.WebPair;
import jp.silverbullet.property2.PropertyType2;
import jp.silverbullet.property2.RuntimeProperty;
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
			public RuntimeProperty getProperty(String id) {
				return StaticInstances.getInstance().getBuilderModel().getProperty(id);
			}

			@Override
			public RuntimeProperty getProperty(String id, int index) {
				return StaticInstances.getInstance().getBuilderModel().getProperty(RuntimeProperty.createIdText(id, index));
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
		
		spec.update(element, row, col, value);

		return "OK";
	}
	
	@GET
	@Path("/getIds")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<String> getIds() {
		return StaticInstances.getInstance().getBuilderModel().getPropertyHolder().getAllIds();
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
	@Path("/getPriorityList")
	@Produces(MediaType.APPLICATION_JSON)
	public List<WebPair> getPriorityList() {
		List<WebPair> ret = new ArrayList<>();
		for (String id : StaticInstances.getInstance().getBuilderModel().getPropertiesHolder2().getAllIds(PropertyType2.NotSpecified)) {
			ret.add(new WebPair(id, String.valueOf(this.getPriority(id))));
		}
		return ret;
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
	
	@GET
	@Path("/copySpec")
	@Produces(MediaType.TEXT_PLAIN)
	public String copySpec(@QueryParam("id") final String id, @QueryParam("from") final String from, @QueryParam("to") final String to) {
		DependencySpecHolder specHolder = StaticInstances.getInstance().getBuilderModel().getDependencySpecHolder2();
		specHolder.getSpec(id).copySpec(from, to);
		return "OK";
	}
	
//	@GET
//	@Path("/getRestrictions")
//	@Produces(MediaType.APPLICATION_JSON)
//	public DependencyRestriction getRestrictions() {
//		DependencySpecHolder specHolder = StaticInstances.getInstance().getBuilderModel().getDependencySpecHolder2();
//		return specHolder.getDependencyRestriction();
//	}
}
