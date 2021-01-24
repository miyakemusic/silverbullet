package jp.silverbullet.testspec;

public abstract class TsPort {

	private TsNode owner;
	private TsPort pairPort = null;
	public String id;
	private TsPortConfig config;

	abstract public String getName();
	
	public TsPort() {}
	public TsPort(TsNode owner2) {
		this.owner = owner2;
	}
	
	public TsNode owner() {
		return this.owner;
	}

	public void connect(TsPort to2) {
		this.pairPort = to2;
		to2.pairPort(this);
	}

	private void pairPort(TsPort tsPort) {
		this.pairPort = tsPort;
	}

	public TsPort pairPort() {
		return pairPort;
	}	

	public void terminate() {
		this.pairPort = null;
	}

	public boolean terminated() {
		return this.pairPort == null;
	}

	public TsNode getOwner() {
		return owner;
	}

	public void setOwner(TsNode owner) {
		this.owner = owner;
	}

	public TsPort getPairPort() {
		return pairPort;
	}

	public void setPairPort(TsPort pairPort) {
		this.pairPort = pairPort;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void config(TsPortConfig config) {
		this.config = config;
	}

	public TsPortConfig config() {
		return this.config;
	}
	
}
