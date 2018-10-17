package jp.silverbullet.web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import jp.silverbullet.StaticInstances;
import jp.silverbullet.SvProperty;
import jp.silverbullet.SvPropertyStore;
import jp.silverbullet.dependency.DependencyBuilder;
import jp.silverbullet.dependency.DependencyEditorModel;
import jp.silverbullet.dependency.DependencyNode;
import jp.silverbullet.dependency.DependencySpec;
import jp.silverbullet.dependency.DependencySpecHolder;
import jp.silverbullet.dependency.DependencySpecTableGenerator;
import jp.silverbullet.dependency.DependencyTableRowData;
import jp.silverbullet.dependency.DependencyTargetConverter;
import jp.silverbullet.property.PropertyHolder;


@Path("/dependencySpec")
public class DependencySpecResource {

	@GET
	@Path("/elements")
	@Produces(MediaType.APPLICATION_JSON) 
	public JsonTable getElements(@QueryParam("id") final String id) {
		SvProperty prop = StaticInstances.getInstance().getInstance().getBuilderModel().getProperty(id);
		DependencySpecHolder specHolder = StaticInstances.getInstance().getInstance().getBuilderModel().getDependencySpecHolder();
		PropertyHolder porpHolder = StaticInstances.getInstance().getInstance().getBuilderModel().getPropertyHolder();
		SvPropertyStore propHolder = StaticInstances.getInstance().getInstance().getBuilderModel().getPropertyStore();
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
		return createSpecTable(id);
	}

	private DependencyTableRowData[] createSpecTable(final String id) {
		SvProperty property = StaticInstances.getInstance().getBuilderModel().getProperty(id);
		DependencySpec spec = StaticInstances.getInstance().getBuilderModel().getDependencySpecHolder().get(id);
		
		DependencyEditorModel dependencyEditorModel = new DependencyEditorModel(property, StaticInstances.getInstance().getBuilderModel().getDependencySpecHolder(),
				StaticInstances.getInstance().getBuilderModel().getPropertyHolder(), StaticInstances.getInstance().getBuilderModel().getPropertyStore());
		List<DependencyTableRowData> table = new DependencySpecTableGenerator(dependencyEditorModel).get(spec);

		return table.toArray(new DependencyTableRowData[0]);
	}

	
	@GET
	@Path("/relationSpecs")
	@Produces(MediaType.APPLICATION_JSON) 
	public Map<String, DependencyTableRowData[]> getRelationIds(@QueryParam("id") final String id) {
		Set<String> ids = new HashSet<>();
		Map<String, DependencyTableRowData[]> map = new HashMap<>();
		
		for (DepChainPair link : createDependencyLink(id)) {
			ids.add(link.from.id);
			ids.add(link.to.id);
		}
		for (String id2 : ids) {
			map.put(id2, this.createSpecTable(id2));
		}
		return map;
	}
	
	@GET
	@Path("/addSpec")
	@Produces(MediaType.TEXT_PLAIN)
	public String addSpec(@QueryParam("id") final String id, @QueryParam("element") final String element, 
			@QueryParam("value") @Encoded final String value, @QueryParam("condition") @Encoded final String condition) {
		
		DependencyTargetConverter converter = new DependencyTargetConverter(element);
		
		DependencySpec spec = StaticInstances.getInstance().getBuilderModel().getDependencySpecHolder().get(id);
		try {
			String value2 = URLDecoder.decode(value, "UTF-8");
			String condition2 = URLDecoder.decode(condition, "UTF-8");
		
			spec.add(converter.getElement(), converter.getSelectionId(), value2, condition2);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "OK";
	}
	
	@GET
	@Path("/editSpec")
	@Produces(MediaType.TEXT_PLAIN)
	public String editSpec(@QueryParam("id") final String id, @QueryParam("element") final String element, 
			@QueryParam("prevValue") @Encoded final String prevValue, @QueryParam("prevCondition") @Encoded final String prevCondition, @QueryParam("prevConfirmation") final String prevConfirmation,
			@QueryParam("value") @Encoded final String value, @QueryParam("condition") @Encoded final String condition, @QueryParam("confirmation") final String confirmation) {
		
		DependencySpec spec = StaticInstances.getInstance().getBuilderModel().getDependencySpecHolder().get(id);
		DependencyTargetConverter converter = new DependencyTargetConverter(element);	
		
		spec.remove(converter.getElement(), converter.getSelectionId(), prevValue, prevCondition);
		spec.add(converter.getElement(), converter.getSelectionId(), value, condition);
		return "OK";
	}
	
	@GET
	@Path("/removeSpec")
	@Produces(MediaType.TEXT_PLAIN)
	public String removeSpec(@QueryParam("id") final String id, @QueryParam("element") final String element, 
			@QueryParam("value") final String value, @QueryParam("condition") final String condition) {
		
		DependencySpec spec = StaticInstances.getInstance().getBuilderModel().getDependencySpecHolder().get(id);
		DependencyTargetConverter converter = new DependencyTargetConverter(element);
		spec.remove(converter.getElement(), converter.getSelectionId(), value, condition);
//		spec.remove(DependencyTargetElement.valueOf(element), value);
		return "OK";
	}
	
	@GET
	@Path("/spec")
	@Produces(MediaType.APPLICATION_JSON) 
	public DependencySpec getSpec(@QueryParam("id") final String id) {
		DependencySpec spec = StaticInstances.getInstance().getBuilderModel().getDependencySpecHolder().get(id);
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
		return createDependencyLink(id);
	}

	@GET
	@Path("/createNew")
	@Produces(MediaType.TEXT_PLAIN) 
	public String createNew(@QueryParam("id") final String id) {
		DependencySpecHolder holder = StaticInstances.getInstance().getBuilderModel().getDependencySpecHolder();
		DependencySpec spec = holder.get(id);
//		spec.add(DependencyTargetElement.Enabled, DependencyExpression.True, "");
		return "OK";
	}
	
	private DepChainPair[] createDependencyLink(final String id) {
		DependencySpecHolder holder = StaticInstances.getInstance().getBuilderModel().getDependencySpecHolder();
		DependencyBuilder builder = new DependencyBuilder(id, holder);

		Set<DepChainPair> set = new LinkedHashSet<>();
		createLinkList(id, "Value", builder.getTree(), set);
		
		return set.toArray(new DepChainPair[0]);
	}

	@GET
	@Path("/ids")
	@Produces(MediaType.APPLICATION_JSON)
	public JsonTable getIds() {
		DependencySpecHolder holder = StaticInstances.getInstance().getBuilderModel().getDependencySpecHolder();
		
		JsonTable ret = new JsonTable();
		
		Set<String> tmps = new HashSet<String>();
		for (String id : holder.getSpecs().keySet()) {
			DependencySpec spec = holder.getSpecs().get(id);
			tmps.addAll(spec.getTriggerIds());
			tmps.add(id);
		}
		
		for (String id2 : tmps) {
			SvProperty prop = StaticInstances.getInstance().getBuilderModel().getProperty(id2);
			ret.addRow(Arrays.asList(prop.getTitle(), prop.getId(), prop.getType(), prop.getComment()));
		}
		
		return ret;
	}
	
	private void createLinkList(String fromId, String fromElement, DependencyNode node, Set<DepChainPair> set) {
		for (DependencyNode child : node.getChildren()) {
			String toId = child.getDependencyProperty().getId();
			addDepChain(fromId, fromElement, set, child, toId);
			String toElement = getElement(child);
			createLinkList(child.getDependencyProperty().getId(), toElement, child, set);
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
