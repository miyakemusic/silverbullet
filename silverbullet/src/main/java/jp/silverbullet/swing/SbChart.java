package jp.silverbullet.swing;

import java.awt.Container;
import java.awt.Dimension;
import java.io.IOException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.fasterxml.jackson.databind.ObjectMapper;

import jp.silverbullet.core.property2.ChartContent;
import jp.silverbullet.core.ui.UiProperty;
import jp.silverbullet.core.ui.part2.Pane;

public class SbChart extends SbWidget {


	private ChartPanel panel;
	private XYSeriesCollection dataset;
	private JFreeChart chart;
	
	public SbChart(Pane pane, UiModel uiModel, Container parent) {
		super(pane, uiModel, parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onSize(int width, int height) {
		panel.setPreferredSize(new Dimension(width, height));
	}

	@Override
	protected void onPosition(int left, int top) {
		// TODO Auto-generated method stub
		
	}

	@Override
	void onInit(Pane pane, UiProperty uiProp, Container parent) {
		dataset = new XYSeriesCollection();
		
		chart = ChartFactory.createXYLineChart(
			        "XY Line Chart Example",
			        "X-Axis",
			        "Y-Axis",
			        dataset,
			        PlotOrientation.VERTICAL,
			        true, true, false);
		panel = new ChartPanel(chart);
		parent.add(panel);
	}
	  
	@Override
	protected void onUpdate(UiProperty uiProp) {
		this.dataset.removeAllSeries();
		XYSeries series = new XYSeries("");
		String val = uiProp.getCurrentValue();
//		System.out.println(val);
		val = this.getUiModel().getUiProperty(uiProp.getId(), "501").getCurrentValue();
//		System.out.println(val);
		
		chart.setTitle(uiProp.getTitle());

		try {
			ChartContent chart = new ObjectMapper().readValue(val, ChartContent.class);
			for (int i = 0; i < chart.getY().length; i++) {
				series.add(i, Double.valueOf(chart.getY()[i]));
			}
			this.dataset.addSeries(series);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
