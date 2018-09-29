package jp.silverbullet.uidesigner.widgets;

import java.io.StringReader;

import javax.xml.bind.JAXB;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.shape.Line;
import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.DependencyInterface;
import jp.silverbullet.uidesigner.SvRowData;
import jp.silverbullet.uidesigner.TableContent;

public class SvChartFx extends SvPropertyWidgetFx {

	private Description description;
	private LineChart<Number,Number> lineChart;
	private ObservableList<Series<Number, Number>> data;
	
	public SvChartFx(SvProperty prop, DependencyInterface widgetListener, Description description) {
		super(prop, widgetListener);

		this.description = description;
		//updateGraph();
		createChart();
	}

	private void createChart() {
        final NumberAxis xAxis = new NumberAxis(1250, 1650, 100);
        final NumberAxis yAxis = new NumberAxis(-50, 10, 10);  
        data = FXCollections.observableArrayList();
        lineChart = new
        		LineChart<Number,Number>(xAxis,yAxis, data);
        lineChart.getXAxis().setAutoRanging(false);
        lineChart.getYAxis().setAutoRanging(false);
        lineChart.setAnimated(false);
        lineChart.setCreateSymbols(false);
        lineChart.getStylesheets().add(this.getClass().getResource("chart.css").toExternalForm());
        this.getChildren().add(lineChart);
	}

	@Override
	public void onValueChanged(String id, String value) {
		//updateGraph();
		updateChart();
	}

	private void updateChart() {
		if (getProperty().getCurrentValue().isEmpty()) {
			return;
		}
		
		long start = System.nanoTime();
		

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
		               
        ((NumberAxis)lineChart.getXAxis()).setLowerBound(xmin);
        ((NumberAxis)lineChart.getXAxis()).setUpperBound(xmax);

        data.clear();
        for (int i = content.getxAxisColumn() + 1; i < content.getHeaders().size(); i++) {
            XYChart.Series series1 = new XYChart.Series();
            series1.setName(content.getHeaders().get(i));   
            data.add(series1);
        }
        
        for (SvRowData rowData : content.getData()) {
        	for (int col = content.getxAxisColumn() + 1; col < content.getHeaders().size(); col++) {
        		double x = Double.valueOf(rowData.getElements().get(content.getxAxisColumn()).getValue());
        		double y = Double.valueOf(rowData.getElements().get(col).getValue()); 
        		data.get(col - content.getxAxisColumn()-1).getData().add(new XYChart.Data(x, y));
        	}
        }
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
        lineChart.getStylesheets().add(this.getClass().getResource("chart.css").toExternalForm());
       
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
        
//        Line line = new Line(10, 10, 10, 200);
//        this.getChildren().add(line);
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
