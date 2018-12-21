package jp.silverbullet.dependency;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import jp.silverbullet.dependency.speceditor3.SvPropertyFactory;
import jp.silverbullet.property.SvProperty;
import jp.silverbullet.web.ui.PropertyGetter;
import obsolute.DependencyExpression;
import obsolute.DependencySpecHolder;
import obsolute.DependencyTargetElement;
import obsolute.alternative.AlternativeDependencyGenerator;
import obsolute.alternative.TrueConditionGenerator;

class CandidateGeneratorTest {

	@Test
	void testCandidateGenerator() {
		
		Map<String, SvProperty> map = new HashMap<>();
		SvPropertyFactory factory = new SvPropertyFactory();
		map.put("ID_MODE", factory.getListProperty("ID_MODE", Arrays.asList("ID_MODE_AAA", "ID_MODE_BBB", "ID_MODE_CCC"), "ID_MODE_AAA"));
		map.put("ID_ROOT", factory.getListProperty("ID_ROOT", Arrays.asList("ID_ROOT_1", "ID_ROOT_2", "ID_ROOT_3"), "ID_ROOT_1"));
		PropertyGetter getter = new PropertyGetter() {
			@Override
			public SvProperty getProperty(String id) {
				return map.get(id);
			}
		};
		
		try {
			List<String> candidates = new TrueConditionGenerator().getCandidates(DependencyExpression.False, "$ID_MODE.Value==%ID_MODE_AAA", getter);
			assertEquals(2, candidates.size());
			assertEquals("$ID_MODE.Value==%ID_MODE_BBB", candidates.get(0));
			assertEquals("$ID_MODE.Value==%ID_MODE_CCC", candidates.get(1));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		try {
			List<String> candidates = new TrueConditionGenerator().getCandidates(DependencyExpression.False, "$ID_MODE.Value!=%ID_MODE_AAA", getter);
			assertEquals(1, candidates.size());
			assertEquals("$ID_MODE.Value==%ID_MODE_AAA", candidates.get(0));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		try {
			List<String> candidates = new TrueConditionGenerator().getCandidates(DependencyExpression.False, "($ID_MODE.Value==%ID_MODE_AAA)||($ID_MODE.Value==%ID_MODE_BBB)", getter);
			assertEquals(1, candidates.size());
			assertEquals("$ID_MODE.Value==%ID_MODE_CCC", candidates.get(0));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		try {
			List<String> candidates = new TrueConditionGenerator().getCandidates(DependencyExpression.True, "$ID_MODE.Value==%ID_MODE_AAA", getter);
			assertEquals(1, candidates.size());
			assertEquals("$ID_MODE.Value==%ID_MODE_AAA", candidates.get(0));
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		try {
			List<String> candidates = new TrueConditionGenerator().getCandidates(DependencyExpression.True, "($ID_MODE.Value==%ID_MODE_AAA)||($ID_MODE.Value==%ID_MODE_BBB)", getter);
			assertEquals(2, candidates.size());
			assertEquals("$ID_MODE.Value==%ID_MODE_AAA", candidates.get(0));
			assertEquals("$ID_MODE.Value==%ID_MODE_BBB", candidates.get(1));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	
		try {
			List<String> candidates = new TrueConditionGenerator().getCandidates(DependencyExpression.True, "($ID_MODE.Value!=%ID_MODE_AAA)&&($ID_MODE.Value!=%ID_MODE_BBB)", getter);
			assertEquals(1, candidates.size());
			assertEquals("$ID_MODE.Value==%ID_MODE_CCC", candidates.get(0));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		

		{
			AlternativeDependencyGenerator generator = new AlternativeDependencyGenerator();
			DependencySpecHolder holder = new DependencySpecHolder();
			holder.get("ID_MODE").add(DependencyTargetElement.ListItemEnabled, "ID_MODE_A", DependencyExpression.True, "$ID_ROOT=%ID_ROOT_A");
			holder.get("ID_MODE").add(DependencyTargetElement.ListItemEnabled, "ID_MODE_A", DependencyExpression.False, DependencyExpression.ELSE);
			holder =  generator.convert(holder, getter);
			holder.get("ID_ROOT").getDepExpHolderMap();
		}
		
		try {
			List<String> candidates = new TrueConditionGenerator().getCandidatesElse(DependencyExpression.True, "$ID_MODE.Value==%ID_MODE_AAA", getter);
			assertEquals(1, candidates.size());
			assertEquals("$ID_MODE.Value==%ID_MODE_AAA", candidates.get(0));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		try {
			List<String> candidates = new TrueConditionGenerator().getCandidatesElse(DependencyExpression.True, "($ID_MODE.Value==%ID_MODE_AAA)||($ID_MODE.Value==%ID_MODE_BBB)", getter);
			assertEquals(2, candidates.size());
			assertEquals("$ID_MODE.Value==%ID_MODE_AAA", candidates.get(0));
			assertEquals("$ID_MODE.Value==%ID_MODE_BBB", candidates.get(1));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		try {
			List<String> candidates = new TrueConditionGenerator().getCandidatesElse(DependencyExpression.False, "$ID_MODE.Value==%ID_MODE_AAA", getter);
			assertEquals(2, candidates.size());
			assertEquals("$ID_MODE.Value==%ID_MODE_BBB", candidates.get(0));
			assertEquals("$ID_MODE.Value==%ID_MODE_CCC", candidates.get(1));

		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		try {
			List<String> candidates = new TrueConditionGenerator().getCandidatesElse(DependencyExpression.False, "$ID_MODE.Value!=%ID_MODE_AAA", getter);
			assertEquals(1, candidates.size());
			assertEquals("$ID_MODE.Value==%ID_MODE_AAA", candidates.get(0));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

}
