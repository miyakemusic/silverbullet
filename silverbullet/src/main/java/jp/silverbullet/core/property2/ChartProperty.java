package jp.silverbullet.core.property2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ChartProperty {
	public enum ChartType {
		XY,
	}
	private ChartType chartType;
	
	private String[] y;
	private String xmin;
	private String xmax;

	private String ymax;

	private String ymin;
	public ChartType getChartType() {
		return chartType;
	}
	public String[] getY() {
		return y;
	}
	public String getXmin() {
		return xmin;
	}
	public String getXmax() {
		return xmax;
	}
	public void setChartType(ChartType chartType) {
		this.chartType = chartType;
	}
	public void setY(String[] y) {
		this.y = y;
	}
	public void setXmin(String xmin) {
		this.xmin = xmin;
	}
	public void setXmax(String xmax) {
		this.xmax = xmax;
	}
	public void setYmax(String ymax) {
		this.ymax = ymax;
	}
	public void setYmin(String ymin) {
		this.ymin = ymin;
	}
	public String getYmax() {
		return ymax;
	}
	public String getYmin() {
		return ymin;
	}
	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
