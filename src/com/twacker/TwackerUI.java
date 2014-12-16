package com.twacker;

import javax.servlet.annotation.WebServlet;

import com.twacker.views.SearchView;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
@Theme("twacker")
public class TwackerUI extends UI {

	Navigator navigator;
	protected static final String MAINVIEW = "main";
	
	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = TwackerUI.class)
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
		getPage().setTitle("Twacker");
		
		navigator = new Navigator(this, this);
		navigator.addView("", new SearchView());
//		navigator.addView(MAINVIEW, new MainView());
	
	}

}