package jp.silverbullet.testspec;

import java.util.ArrayList;
import java.util.List;

public class PrsNetworkConfiguration {
	public List<PrsNode> list = new ArrayList<>();
	public PrsNode root;
	
	public void add(PrsNode psNode) {
		this.list.add(psNode);
	}

}
