package jp.silverbullet.register.json;

import java.util.ArrayList;
import java.util.List;

import jp.silverbullet.register2.RegisterBit;

public class SvRegisterJson {
	private List<RegisterBit> bits = new ArrayList<RegisterBit>();
	private String name;
	private String description;
	private String address;
	
	public void addRegisterBit(RegisterBit registerBit) {
		this.bits.add(registerBit);
	}

	public void addAll(List<RegisterBit> bits2) {
		this.bits.addAll(bits2);
	}

	public List<RegisterBit> getBits() {
		return bits;
	}

	public void setBits(List<RegisterBit> bits) {
		this.bits = bits;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getAddress() {
		return address;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	
}
