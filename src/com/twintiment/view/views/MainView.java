package com.twintiment.view.views;

import java.util.List;

import com.vaadin.navigator.View;

public interface MainView extends View {
	
	public static final String ID = "search";
	
	public List<String> getFilterTerms();
	public void addTableRow(Object[] cells);
	public void addMapMarker(double[] coordinates, String text, double score);
}
