package jp.silverbullet.web;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.silverbullet.StaticInstances;
import jp.silverbullet.dependency2.CommitListener;
import jp.silverbullet.dependency2.RequestRejectedException;
import jp.silverbullet.property2.PropertyType2;
import jp.silverbullet.property2.RuntimeProperty;
import jp.silverbullet.sequncer.Sequencer;
import jp.silverbullet.web.ui.JsProperty;

@Path("/runtime")
public class RuntimeResource {
	@GET
	@Path("/getProperty")
	@Produces(MediaType.APPLICATION_JSON) 
	public JsProperty getProperty(@QueryParam("id") String id, @QueryParam("index") Integer index, @QueryParam("ext") String ext) {
		RuntimeProperty property = StaticInstances.getInstance().getBuilderModel().getRuntimePropertyStore().get(RuntimeProperty.createIdText(id,index));
		return new JsProperty(property, ext);
	}

	@GET
	@Path("/respondMessage")
	@Produces(MediaType.APPLICATION_JSON)
	public String respondMessage(@QueryParam("id") String id, @QueryParam("type") String type) {
		StaticInstances.getInstance().getBuilderModel().respondToMessage(id, type);
		return "OK";
	}
	
	@GET
	@Path("/getProperties")
	@Produces(MediaType.APPLICATION_JSON)
	public List<JsProperty> getProperties() {
		return JsProperty.convert(StaticInstances.getInstance().getBuilderModel().getRuntimePropertyStore().getAllProperties(PropertyType2.NotSpecified));
	}
	
	@GET
	@Path("/setValue")
	@Produces(MediaType.APPLICATION_JSON) 
	public ValueSetResult setCurrentValue(@QueryParam("id") String id, @QueryParam("index") Integer index, @QueryParam("value") String value) {
		ValueSetResult ret = new ValueSetResult();
		Sequencer sequencer = null;
		
		try {
			sequencer = StaticInstances.getInstance().getBuilderModel().getSequencer();
			sequencer.requestChange(id, index, value, new CommitListener() {
				@Override
				public Reply confirm(String message) {
					return Reply.Accept;
				}
			});
			StaticInstances.getInstance().getBuilderModel().getUiLayoutHolder().getCurrentUi().doAutoDynamicPanel();

			ret.result = "Accepted";
		} catch (RequestRejectedException e) {
			ret.message = e.getMessage();
			ret.result = "Rejected";
		} finally {
			ret.debugLog = sequencer.getDebugDepLog();		
		}

		return ret;
	}
	

}
