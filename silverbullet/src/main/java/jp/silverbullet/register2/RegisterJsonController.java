package jp.silverbullet.register2;

import jp.silverbullet.StaticInstances;
import jp.silverbullet.register.RegisterBit;
import jp.silverbullet.register.SvRegister;
import jp.silverbullet.register.RegisterBit.ReadWriteType;
import jp.silverbullet.register.RegisterSpecHolder;
import jp.silverbullet.web.KeyValue;

public class RegisterJsonController {

	private RegisterSpecHolder specHolder;

	public RegisterJsonController(RegisterSpecHolder registerSpecHolder) {
		this.specHolder = registerSpecHolder;
	}

	public void handle(KeyValue[] changes) {
		for (KeyValue kv : changes) {
			String[] tmp = kv.getKey().split("_");
			String param = tmp[0];
			int row = Integer.valueOf(tmp[1]);		
			SvRegister register = this.specHolder.getRegisterByIndex(row);
			
			if (param.equals("addr")) {
				register.setAddressHex(kv.getValue());
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
						register.getBits().remove(bitRow);
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
		}
	}

}
