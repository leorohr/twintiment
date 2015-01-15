package com.twacker.view.views;

import java.util.List;

import com.vaadin.navigator.View;
import com.vaadin.ui.TextArea;

public interface SearchView extends View {
	
	public static final String ID = "search";
	
	public List<String> getFilterTerms();
	public TextArea getTextArea();

}
