package jp.silverbullet.uidesigner.widgets;

import java.io.StringReader;

import javax.xml.bind.JAXB;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.shape.Line;
import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.engine.DependencyInterface;
import jp.silverbullet.uidesigner.SvRowData;
import jp.silverbullet.uidesigner.TableContent;

public class SvChartFx extends SvPropertyWidgetFx {

	private Description description;

	public SvChartFx(SvProperty prop, DependencyInterface widgetListener, Description description) {
		super(prop, widgetListener);

		this.description = description;
		updateGraph();
	}

	@Override
	public void onValueChanged(String id, String value) {
		updateGraph();

	}

	protected void updateGraph() {
		if (getProperty().getCurrentValue().isEmpty()) {
			return;
		}
		
		this.getChildren().clear();
		StringReader reader = new StringReader(getProperty().getCurrentValue());
		TableContent content = JAXB.unmarshal(reader, TableContent.class);
		
		double xmin = Double.MAX_VALUE, xmax = Double.MIN_VALUE;
		double ymin = Double.MAX_VALUE, ymax = Double.MIN_VALUE;
		for (SvRowData rowData : content.getData()) {
			for (int col = content.getxAxisColumn(); col < content.getHeaders().size(); col++) {
				double v = Double.valueOf(rowData.getElements().get(col).getValue());
				if (col == content.getxAxisColumn()) {
					xmin = Math.min(xmin, v);
					xmax = Math.max(xmax, v);
				}
				else {
					ymin = Math.min(ymin, v);
					ymax = Math.max(ymax, v);			
				}
			}
		}
		
		ymin = Math.round(ymin) * 2;
		ymax = Math.round(ymax) * 2;
		
        final NumberAxis xAxis = new NumberAxis(xmin, xmax, (xmax - xmin)/10);
        final NumberAxis yAxis = new NumberAxis(-50, 10, 10);  
        
        final LineChart<Number,Number> lineChart = new
        		LineChart<Number,Number>(xAxis,yAxis);
        lineChart.setCreateSymbols(false);
        //lineChart.setStyle(".chart-series-line { -fx-stroke-width: 2px;}");
        lineChart.getStylesheets().add(this.getClass().getResource("chart.css").toExternalForm());
 //       xAxis.setLabel(content.getHeaders().get(content.getxAxisColumn()));                
 //       yAxis.setLabel("Level");
 //       lineChart.setTitle(content.getTitle());
       
        for (int i = content.getxAxisColumn() + 1; i < content.getHeaders().size(); i++) {
            XYChart.Series series1 = new XYChart.Series();
            series1.setName(content.getHeaders().get(i));   
           
            lineChart.getData().add(series1);
        }
        
        for (SvRowData rowData : content.getData()) {
        	for (int col = content.getxAxisColumn() + 1; col < content.getHeaders().size(); col++) {
        		double x = Double.valueOf(rowData.getElements().get(content.getxAxisColumn()).getValue());
        		double y = Double.valueOf(rowData.getElements().get(col).getValue()); 
        		lineChart.getData().get(col - content.getxAxisColumn()-1).getData().add(new XYChart.Data(x, y));
        	}
        }

        
        this.getChildren().add(lineChart);
  
        String height = description.getValue(Description.HEIGHT);
        String width = description.getValue(Description.WIDTH);
        if (!height.isEmpty()) {
        	lineChart.setMinHeight(Double.valueOf(height));
        	lineChart.setMaxHeight(Double.valueOf(height));
       // 	lineChart.heightProperty().add(Double.valueOf(height));
        }
        if (!width.isEmpty()) {
        	lineChart.setMinWidth(Double.valueOf(width));
        	lineChart.setMaxWidth(Double.valueOf(width));
        }
        
        Line line = new Line(10, 10, 10, 200);
        this.getChildren().add(line);
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
