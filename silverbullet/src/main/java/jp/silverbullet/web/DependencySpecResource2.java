package jp.silverbullet.web;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.silverbullet.StaticInstances;
import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.DepChainPair;
import jp.silverbullet.dependency2.DependencySpec;
import jp.silverbullet.dependency2.DependencySpecAnalyzer;
import jp.silverbullet.dependency2.DependencySpecHolder;
import jp.silverbullet.dependency2.Expression;
import jp.silverbullet.dependency2.GenericLink;
import jp.silverbullet.dependency2.GenericLinks;
import jp.silverbullet.dependency2.LinkGenerator;
import jp.silverbullet.dependency2.LinkGenerator.LinkLevel;
import jp.silverbullet.dependency2.WebDataConverter;
import jp.silverbullet.dependency2.WebDependencySpec;
import jp.silverbullet.web.ui.PropertyGetter;

@Path("/dependencySpec2")
public class DependencySpecResource2 {

//	private DependencySpecHolder loadSpec() {
//		ObjectMapper mapper = new ObjectMapper();
//		try {
//			DependencySpecHolder obj = mapper.readValue(new File("C:\\Users\\a1199022\\git3\\silverbullet\\silverbullet\\sample.json"), DependencySpecHolder.class);
//			return obj;
//		} catch (JsonGenerationException e) {
//			e.printStackTrace();
//		} catch (JsonMappingException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//	
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
		
//		if (element.startsWith(id)) {
//			element = DependencySpec.OptionEnable + "#" + element;
//		}
		List<Expression> expressions = spec.getExpression(element);
		Expression exp = null;
		if (expressions.size() <= row) {
			exp = new Expression();
			expressions.add(exp);
		}
		else {
			exp = expressions.get(row);
		}
		if (col.equals(DependencySpec.Value)) {
			exp.setValue(value);
		}
		else if (col.equals(Expression.Trigger)) {
			exp.setTrigger(value);
		}
		else if (col.equals(Expression.Condition)) {
			exp.setCondition(value);
		}
		else {
			
		}
		return "";
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
}
