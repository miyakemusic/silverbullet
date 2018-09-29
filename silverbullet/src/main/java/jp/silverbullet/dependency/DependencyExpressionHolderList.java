package jp.silverbullet.dependency;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class DependencyExpressionHolderList {
	private ArrayList<DependencyExpressionHolder> dependencyExpressionHolders = new ArrayList<>();

	public DependencyExpressionHolderList() {}
	
	public DependencyExpressionHolderList(ArrayList<DependencyExpressionHolder> dependencyExpressionHolders) {
		this.dependencyExpressionHolders = dependencyExpressionHolders;
	}
	
	public ArrayList<DependencyExpressionHolder> getDependencyExpressionHolders() {
		return dependencyExpressionHolders;
	}

	public void setDependencyExpressionHolders(ArrayList<DependencyExpressionHolder> dependencyExpressionHolders) {
		this.dependencyExpressionHolders = dependencyExpressionHolders;
	}
	
}
