package jp.silverbullet.web;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.core.util.Base64;

import jp.silverbullet.StaticInstances;
import jp.silverbullet.register.RegisterShortCut;
import jp.silverbullet.register.SvSimulator;
import jp.silverbullet.register.json.SvRegisterJsonHolder;
import jp.silverbullet.register2.RegisterJsonController;

@Path("/register2")
public class RegisterResource2 {

	@GET
	@Path("/interrupt")
	@Produces(MediaType.TEXT_PLAIN) 
	public String interupt() {
		StaticInstances.getInstance().getBuilderModel().getRuntimRegisterMap().getRegisterController().triggerInterrupt();
		return "OK";
	}
	
	@GET
	@Path("/getRegisters")
	@Produces(MediaType.APPLICATION_JSON) 
	public SvRegisterJsonHolder getRegisters() {
		return new SvRegisterJsonHolder(StaticInstances.getInstance().getBuilderModel().getRegisterSpecHolder());
	}
	
	@POST
	@Path("/postChanges")
	@Consumes(MediaType.APPLICATION_JSON) 
	@Produces(MediaType.TEXT_PLAIN) 
	public String postChanges(KeyValue[] changes) {
		RegisterJsonController controller = new RegisterJsonController(
				StaticInstances.getInstance().getBuilderModel().getRegisterSpecHolder());
		controller.handle(changes);
		
		return "OK";
	}
	
	@GET
	@Path("/addNew")
	public String addNew() {
		StaticInstances.getInstance().getBuilderModel().getRegisterSpecHolder().addRegister();
		return "OK";
	}
	
	@GET
	@Path("/addRow")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addRow(@QueryParam("row") final Integer row) {
		StaticInstances.getInstance().getBuilderModel().getRegisterSpecHolder().insertRegisterAt(row);

		return "OK";
	}
	
	@GET
	@Path("/deleteRow")
	@Produces(MediaType.TEXT_PLAIN) 
	public String deleteRow(@QueryParam("row") final Integer row) {
		StaticInstances.getInstance().getBuilderModel().getRegisterSpecHolder().removeRow(row);

		return "OK";
	}
	
	@GET
	@Path("/addBitRow")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addBitRow(@QueryParam("row") final int row) {
		StaticInstances.getInstance().getBuilderModel().getRegisterSpecHolder().getRegisterByIndex(row).addBit();
		return "OK";
	}

	@GET
	@Path("/setCurrentValue")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setCurrentValue(@QueryParam("regName") final String regName, @QueryParam("bitName") final String bitName, @QueryParam("value") final String value) {
		StaticInstances.getInstance().getBuilderModel().getRuntimRegisterMap().getRegisterController().write(regName, bitName, value);
		//		RegisterInfo regInfo = new RegisterInfo(regName, bitName, value, StaticInstances.getInstance().getBuilderModel().getRegisterProperty());
//		StaticInstances.getInstance().getSimulator().updateRegister(regInfo.getIntAddress(), regInfo.getDataSet(), regInfo.getMask());	
		return "OK";
	}	

	@POST
	@Path("/setBlockData")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String setBlockData(String data, @QueryParam("regName") final String regName) {
		byte[] b = Base64.decode(data.replace("data:application/octet-stream;base64,", ""));
		StaticInstances.getInstance().getBuilderModel().getRuntimRegisterMap().getRegisterController().write(regName, b);
		return "OK";
	}
	
	@GET
	@Path("/triggerShortcut")
	@Produces(MediaType.TEXT_PLAIN) 
	public String triggerShortcut(@QueryParam("regName") final String regName, @QueryParam("bitName") final String bitName) {
		setCurrentValue(regName, bitName, "1");
		if (StaticInstances.getInstance().getBuilderModel().getRegisterShortCut().isInterruptEnabled(regName, bitName)) {
			this.interupt();
		}
		return "OK";
	}

	@GET
	@Path("/createShortCut")
	@Produces(MediaType.TEXT_PLAIN) 
	public String createShortCut(@QueryParam("regName") final String regName, @QueryParam("bitName") final String bitName) {
		StaticInstances.getInstance().getBuilderModel().getRegisterShortCut().add(regName, bitName);
		return "OK";
	}

	@GET
	@Path("/addToTest")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addToTest(@QueryParam("regName") final String regName, @QueryParam("bitName") final String bitName) {
		int value = StaticInstances.getInstance().getBuilderModel().getRegisterMapModel().getValue(regName, bitName);
		StaticInstances.getInstance().getBuilderModel().getTestRecorder().addRegisterQuery(regName, bitName, value);
		return "OK";
	}
	
	@GET
	@Path("/getShortCuts")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<RegisterShortCut> getShortCuts() {
		return StaticInstances.getInstance().getBuilderModel().getRegisterShortCut().getShortcuts();
	}
	
	@GET
	@Path("/getCurrentValue")
	@Produces(MediaType.TEXT_PLAIN) 
	public String getCurrentValue(@QueryParam("regName") final String regName, @QueryParam("bitName") final String bitName) {
		int ret = StaticInstances.getInstance().getBuilderModel().getRuntimRegisterMap().readRegister(regName, bitName);
		return String.valueOf(ret);
	}

	@GET
	@Path("/getSimulators")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getSimulators() {
		return StaticInstances.getInstance().getBuilderModel().getRegisterMapModel().getSimulatorClasses(StaticInstances.getInstance().getBuilderModel().getUserApplicationPath());
	}
	
	@GET
	@Path("getAddedSimulators")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getAddedSimulators() {
		List<String> ret = new ArrayList<>();
		for (SvSimulator simulator : StaticInstances.getInstance().getBuilderModel().getRegisterMapModel().getSimulators()) {
			String sim = simulator.getClass().getSimpleName();
			if (!sim.isEmpty()) {
				ret.add(sim);
			}
		}

		return ret;
	}
	
	@GET
	@Path("loadSimulator")
	@Produces(MediaType.TEXT_PLAIN)
	public String loadSimulator(@QueryParam("simulator") final String simulator) {

		return "OK";
	}
	
	@GET
	@Path("unloadSimulator")
	@Produces(MediaType.TEXT_PLAIN)
	public String unloadSimulator(@QueryParam("simulator") final String simulator) {
		return "OK";
	}
	
	@GET
	@Path("/setCheck")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setCheck(@QueryParam("regName") final String regName, @QueryParam("bitName") final String bitName, @QueryParam("value") final String value) {
		return "OK";
	}

}

