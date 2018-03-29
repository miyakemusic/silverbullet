package jp.silverbullet.uidesigner.widgets;

public abstract class DescriptionApplyer {
	abstract protected void setPrefWidth(Double width);
	abstract protected void setPrefHeight(Double height);
	
	public DescriptionApplyer(Description description) {
        String height = description.getValue(Description.HEIGHT);
        String width = description.getValue(Description.WIDTH);
        if (!height.isEmpty()) {
        	setPrefHeight(Double.valueOf(height));
        }
        if (!width.isEmpty()) {
        	setPrefWidth(Double.valueOf(width));
        }
	}
}
