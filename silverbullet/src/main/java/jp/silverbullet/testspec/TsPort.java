package jp.silverbullet.testspec;

public abstract class TsPort {

	private TsNode owner;
	private TsPort pairPort;
	
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

	abstract public String getName();
}
