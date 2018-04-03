package jp.silverbullet.dependency.speceditor3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="DependencySpec")
public class DependencySpec2 {	
	public static final String DefaultItem = "DefaultItem";
	
	private HashMap<DependencyTargetElement, DependencyExpressionHolderMap> depExpHolderMap = new HashMap<>();
	private String id;

	public DependencySpec2() {}
	
	public DependencySpec2(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void add(DependencyExpressionHolder specDetail) {
		getSpecs(specDetail.getTargetElement()).add(specDetail);
	}

	public void add(String targetListItem, DependencyExpressionHolder specDetail) {
		getSpecs(specDetail.getTargetElement(), targetListItem).add(specDetail);
	}


	private List<DependencyExpressionHolder> getSpecs(DependencyTargetElement targetElement, String targetListItem) {
		if (!depExpHolderMap.containsKey(targetElement)) {
			DependencyExpressionHolderMap map2 = new DependencyExpressionHolderMap();
			depExpHolderMap.put(targetElement, map2);
		}
		
		DependencyExpressionHolderMap map2 = depExpHolderMap.get(targetElement);
		if (!map2.containsKey(targetListItem)) {
			map2.put(targetListItem, new ArrayList<DependencyExpressionHolder>());
		}
		return map2.get(targetListItem);
	}

	private List<DependencyExpressionHolder> getSpecs(DependencyTargetElement targetElement) {
		return getSpecs(targetElement, DefaultItem);
	}

	public List<DependencyProperty> findToBeChangedBy(String targetId) {
		List<DependencyProperty> ret = new ArrayList<>();
		if (id.equals(targetId)) {
			return ret;
		}
		for (DependencyExpressionHolderMap map2 : this.depExpHolderMap.values())  {
			for (String key : map2.keySet()) {
				List<DependencyExpressionHolder> list = map2.get(key);
				for (DependencyExpressionHolder expressionHolder : list) {
					List<DependencyProperty> props = expressionHolder.getRelatedSpecs(id, key, targetId, expressionHolder.getTargetElement());
					for (DependencyProperty p : props) {
						p.setSettingDisabledBehavior(expressionHolder.getSettingDisabledBehavior());
					}
					ret.addAll(props);
				}
			}
		}
		DependencyProperty other = null;
		for (DependencyProperty dp : ret) {
			if (dp.getCondition().equals(DependencyExpression.ELSE)) {
				other = dp;
			}
		}
		if (other != null) {
			for (DependencyProperty dp : ret) {
				if (dp.equals(other)) {
					continue;
				}
				other.addOtherSource(dp);
				dp.setOther(other);
			}
			/// moves to tail
			ret.remove(other);
			ret.add(other);
		}
		return ret;
	}

	public HashMap<DependencyTargetElement, DependencyExpressionHolderMap> getDepExpHolderMap() {
		return depExpHolderMap;
	}

	public void setDepExpHolderMap(HashMap<DependencyTargetElement, DependencyExpressionHolderMap> depExpHolderMap) {
		this.depExpHolderMap = depExpHolderMap;
	}

	public boolean remove(DependencyExpression pointer) {
		for (DependencyExpressionHolderMap map2 : this.depExpHolderMap.values())  {
			for (String key : map2.keySet()) {
				List<DependencyExpressionHolder> list = map2.get(key);
				for (DependencyExpressionHolder expressionHolder : list) {
					if (expressionHolder.remove(pointer)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public Set<String> getTriggerIds() {
		Set<String> ret = new HashSet<String>();
		for (DependencyExpressionHolderMap map2 : this.depExpHolderMap.values())  {
			for (String key : map2.keySet()) {
				List<DependencyExpressionHolder> list = map2.get(key);
				for (DependencyExpressionHolder expressionHolder : list) {
					ret.addAll(expressionHolder.getTriggerIds());
				}
			}
		}
		
		return ret;
	}

}
