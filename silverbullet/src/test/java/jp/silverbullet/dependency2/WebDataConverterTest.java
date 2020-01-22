package jp.silverbullet.dependency2;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import jp.silverbullet.core.PropertyGetter;
import jp.silverbullet.core.dependency2.DependencySpec;
import jp.silverbullet.core.dependency2.DependencySpecHolder;
import jp.silverbullet.core.dependency2.WebDataConverter;
import jp.silverbullet.core.dependency2.WebDependencySpec;
import jp.silverbullet.core.property2.RuntimeProperty;

public class WebDataConverterTest {

	@Test
	public void testWebDataConverter() {
		PropertyStoreForTest store = new PropertyStoreForTest();
		store.addListProperty("ID_MIDDLE", Arrays.asList("ID_MIDDLE_A1", "ID_MIDDLE_A2", "ID_MIDDLE_B1", "ID_MIDDLE_B2"), "ID_MIDDLE_A1");
		store.addListProperty("ID_ROOT", Arrays.asList("ID_ROOTA", "ID_ROOTA2", "ID_ROOTB"), "ID_ROOTA");

		DependencySpec spec = new DependencySpec("ID_MIDDLE");
		spec.addEnable(DependencySpec.True, "$ID_ROOT==%ID_ROOTA");
		spec.addEnable(DependencySpec.False, DependencySpec.Else);
		
		spec.addOptionEnabled("ID_MIDDLE_A1", DependencySpec.True, "$ID_ROOT==%ID_ROOTA");
		spec.addOptionEnabled("ID_MIDDLE_A1", DependencySpec.True, "$ID_ROOT==%ID_ROOTA2");
		spec.addOptionEnabled("ID_MIDDLE_A1", DependencySpec.False, DependencySpec.Else);
		
		spec.addOptionEnabled("ID_MIDDLE_A2", DependencySpec.True, "$ID_ROOT==%ID_ROOTA");
		spec.addOptionEnabled("ID_MIDDLE_A2", DependencySpec.False, DependencySpec.Else);
		
		spec.addOptionEnabled("ID_MIDDLE_B1", DependencySpec.True, "$ID_ROOT==%ID_ROOTB");
		spec.addOptionEnabled("ID_MIDDLE_B1", DependencySpec.False, DependencySpec.Else);
		
		spec.addOptionEnabled("ID_MIDDLE_B2", DependencySpec.True, "$ID_ROOT==%ID_ROOTB");
		spec.addOptionEnabled("ID_MIDDLE_B2", DependencySpec.False, DependencySpec.Else);

		DependencySpecHolder specHolder = new DependencySpecHolder();
		specHolder.addSpec(spec);
		
		WebDataConverter converter = new WebDataConverter(specHolder, new PropertyGetter() {
			@Override
			public RuntimeProperty getProperty(String id) {
				return store.getProperty(id);
			}

			@Override
			public RuntimeProperty getProperty(String id, int index) {
				return store.getProperty(id, index);
			}
		});
		WebDependencySpec webSpec = converter.getSpec("ID_MIDDLE");
		
	}

}
