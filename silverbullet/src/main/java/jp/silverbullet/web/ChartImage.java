package jp.silverbullet.web;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import jp.silverbullet.core.property2.ChartProperty;

public class ChartImage {

	public ByteArrayOutputStream get(ChartProperty chartProp) {
		XYSeriesCollection dataset = new XYSeriesCollection();
		JFreeChart chart = ChartFactory.createXYLineChart(
		        "",
		        "X-Axis",
		        "Y-Axis",
		        dataset,
		        PlotOrientation.VERTICAL,
		        true, true, false);
		
		XYSeries series = new XYSeries("");
		try {
			for (int i = 0; i < chartProp.getY().length; i++) {
				series.add(i, Double.valueOf(chartProp.getY()[i]));
			}
			dataset.addSeries(series);
			
			ByteArrayOutputStream ret = new ByteArrayOutputStream();
			BufferedOutputStream out = new BufferedOutputStream(ret);
			ChartUtils.writeChartAsJPEG(out, chart, 400, 300);
			out.flush();
			return ret;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
