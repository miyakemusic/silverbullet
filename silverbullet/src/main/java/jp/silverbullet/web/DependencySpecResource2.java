package jp.silverbullet.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.silverbullet.SilverBulletServer;
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
	public Response getSpec(@QueryParam("id") final String id) {
		DependencySpecHolder holder = SilverBulletServer.getStaticInstance().getBuilderModel().getDependencySpecHolder2();
		WebDataConverter converter = new WebDataConverter(holder, new PropertyGetter() {
			@Override
			public RuntimeProperty getProperty(String id) {
				return SilverBulletServer.getStaticInstance().getBuilderModel().getRuntimePropertyStore().get(id);
			}

			@Override
			public RuntimeProperty getProperty(String id, int index) {
				return SilverBulletServer.getStaticInstance().getBuilderModel().getRuntimePropertyStore().get(RuntimeProperty.createIdText(id, index));
			}			
		});
				
		return Response.ok().entity(converter.getSpec(id)).build();
	}
	
	@GET
	@Path("/updateSpec")
	@Produces(MediaType.TEXT_PLAIN) 
	public String updateSpec(@QueryParam("id") final String id, @QueryParam("element") String element, 
			@QueryParam("row") final Integer row, @QueryParam("col") final String col, @QueryParam("value") final String value) {
	
		DependencySpecHolder holder = SilverBulletServer.getStaticInstance().getBuilderModel().getDependencySpecHolder2();
		DependencySpec spec = holder.getSpec(id);
		
		spec.update(element, row, col, value);

		return "OK";
	}
	
	@GET
	@Path("/getIds")
	@Produces(MediaType.APPLICATION_JSON) 
	public Set<String> getIds() {
		return SilverBulletServer.getStaticInstance().getBuilderModel().getDependencySpecHolder2().getAllIds();
	}
	
	@GET
	@Path("/getLinks")
	@Produces(MediaType.APPLICATION_JSON)
	public GenericLinks getLinks(@QueryParam("id") final String id) {
		LinkGenerator linkGenerator = new DependencySpecAnalyzer(SilverBulletServer.getStaticInstance().getBuilderModel().getDependencySpecHolder2()).getLinkGenerator();//.generateLinks(LinkLevel.Detail);//.filter(id).getLinks();
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
		return SilverBulletServer.getStaticInstance().getBuilderModel().getDependencySpecHolder2().getPriority(id);
	}

	@GET
	@Path("/getPriorityList")
	@Produces(MediaType.APPLICATION_JSON)
	public List<WebPair> getPriorityList() {
		List<WebPair> ret = new ArrayList<>();
		for (String id : SilverBulletServer.getStaticInstance().getBuilderModel().getPropertiesHolder2().getAllIds(PropertyType2.NotSpecified)) {
			ret.add(new WebPair(id, String.valueOf(this.getPriority(id))));
		}
		return ret;
	}
	
	@GET
	@Path("/setPriority")
	@Produces(MediaType.TEXT_PLAIN)
	public String getPriority(@QueryParam("id") final String id, @QueryParam("priority") final Integer priority) {
		SilverBulletServer.getStaticInstance().getBuilderModel().getDependencySpecHolder2().setPriority(id, priority);
		return "OK";
	}
	
	
//	@GET
//	@Path("/setAlternative")
//	@Produces(MediaType.TEXT_PLAIN)
//	public String setAlternative(@QueryParam("enabled") final Boolean enabled) {
//		String type = "";
//		if (enabled) {
//			type = "Alternative";
//		}
//		else {
//			type = "Normal";
//		}
//		SilverBulletServer.getStaticInstance().getBuilderModel().switchDependency(type);
//		return "OK";
//	}
	
	@GET
	@Path("/copySpec")
	@Produces(MediaType.TEXT_PLAIN)
	public String copySpec(@QueryParam("id") final String id, @QueryParam("from") final String from, @QueryParam("to") final String to) {
		DependencySpecHolder specHolder = SilverBulletServer.getStaticInstance().getBuilderModel().getDependencySpecHolder2();
		specHolder.getSpec(id).copySpec(from, to);
		return "OK";
	}
	
//	@GET
//	@Path("/getRestrictions")
//	@Produces(MediaType.APPLICATION_JSON)
//	public DependencyRestriction getRestrictions() {
//		DependencySpecHolder specHolder = SilverBulletServer.getStaticInstance().getBuilderModel().getDependencySpecHolder2();
//		return specHolder.getDependencyRestriction();
//	}
}
