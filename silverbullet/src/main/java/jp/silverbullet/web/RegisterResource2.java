package jp.silverbullet.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.silverbullet.core.KeyValue;
import jp.silverbullet.core.register2.RegisterAccessor;
import jp.silverbullet.core.register2.RegisterJsonController;
import jp.silverbullet.core.register2.RegisterShortCut;
import jp.silverbullet.core.register2.RuntimeRegisterMap.DeviceType;
import jp.silverbullet.dev.BuilderModelImpl.RegisterTypeEnum;
import jp.silverbullet.web.register.json.SvRegisterJsonHolder;

@Path("/{app}/register2")
public class RegisterResource2 {

	@GET
	@Path("/interrupt")
	@Produces(MediaType.TEXT_PLAIN) 
	public String interupt(@PathParam("app") String app) {
		SilverBulletServer.getStaticInstance().getBuilderModel(app).getRuntimRegisterMap().getRegisterController().triggerInterrupt();
		return "OK";
	}
	
	@GET
	@Path("/getRegisters")
	@Produces(MediaType.APPLICATION_JSON) 
	public SvRegisterJsonHolder getRegisters(@PathParam("app") String app) {
		return new SvRegisterJsonHolder(SilverBulletServer.getStaticInstance().getBuilderModel(app).getRegisterSpecHolder());
	}
	
	@POST
	@Path("/postChanges")
	@Consumes(MediaType.APPLICATION_JSON) 
	@Produces(MediaType.TEXT_PLAIN) 
	public String postChanges(@PathParam("app") String app, KeyValue[] changes) {
		RegisterJsonController controller = new RegisterJsonController(
				SilverBulletServer.getStaticInstance().getBuilderModel(app).getRegisterSpecHolder());
		controller.handle(changes);
		
		return "OK";
	}
	
	@GET
	@Path("/addNew")
	public String addNew(@PathParam("app") String app) {
		SilverBulletServer.getStaticInstance().getBuilderModel(app).getRegisterSpecHolder().addRegister();
		return "OK";
	}
	
	@GET
	@Path("/addRow")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addRow(@PathParam("app") String app, @QueryParam("row") final Integer row) {
		SilverBulletServer.getStaticInstance().getBuilderModel(app).getRegisterSpecHolder().insertRegisterAt(row);

		return "OK";
	}
	
	@GET
	@Path("/deleteRow")
	@Produces(MediaType.TEXT_PLAIN) 
	public String deleteRow(@PathParam("app") String app, @QueryParam("row") final Integer row) {
		SilverBulletServer.getStaticInstance().getBuilderModel(app).getRegisterSpecHolder().removeRow(row);

		return "OK";
	}
	
	@GET
	@Path("/addBitRow")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addBitRow(@PathParam("app") String app, @QueryParam("row") final int row) {
		SilverBulletServer.getStaticInstance().getBuilderModel(app).getRegisterSpecHolder().getRegisterByIndex(row).addBit();
		return "OK";
	}

	@GET
	@Path("/setCurrentValue")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setCurrentValue(@PathParam("app") String app, @QueryParam("regName") final String regName, @QueryParam("bitName") final String bitName, @QueryParam("value") final String value) {
		SilverBulletServer.getStaticInstance().getBuilderModel(app).getRuntimRegisterMap().getRegisterController().updateValue(regName, bitName, Integer.valueOf(value));

		return "OK";
	}	

	@POST
	@Path("/setBlockData")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String setBlockData(@PathParam("app") String app, String data, @QueryParam("regName") final String regName) {
		byte[] b = Base64.getDecoder().decode(data.split(",")[1]);//data.replace("data:application/octet-stream;base64,", ""));
		SilverBulletServer.getStaticInstance().getBuilderModel(app).getRuntimRegisterMap().getRegisterController().updateValue(regName, b);
		return "OK";
	}
	
	@GET
	@Path("/triggerShortcut")
	@Produces(MediaType.TEXT_PLAIN) 
	public String triggerShortcut(@PathParam("app") String app, @QueryParam("regName") final String regName, @QueryParam("bitName") final String bitName) {
		setCurrentValue(app, regName, bitName, "1");
		if (SilverBulletServer.getStaticInstance().getBuilderModel(app).getRegisterShortCut().isInterruptEnabled(regName, bitName)) {
			this.interupt(app);
		}
		return "OK";
	}

	@GET
	@Path("/createShortCut")
	@Produces(MediaType.TEXT_PLAIN) 
	public String createShortCut(@PathParam("app") String app, @QueryParam("regName") final String regName, @QueryParam("bitName") final String bitName) {
		SilverBulletServer.getStaticInstance().getBuilderModel(app).getRegisterShortCut().add(regName, bitName);
		return "OK";
	}

	@GET
	@Path("/addToTest")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addToTest(@PathParam("app") String app, @QueryParam("regName") final String regName, @QueryParam("bitName") final String bitName) {
		long value = SilverBulletServer.getStaticInstance().getBuilderModel(app).getRegisterAccessor().readRegister(regName, bitName);
		SilverBulletServer.getStaticInstance().getBuilderModel(app).getTestRecorder().addRegisterQuery(regName, bitName, value);
		return "OK";
	}
	
	@GET
	@Path("/getShortCuts")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<RegisterShortCut> getShortCuts(@PathParam("app") String app) {
		return SilverBulletServer.getStaticInstance().getBuilderModel(app).getRegisterShortCut().getShortcuts();
	}
	
	@GET
	@Path("/getCurrentValue")
	@Produces(MediaType.TEXT_PLAIN) 
	public String getCurrentValue(@PathParam("app") String app, @QueryParam("regName") final String regName, @QueryParam("bitName") final String bitName) {
		long ret = SilverBulletServer.getStaticInstance().getBuilderModel(app).getRegisterAccessor().readRegister(regName, bitName);
		return String.valueOf(ret);
	}

	@GET
	@Path("/getSimulators")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getSimulators(@PathParam("app") String app) {
		List<RegisterAccessor> sims = SilverBulletServer.getStaticInstance().getBuilderModel(app).getSimulators();
		List<String> ret = new ArrayList<>();
		sims.forEach(a -> ret.add(a.getClass().getSimpleName()));
		return ret;
	}
	
	@GET
	@Path("getAddedSimulators")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getAddedSimulators(@PathParam("app") String app) {
		Set<RegisterAccessor> list = SilverBulletServer.getStaticInstance().getBuilderModel(app).getRuntimRegisterMap().getUserDevices();
		List<String> ret = new ArrayList<>();
		list.forEach(a -> ret.add(a.getClass().getSimpleName()));
		return ret;
	}
	
	@GET
	@Path("loadSimulator")
	@Produces(MediaType.TEXT_PLAIN)
	public String loadSimulator(@PathParam("app") String app, @QueryParam("simulator") final String simulator) {
		RegisterAccessor rg = SilverBulletServer.getStaticInstance().getBuilderModel(app).getSimulator(simulator);
		SilverBulletServer.getStaticInstance().getBuilderModel(app).getRuntimRegisterMap().addDevice(DeviceType.SIMULATOR, rg);
		return "OK";
	}
	
	@GET
	@Path("unloadSimulator")
	@Produces(MediaType.TEXT_PLAIN)
	public String unloadSimulator(@PathParam("app") String app, @QueryParam("simulator") final String simulator) {
		RegisterAccessor rg = SilverBulletServer.getStaticInstance().getBuilderModel(app).getSimulator(simulator);
		SilverBulletServer.getStaticInstance().getBuilderModel(app).getRuntimRegisterMap().removeDevice(rg);
		return "OK";
	}
	
	@GET
	@Path("/setCheck")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setCheck(@PathParam("app") String app, @QueryParam("regName") final String regName, @QueryParam("bitName") final String bitName, @QueryParam("value") final String value) {
		return "OK";
	}

	@GET
	@Path("/setRegisterType")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setRegisterType(@PathParam("app") String app, @QueryParam("type") final String type) {
		SilverBulletServer.getStaticInstance().getBuilderModel(app).setRegisterType(RegisterTypeEnum.valueOf(type));
		return "OK";
	}

	@GET
	@Path("/getRegSize")
	@Produces(MediaType.APPLICATION_JSON) 
	public int getRegSize(@PathParam("app") String app) {
		return SilverBulletServer.getStaticInstance().getBuilderModel(app).getRegisterSpecHolder().getRegSize();
	}

	@GET
	@Path("/getRegSizeList")
	@Produces(MediaType.APPLICATION_JSON) 
	public List<String> getRegSizeList(@PathParam("app") String app) {
		return Arrays.asList("8", "16", "32", "64", "128");
	}
	
	@GET
	@Path("/setRegSize")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setRegSize(@PathParam("app") String app, @QueryParam("value") final int value) {
		SilverBulletServer.getStaticInstance().getBuilderModel(app).getRegisterSpecHolder().setRegSize(value);
		return "OK";
	}
}

