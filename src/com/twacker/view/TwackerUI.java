package com.twacker.view;

import javax.servlet.annotation.WebServlet;

import com.twacker.view.views.MainViewImpl;
import com.twacker.view.views.SearchViewImpl;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;

//TODO Application class?
//TODO rename project

/** 
 * TODO
 * @author leorohr
 *
 */
@SuppressWarnings("serial")
@Theme("twacker")
public class TwackerUI extends UI {

	private Navigator navigator;
	
	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = TwackerUI.class)
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
		getPage().setTitle("Twacker");
		
		navigator = new Navigator(this, this);
		navigator.addView("", new SearchViewImpl());
		navigator.addView(MainViewImpl.ID, new MainViewImpl());
	
	}

}