package jp.silverbullet.uidesigner.widgets;

import eu.hansolo.medusa.FGauge;
import eu.hansolo.medusa.FGaugeBuilder;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.GaugeDesign;
import eu.hansolo.medusa.GaugeDesign.GaugeBackground;
import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.DependencyInterface;

public class SvGaugeFx extends SvPropertyWidgetFx {

	private Gauge gauge;

	public SvGaugeFx(SvProperty prop, DependencyInterface svPanelHandler, Description style, Description description) {
		super(prop, svPanelHandler);
		
		int width = 200;
		if (description.isDefined(Description.WIDTH)) {
			width = Integer.valueOf(description.getValue(Description.WIDTH));
		}
		int height = 200;
		if (description.isDefined(Description.HEIGHT)) {
			height = Integer.valueOf(description.getValue(Description.HEIGHT));
		}
		gauge = GaugeBuilder.create()  
                .title(prop.getTitle())  
                //.subTitle("SubTitle")  
                .unit(prop.getUnit())   
                .prefHeight(height)
                .prefWidth(width)
                .build();  
		FGauge fGauge = FGaugeBuilder  
			     .create()  
			     .prefSize(width, height)  
			     .gauge(gauge)  
			     .gaugeDesign(GaugeDesign.METAL)  
			     .gaugeBackground(GaugeBackground.CARBON)  
			     .foregroundVisible(true)  
			     .build();  
		gauge.setMinMeasuredValue(Double.valueOf(prop.getMin()));
		gauge.setMaxMeasuredValue(Double.valueOf(prop.getMax()));
		gauge.setMouseTransparent(true);
		this.getChildren().add(gauge);
	}

	@Override
	public void onValueChanged(String id, String value) {
		gauge.setValue(Double.valueOf(value));
	}

	@Override
	public void onEnableChanged(String id, boolean b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFlagChanged(String id, Flag flag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onListMaskChanged(String id, String string) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTitleChanged(String id, String title) {
		// TODO Auto-generated method stub
		
	}

}
