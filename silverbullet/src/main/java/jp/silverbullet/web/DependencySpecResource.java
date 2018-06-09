package jp.silverbullet.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.silverbullet.BuilderFx;
import jp.silverbullet.SvProperty;
import jp.silverbullet.SvPropertyStore;
import jp.silverbullet.dependency.speceditor3.DependencyBuilder3;
import jp.silverbullet.dependency.speceditor3.DependencyNode;
import jp.silverbullet.dependency.speceditor3.DependencyProperty;
import jp.silverbullet.dependency.speceditor3.DependencySpec2;
import jp.silverbullet.dependency.speceditor3.DependencySpecHolder2;
import jp.silverbullet.dependency.speceditor3.DependencySpecTableGenerator;
import jp.silverbullet.dependency.speceditor3.DependencyTargetElement;
import jp.silverbullet.dependency.speceditor3.ui.DependencyEditorModel;
import jp.silverbullet.dependency.speceditor3.ui.DependencyTableRowData;
import jp.silverbullet.dependency.speceditor3.ui.DependencyTargetConverter;
import jp.silverbullet.property.PropertyHolder;

@Path("/dependencySpec")
public class DependencySpecResource {

	@GET
	@Path("/elements")
	@Produces(MediaType.APPLICATION_JSON) 
	public JsonTable getElements(@QueryParam("id") final String id) {
		SvProperty prop = BuilderFx.getModel().getBuilderModel().getProperty(id);
		DependencySpecHolder2 specHolder = BuilderFx.getModel().getBuilderModel().getDependencySpecHolder2();
		PropertyHolder porpHolder = BuilderFx.getModel().getBuilderModel().getPropertyHolder();
		SvPropertyStore propHolder = BuilderFx.getModel().getBuilderModel().getPropertyStore();
		DependencyEditorModel model = new DependencyEditorModel(prop, specHolder, porpHolder, propHolder);
		
		JsonTable ret = new JsonTable();
		ret.setHeader(Arrays.asList("Element", "Value", "Condition"));
		for (String element :  model.getAllElements(id)) {
			String[] row = new String[3];
			row[0] = element;
			ret.addRow(row);
		}
		return ret;
	}
	
	@GET
	@Path("/specTable")
	@Produces(MediaType.APPLICATION_JSON) 
	public DependencyTableRowData[] getSpecTable(@QueryParam("id") final String id) {
//		JsonTable ret = new JsonTable();
		
		SvProperty property = BuilderFx.getModel().getBuilderModel().getProperty(id);
		DependencySpec2 spec = BuilderFx.getModel().getBuilderModel().getDependencySpecHolder2().get(id);
		
		DependencyEditorModel dependencyEditorModel = new DependencyEditorModel(property, BuilderFx.getModel().getBuilderModel().getDependencySpecHolder2(),
				BuilderFx.getModel().getBuilderModel().getPropertyHolder(), BuilderFx.getModel().getBuilderModel().getPropertyStore());
		List<DependencyTableRowData> table = new DependencySpecTableGenerator(dependencyEditorModel).get(spec);
		
//		ret.setHeader(Arrays.asList("Element","Value", "Condition", "Confirm"));
//		for (DependencyTableRowData row : table) {
//			ret.addRow(Arrays.asList(row.getElement(), row.getValue(), row.getCondition(), row.get));
//		}
		return table.toArray(new DependencyTableRowData[0]);
	}
	
	@GET
	@Path("/addSpec")
	@Produces(MediaType.TEXT_PLAIN)
	public String addSpec(@QueryParam("id") final String id, @QueryParam("element") final String element, 
			@QueryParam("value") final String value, @QueryParam("condition") final String condition) {
		
		DependencyTargetConverter converter = new DependencyTargetConverter(element);
		
		DependencySpec2 spec = BuilderFx.getModel().getBuilderModel().getDependencySpecHolder2().get(id);
		spec.add(converter.getElement(), converter.getSelectionId(), value, condition);
		return "OK";
	}
	
	
	@GET
	@Path("/removeSpec")
	@Produces(MediaType.TEXT_PLAIN)
	public String removeSpec(@QueryParam("id") final String id, @QueryParam("element") final String element, 
			@QueryParam("value") final String value) {
		
		DependencySpec2 spec = BuilderFx.getModel().getBuilderModel().getDependencySpecHolder2().get(id);
		spec.remove(DependencyTargetElement.valueOf(element), value);
		return "OK";
	}
	
	@GET
	@Path("/spec")
	@Produces(MediaType.APPLICATION_JSON) 
	public DependencySpec2 getSpec(@QueryParam("id") final String id) {
		DependencySpec2 spec = BuilderFx.getModel().getBuilderModel().getDependencySpecHolder2().get(id);
		return spec;
	}
	
	
	class DepChain {
		public DepChain(String id, String element) {
			this.id = id;
			this.element = element;
		}
		public String id;
		public String element;
		
		@Override
		public boolean equals(Object arg0) {
			DepChain target = (DepChain)arg0;
			return id.equals(target.id) && element.equals(target.element);
		}
	}
	class DepChainPair{
		public DepChainPair(DepChain from, DepChain to) {
			this.from = from;
			this.to = to;
		}
		public DepChain from;
		public DepChain to;
		@Override
		public boolean equals(Object arg0) {
			DepChainPair target = (DepChainPair)arg0;
			
			return from.equals(target.from) && to.equals(target.to);
		}
	}
	
	@GET
	@Path("/target")
	@Produces(MediaType.APPLICATION_JSON)
	public DepChainPair[] getTarget(@QueryParam("id") final String id) {
		DependencySpecHolder2 holder = BuilderFx.getModel().getBuilderModel().getDependencySpecHolder2();
		DependencyBuilder3 builder = new DependencyBuilder3(id, holder);

		Set<DepChainPair> set = new LinkedHashSet<>();
		getList(id, "Value", builder.getTree(), set);
		
		return set.toArray(new DepChainPair[0]);
	}

	private void getList(String fromId, String fromElement, DependencyNode node, Set<DepChainPair> set) {
		for (DependencyNode child : node.getChildren()) {
			String toId = child.getDependencyProperty().getId();
			addDepChain(fromId, fromElement, set, child, toId);
			String toElement = getElement(child);
			getList(child.getDependencyProperty().getId(), toElement, child, set);
		}
	}

	private void addDepChain(String fromId, String fromElement, Set<DepChainPair> set, DependencyNode child,
			String toId) {
		
		DepChainPair depChain = new DepChainPair(new DepChain(fromId, fromElement), new DepChain(toId, getElement(child)));
		boolean found = false;
		for (DepChainPair p : set) {
			if (p.equals(depChain)) {
				found = true;
				break;
			}
		}
		if (!found) {
			set.add(depChain);
		}
	}

	private String getElement(DependencyNode child) {
		return DependencyTargetConverter.convertToString(child.getDependencyProperty().getElement(), child.getDependencyProperty().getSelectionId());
	}
}
