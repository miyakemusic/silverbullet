package jp.silverbullet.dependency.speceditor3.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.scene.layout.VBox;
import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.speceditor3.DependencyExpression;
import jp.silverbullet.dependency.speceditor3.DependencyTargetElement;
import jp.silverbullet.property.ListDetailElement;
import jp.silverbullet.property.PropertyHolder;

public class ExpressionEditorUi extends VBox {
	
	protected String lastestId = "";
	private ExpressionEditorPane value;
	private ExpressionEditorPane condition;

	public ExpressionEditorUi(SvProperty property, DependencyTargetElement element, PropertyHolder propertyHolder) {
		create(property, element, propertyHolder, "", "");
	}


	public ExpressionEditorUi(SvProperty property, DependencyTargetElement element, PropertyHolder propertyHolder, String value2, String condition2) {
		create(property, element, propertyHolder, value2, condition2);
	}


	private void create(SvProperty property, DependencyTargetElement element, PropertyHolder propertyHolder, String value2, String condition2) {
		this.getChildren().add(value = new ExpressionEditorPane("Value", propertyHolder, value2) {

			@Override
			protected boolean isIdVisible() {
				if (property.isNumericProperty() && element.equals(DependencyTargetElement.Value)) {
					return true;
				}
				return false;
			}

			@Override
			protected List<String> getComparators() {				
				//return Arrays.asList(DependencyExpression.Equals, DependencyExpression.LargerThan, DependencyExpression.SmallerThan, DependencyExpression.SmallerThan);
				return new ArrayList<String>();
			}

			@Override
			protected List<String> getResultCandidates() {
				List<String> ret = new ArrayList<String>();
				
				boolean booleanRequred = isBooleanRequredInResult(property, element);
				
				if (booleanRequred) {
					return Arrays.asList(DependencyExpression.True, DependencyExpression.False);
				}
				else if (property.isNumericProperty() ) {
					
				}
				else if (property.isListProperty() && element.equals(DependencyTargetElement.Value)) {
					for (ListDetailElement e : property.getListDetail()) {
						ret.add("%" + e.getId());
					}
				}
				return ret;
			}

			private boolean isBooleanRequredInResult(SvProperty property, DependencyTargetElement element) {
				if (element.equals(DependencyTargetElement.Enabled) || element.equals(DependencyTargetElement.Visible)
						|| element.equals(DependencyTargetElement.ListItemEnabled) || element.equals(DependencyTargetElement.ListItemVisible)) {
					return true;
				}
				if (property.isBooleanProperty() && element.equals(DependencyTargetElement.Value)) {
					return true;
				}
				return false;
			}

			@Override
			protected List<String> getComparisonTargets() {
				return getEmptyList();
			}
			
		});
		this.getChildren().add(condition = new ExpressionEditorPane("Condition", propertyHolder, condition2) {

			@Override
			protected boolean isIdVisible() {
				return true;
			}

			@Override
			protected List<String> getComparators() {
				return Arrays.asList(DependencyExpression.Equals, DependencyExpression.NotEquals, DependencyExpression.LargerThan, DependencyExpression.SmallerThan);

			}

			@Override
			protected List<String> getResultCandidates() {
				return Arrays.asList(DependencyExpression.AnyValue, DependencyExpression.ELSE);
			}

			@Override
			protected List<String> getComparisonTargets() {
				return getEmptyList();
			}
			
		});
	}


	protected List<String> getEmptyList() {
		return new ArrayList<String>();
	}


	public String getValue() {
		return value.getText();
	}
	
	public String getCondition() {
		return this.condition.getText();
	}
}
