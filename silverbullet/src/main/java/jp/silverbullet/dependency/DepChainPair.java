package jp.silverbullet.dependency;

public class DepChainPair{
	public DepChainPair(IdElement from, IdElement to) {
		this.from = from;
		this.to = to;
	}
	public IdElement from;
	public IdElement to;
	@Override
	public boolean equals(Object arg0) {
		DepChainPair target = (DepChainPair)arg0;
		
		return from.equals(target.from) && to.equals(target.to);
	}
}
