package jp.silverbullet.web;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

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
import jp.silverbullet.dependency.DepPropertyStore;
import jp.silverbullet.dependency2.DependencySpecHolder;
import jp.silverbullet.dependency2.WebDataConverter;
import jp.silverbullet.dependency2.WebDependencySpec;

@Path("/dependencySpec2")
public class DependencySpecResource2 {

	private DependencySpecHolder loadSpec() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			DependencySpecHolder obj = mapper.readValue(new File("C:\\Users\\a1199022\\git3\\silverbullet\\silverbullet\\sample.json"), DependencySpecHolder.class);
			return obj;
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@GET
	@Path("/getSpec")
	@Produces(MediaType.APPLICATION_JSON) 
	public WebDependencySpec getSpec(@QueryParam("id") final String id) {
		WebDataConverter converter = new WebDataConverter(loadSpec(), new DepPropertyStore() {
			@Override
			public SvProperty getProperty(String id) {
				return StaticInstances.getInstance().getBuilderModel().getProperty(id);
			}

			@Override
			public void add(SvProperty createListProperty) {
				// TODO Auto-generated method stub
				
			}
			
		});
		return converter.getSpec(id);
	}
}
