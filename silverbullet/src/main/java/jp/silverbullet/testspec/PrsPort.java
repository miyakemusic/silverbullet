package jp.silverbullet.testspec;

public class PrsPort {
	
	public PrsPort() {}
	public PrsPort(String id2, String name2, String pairPortId2, TsPortConfig tsPortConfig) {
		this.id = id2;
		this.name = name2;
		this.pairPortId = pairPortId2;
		this.config = tsPortConfig;
	}
	public String id;
	public String name;
	public String pairPortId;
	public TsPortConfig config;
}
