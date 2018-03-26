package jp.silverbullet.uidesigner.widgets;

import java.io.StringReader;

import javax.xml.bind.JAXB;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javafx.embed.swing.SwingNode;
import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.engine.DependencyInterface;
import jp.silverbullet.uidesigner.SvRowData;
import jp.silverbullet.uidesigner.TableContent;

public class SvChartJFree extends SvPropertyWidgetFx {

	private XYSeriesCollection dataSet;
	private JFreeChart lineChart;

	public SvChartJFree(SvProperty prop, DependencyInterface dependencyInterface, Description style, Description description) {
		super(prop, dependencyInterface);
		final SwingNode chartSwingNode = new SwingNode();

		ChartPanel panel = new ChartPanel(
	    		  lineChart = generateLineChart()
	      ) ;
	    chartSwingNode.setContent(panel);
	    chartSwingNode.setMouseTransparent(true);
	    this.getChildren().add(chartSwingNode);
	    
	    new DescriptionApplyer(description) {
			@Override
			protected void setPrefWidth(Double width) {
				SvChartJFree.this.setPrefWidth(width);
			}

			@Override
			protected void setPrefHeight(Double height) {
				SvChartJFree.this.setPrefHeight(height);
			}
	    	
	    };
	}

	private JFreeChart generateLineChart() {
		JFreeChart lineChart = ChartFactory.createXYLineChart(
		         "Title",
		         "X","Y",
		         dataSet = createDataset(),
		         PlotOrientation.VERTICAL,
		         true,true,false);

		lineChart.getXYPlot().getDomainAxis().setAutoRange(false);
		lineChart.getXYPlot().getRangeAxis().setAutoRange(false);
		lineChart.getXYPlot().getRangeAxis().setLowerBound(-60);
		lineChart.getXYPlot().getRangeAxis().setUpperBound(20);
		return lineChart;
	}

	private XYSeriesCollection createDataset( ) {
		   XYSeriesCollection  dataset = new XYSeriesCollection ( );
		   XYSeries series = new XYSeries( "Chrome" );

		   for (double x = 0; x <= 600; x+= 1) {
			   double y = Math.random();
			   series.add(x, y);
			  // series.add(x, y-1);
		   }
		   dataset.addSeries(series);
		   return dataset;
	 }
	@Override
	public void onValueChanged(String id, String value) {
		long start = System.nanoTime();
		StringReader reader = new StringReader(getProperty().getCurrentValue());
		TableContent content = JAXB.unmarshal(reader, TableContent.class);
		this.lineChart.setTitle(content.getTitle());
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
		      
		lineChart.getXYPlot().getDomainAxis().setLabel(content.getHeaders().get(content.getxAxisColumn()));
		lineChart.getXYPlot().getDomainAxis().setLowerBound(xmin);
		lineChart.getXYPlot().getDomainAxis().setUpperBound(xmax);
//		lineChart.getXYPlot().getRangeAxis().setLowerBound(ymin);
//		lineChart.getXYPlot().getRangeAxis().setUpperBound(ymax);
		
        dataSet.removeAllSeries();
        for (int i = content.getxAxisColumn() + 1; i < content.getHeaders().size(); i++) {
        	XYSeries series = new XYSeries(content.getHeaders().get(i));  
            dataSet.addSeries(series);
        }
        
        for (SvRowData rowData : content.getData()) {
        	for (int col = content.getxAxisColumn() + 1; col < content.getHeaders().size(); col++) {
        		double x = Double.valueOf(rowData.getElements().get(content.getxAxisColumn()).getValue());
        		double y = Double.valueOf(rowData.getElements().get(col).getValue()); 
        		dataSet.getSeries(col - content.getxAxisColumn()-1).add(x, y);
        	}
        }
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
