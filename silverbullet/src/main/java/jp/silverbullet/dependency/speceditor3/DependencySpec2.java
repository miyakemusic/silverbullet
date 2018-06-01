package jp.silverbullet.dependency.speceditor3;

import java.util.ArrayList;
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

	public List<DependencyProperty> findToBeChangedBy(String triggerId) {
		List<DependencyProperty> ret = new ArrayList<>();
		if (id.equals(triggerId)) {
			return ret;
		}
		for (DependencyExpressionHolderMap expressionHolderMap : this.depExpHolderMap.values())  {
			for (String selectionId : expressionHolderMap.keySet()) {
				List<DependencyExpressionHolder> expressionHolders = expressionHolderMap.get(selectionId);
				for (DependencyExpressionHolder expressionHolder : expressionHolders) {
					List<DependencyProperty> props = expressionHolder.getRelatedSpecs(id, selectionId, triggerId, expressionHolder.getTargetElement());
					for (DependencyProperty p : props) {
						p.setSettingDisabledBehavior(expressionHolder.getSettingDisabledBehavior());
					}
					ret.addAll(props);
				}
			}
		}
		
		// Connects else condition
//		DependencyProperty other = null;
//		for (DependencyProperty dp : ret) {
//			if (dp.getCondition().equals(DependencyExpression.ELSE)) {
//				other = dp;
//			}
//		}
//		if (other != null) {
//			boolean elseValid = true;
//			for (DependencyProperty dp : ret) {
//				if (dp.equals(other)) {
//					continue;
//				}
//				dp.setOther(other);
//				elseValid = true;
//			}
//			/// moves to tail
//			ret.remove(other); 
//			if (elseValid) { // if else condition is alone, that's invalid.
//				ret.add(other);
//			}
//		}
		return ret;
	}

	public HashMap<DependencyTargetElement, DependencyExpressionHolderMap> getDepExpHolderMap() {
		return depExpHolderMap;
	}

	public void setDepExpHolderMap(HashMap<DependencyTargetElement, DependencyExpressionHolderMap> depExpHolderMap) {
		this.depExpHolderMap = depExpHolderMap;
	}

	public boolean remove(DependencyExpression pointer) {
		boolean ret = false;
		DependencyTargetElement elementRemoved = null;
		for (DependencyTargetElement element: this.depExpHolderMap.keySet())  {
			DependencyExpressionHolderMap map2 = this.depExpHolderMap.get(element);
			String keyRemoved = "";
			for (String key : map2.keySet()) {
				List<DependencyExpressionHolder> list = map2.get(key);
				List<DependencyExpressionHolder> toBeRemoved = new ArrayList<>();
				for (DependencyExpressionHolder expressionHolder : list) {
					if (expressionHolder.remove(pointer)) {
						ret = true;
						if (expressionHolder.isEmpty()) {
							toBeRemoved.add(expressionHolder);
						}
						break;
					}

				}
				list.removeAll(toBeRemoved);
				if (ret) {
					if (list.isEmpty()) {
						keyRemoved = key;
					}
					break;
					//return true;
				}
			}
			if (!keyRemoved.isEmpty()) {
				map2.remove(keyRemoved);
				if (map2.isEmpty()) {
					elementRemoved = element;
				}
				break;
			}
		}
	
		if (elementRemoved != null) {
			depExpHolderMap.remove(elementRemoved);
		}
		return ret;
	}

	public Set<String> getTriggerIds() {
		Set<String> ret = new HashSet<String>();
		for (DependencyTargetElement target : this.depExpHolderMap.keySet())  {
			DependencyExpressionHolderMap map2 = depExpHolderMap.get(target);
			for (String key : map2.keySet()) {
				List<DependencyExpressionHolder> list = map2.get(key);
				for (DependencyExpressionHolder expressionHolder : list) {
					ret.addAll(expressionHolder.getTriggerIds());
				}
			}
		}
		
		return ret;
	}

	public DependencyExpression get(DependencyTargetElement element, String selection, String value, String condition) {
		DependencyExpressionHolderMap  map = this.depExpHolderMap.get(element);
		if (selection.isEmpty()) {
			selection = DefaultItem;
		}
		for (DependencyExpressionHolder spec : map.get(selection)) {
			for (DependencyExpression exp : spec.getExpressions().get(value).getDependencyExpressions()) {
				if (exp.getExpression().getExpression().equals(condition)) {
					return exp;
				}
			}
		}
		return null;
	}

	public boolean isEmpty() {
		return depExpHolderMap.isEmpty();
	}

	public DependencyExpressionHolder getDependencyExpressionHolder(DependencyTargetElement e, String selectionId) {
		if (selectionId == null || selectionId.isEmpty()) {
			selectionId = DefaultItem;
		}
		if (!this.depExpHolderMap.containsKey(e)) {
			DependencyExpressionHolderMap map = new DependencyExpressionHolderMap();
			this.depExpHolderMap.put(e, map);
		}
//		if (!selectionId.equals(DefaultItem)) {
			if (!this.depExpHolderMap.get(e).containsKey(selectionId)) {
				this.depExpHolderMap.get(e).put(selectionId, new ArrayList<DependencyExpressionHolder>());
			}
//		}
		if (this.depExpHolderMap.get(e).get(selectionId).size() == 0) {
			this.depExpHolderMap.get(e).get(selectionId).add(new DependencyExpressionHolder(e));
		}
		DependencyExpressionHolder ret = this.depExpHolderMap.get(e).get(selectionId).get(0);
		
		return ret;
	}

}
