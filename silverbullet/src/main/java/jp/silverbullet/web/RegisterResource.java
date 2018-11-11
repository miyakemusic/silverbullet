package jp.silverbullet.web;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.core.util.Base64;

import jp.silverbullet.StaticInstances;
import jp.silverbullet.handlers.RegisterAccess;
import jp.silverbullet.register.BitSetToIntConverter;
import jp.silverbullet.register.RegisterBit;
import jp.silverbullet.register.RegisterInfo;
import jp.silverbullet.register.RegisterBit.ReadWriteType;
import jp.silverbullet.register.RegisterProperty;
import jp.silverbullet.register.RegisterShortCut;
import jp.silverbullet.register.SvRegister;
import jp.silverbullet.register.SvSimulator;
import jp.silverbullet.register.json.SvRegisterJson;
import jp.silverbullet.register.json.SvRegisterJsonHolder;

@Path("/register")
public class RegisterResource {

	@GET
	@Path("/interrupt")
	@Produces(MediaType.TEXT_PLAIN) 
	public String interupt() {
		StaticInstances.getInstance().getBuilderModel().getRegisterMapModel().triggerInterrupt();
		return "OK";
	}
	
	@GET
	@Path("/getRegisters")
	@Produces(MediaType.APPLICATION_JSON) 
	public SvRegisterJsonHolder getRegisters() {
		RegisterProperty regProp =  StaticInstances.getInstance().getBuilderModel().getRegisterProperty();
		
		SvRegisterJsonHolder ret = new SvRegisterJsonHolder();
		for (SvRegister register : regProp.getRegisters()) {
			ret.addRegister(convertRegister(register));
		}
		
		return ret;
	}

	private SvRegisterJson convertRegister(SvRegister register) {
		SvRegisterJson ret = new SvRegisterJson();
		ret.setAddress(register.getAddress());
		ret.setDescription(register.getDescription());
		ret.setName(register.getName());
		ret.addAll(register.getBits().getBits());
		return ret;
	}
	
	@POST
	@Path("/postChanges")
	@Consumes(MediaType.APPLICATION_JSON) 
	@Produces(MediaType.TEXT_PLAIN) 
	public String postChanges(KeyValue[] changes) {
		List<Integer> removes = new ArrayList<>();
		boolean sortRequired = false;
		
		for (KeyValue kv : changes) {
//			System.out.println(kv.getKey() + " = " + kv.getValue());
			String[] tmp = kv.getKey().split("_");
			String param = tmp[0];
			int row = Integer.valueOf(tmp[1]);
			SvRegister register =  StaticInstances.getInstance().getBuilderModel().getRegisterProperty().getRegisters().get(row);
			
			if (param.equals("addr")) {
				register.setAddress(kv.getValue());
				sortRequired =true;
			}
			else if (param.equals("name")) {
				register.setName(kv.getValue());
			}
			else if (param.equals("desc")) {
				register.setDescription(kv.getValue());
			}
			else if (param.equals("bit")) {
				String bitParam = tmp[2];
				int bitRow = Integer.valueOf(tmp[3]);
				RegisterBit bit = register.getBits().getBits().get(bitRow);
				if (bitParam.equals("bit")) {
					if (kv.getValue().isEmpty()) {
						removes.add(bitRow);
					}
					else {
						bit.setBit(kv.getValue());
					}
				}
				else if (bitParam.equals("size")) {
					bit.setSize(Integer.valueOf(kv.getValue()));
				}
				else if (bitParam.equals("type")) {
					bit.setType(ReadWriteType.valueOf(kv.getValue()));
				}
				else if (bitParam.equals("name")) {
					bit.setName(kv.getValue());
				}
				else if (bitParam.equals("desc")) {
					bit.setDescription(kv.getValue());
				}
			}
			
			register.getBits().removeAll(removes);
			register.getBits().sort();
		}
		
		if (sortRequired) {
			StaticInstances.getInstance().getBuilderModel().getRegisterProperty().sort();
		}
		
		return "OK";
	}
	
	@GET
	@Path("/addNew")
	public String addNew() {
		RegisterProperty registerProperty = StaticInstances.getInstance().getBuilderModel().getRegisterProperty();
		
		int last = registerProperty.getLastDecAddess();
		int address = last + StaticInstances.getInstance().getBuilderModel().getRegisterProperty().getRegisterWidth()/8;
		
		registerProperty.addRegister("NewReg" + Calendar.getInstance().getTimeInMillis(), "0x"+Integer.toHexString(address), "NewRegister");
		
		StaticInstances.getInstance().getBuilderModel().getRegisterMapModel().update();
		return "OK";
	}
	
	@GET
	@Path("/addRow")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addRow(@QueryParam("row") final String row) {
		List<SvRegister> registers = StaticInstances.getInstance().getBuilderModel().getRegisterProperty().getRegisters();
		
		SvRegister newRegister = new SvRegister();
		newRegister.setName("NEW" + Calendar.getInstance().getTimeInMillis());
		int iRow = Integer.valueOf(row);
			
		String currentAddress = registers.get(iRow).getAddress();
		if (currentAddress.contains("-")) {
			currentAddress = currentAddress.split("-")[1];
		}
		int address = Integer.parseInt(currentAddress.replace("0x", ""), 16);
		
		newRegister.setAddress("0x" + Integer.toHexString(address+1));
		if (iRow < registers.size()-1) {
			iRow++;
		}
		
		StaticInstances.getInstance().getBuilderModel().getRegisterProperty().getRegisters().add(iRow, newRegister);

		StaticInstances.getInstance().getBuilderModel().getRegisterMapModel().update();

		return "OK";
	}
	
	@GET
	@Path("/deleteRow")
	@Produces(MediaType.TEXT_PLAIN) 
	public String deleteRow(@QueryParam("row") final Integer row) {
		StaticInstances.getInstance().getBuilderModel().getRegisterProperty().remove(row);
		StaticInstances.getInstance().getBuilderModel().getRegisterMapModel().update();

		return "OK";
	}
	
	@GET
	@Path("/addBitRow")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addBitRow(@QueryParam("row") final int row) {
		SvRegister register = StaticInstances.getInstance().getBuilderModel().getRegisterProperty().getRegisters().get(row);
		register.addBit("new bit", ReadWriteType.RW, "new bit", "new bit");
		
		StaticInstances.getInstance().getBuilderModel().getRegisterMapModel().update();

		return "OK";
	}

	@GET
	@Path("/setCurrentValue")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setCurrentValue(@QueryParam("regName") final String regName, @QueryParam("bitName") final String bitName, @QueryParam("value") final String value) {
		RegisterInfo regInfo = new RegisterInfo(regName, bitName, value, StaticInstances.getInstance().getBuilderModel().getRegisterProperty());
		StaticInstances.getInstance().getSimulator().updateRegister(regInfo.getIntAddress(), regInfo.getDataSet(), regInfo.getMask());	
		return "OK";
	}	

	@POST
	@Path("/setBlockData")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String setBlockData(String data, @QueryParam("regName") final String regName) {
		byte[] b = Base64.decode(data.replace("data:application/octet-stream;base64,", ""));
		
		SvRegister register  = StaticInstances.getInstance().getBuilderModel().getRegisterProperty().getRegisterByName(regName);
		StaticInstances.getInstance().getSimulator().updateBlockData(register.getDecAddress(), b);
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
		SvRegister register  = StaticInstances.getInstance().getBuilderModel().getRegisterProperty().getRegisterByName(regName);
		Map<Long, BitSet> value = StaticInstances.getInstance().getBuilderModel().getRegisterMapModel().getMapValue();
		String address = register.getAddress().replace("0x", "");
		if (address.contains("-")) {
			address = address.split("-")[0];
		}
		long intAddress = Integer.parseInt(address, 16);
		BitSet bitSet = value.get(intAddress);
		
		RegisterBit bit = register.getBits().get(bitName);

		int startBit = bit.getStartBit();
		int endBit = bit.getEndBit();
		int ret = new BitSetToIntConverter().convert(bitSet, startBit, endBit+1);
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
		try {
			Class<?> c = Class.forName(StaticInstances.getInstance().getBuilderModel().getUserApplicationPath() + ".test." + simulator);	
//System.out.println(c);
			SvSimulator object = (SvSimulator)c.getConstructor(RegisterAccess.class).newInstance(StaticInstances.getInstance().getBuilderModel().getRegisterAccess());
//System.out.println(object);
			StaticInstances.getInstance().getBuilderModel().getRegisterMapModel().addSimulator(object);
			StaticInstances.getInstance().getBuilderModel().getRegisterMapModel().update();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "OK";
	}
	
	@GET
	@Path("unloadSimulator")
	@Produces(MediaType.TEXT_PLAIN)
	public String unloadSimulator(@QueryParam("simulator") final String simulator) {
		StaticInstances.getInstance().getBuilderModel().getRegisterMapModel().removeSimulator(simulator);
		return "OK";
	}
	
	@GET
	@Path("/setCheck")
	@Produces(MediaType.TEXT_PLAIN) 
	public String setCheck(@QueryParam("regName") final String regName, @QueryParam("bitName") final String bitName, @QueryParam("value") final String value) {
		StaticInstances.getInstance().getBuilderModel().getRegisterShortCut().updateCheck(regName, bitName, value.equals("on"));
		return "OK";
	}

}
