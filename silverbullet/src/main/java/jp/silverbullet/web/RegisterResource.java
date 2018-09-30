package jp.silverbullet.web;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.silverbullet.StaticInstances;
import jp.silverbullet.register.BitSetToIntConverter;
import jp.silverbullet.register.RegisterBit;
import jp.silverbullet.register.RegisterBit.ReadWriteType;
import jp.silverbullet.register.RegisterProperty;
import jp.silverbullet.register.SvRegister;
import jp.silverbullet.register.json.SvRegisterJson;
import jp.silverbullet.register.json.SvRegisterJsonHolder;

@Path("/register")
public class RegisterResource {

	@GET
	@Path("/getRegisters")
	@Produces(MediaType.APPLICATION_JSON) 
	public SvRegisterJsonHolder getRegisters() {
		RegisterProperty regProp =  StaticInstances.getBuilderModel().getRegisterProperty();
		
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
			SvRegister register =  StaticInstances.getBuilderModel().getRegisterProperty().getRegisters().get(row);
			
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
			StaticInstances.getBuilderModel().getRegisterProperty().sort();
		}
		
		return "OK";
	}
	
	@GET
	@Path("/addRow")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addRow(@QueryParam("row") final String row) {
		List<SvRegister> registers = StaticInstances.getBuilderModel().getRegisterProperty().getRegisters();
		
		SvRegister newRegister = new SvRegister();
		newRegister.setName("NEW");
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
		
		StaticInstances.getBuilderModel().getRegisterProperty().getRegisters().add(iRow, newRegister);
	
		return "OK";
	}
	
	@GET
	@Path("/addBitRow")
	@Produces(MediaType.TEXT_PLAIN) 
	public String addBitRow(@QueryParam("row") final int row) {
		SvRegister register = StaticInstances.getBuilderModel().getRegisterProperty().getRegisters().get(row);
//		register.addBit("new", 0, 0, ReadWriteType.RW, "description", "definition");
		register.addBit("new bit", ReadWriteType.RW, "new bit", "new bit");
		return "OK";
	}

	@GET
	@Path("/getCurrentValue")
	@Produces(MediaType.TEXT_PLAIN) 
	public String getCurrentValue(@QueryParam("regName") final String regName, @QueryParam("bitName") final String bitName) {
		SvRegister register  = StaticInstances.getBuilderModel().getRegisterProperty().getRegisterByName(regName);
		Map<Long, BitSet> value = StaticInstances.getRegisterMapModel().getMapValue();
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
		return StaticInstances.getRegisterMapModel().getSimulatorClasses();
	}
	
	@GET
	@Path("setSimulator")
	@Produces(MediaType.TEXT_PLAIN)
	public String setSimulator(@QueryParam("simulator") final String simulator) {
		StaticInstances.getRegisterMapModel().setSimulatorClass(simulator);
		StaticInstances.getRegisterMapModel().setSimulatorEnabled(true);
		StaticInstances.getRegisterMapModel().update();
		return "OK";
	}
}