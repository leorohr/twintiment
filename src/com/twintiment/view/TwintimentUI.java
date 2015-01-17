package com.twintiment.view;

import javax.servlet.annotation.WebServlet;

import com.twintiment.view.views.MainViewImpl;
import com.twintiment.view.views.SearchViewImpl;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;

//TODO Application class?

/** 
 * TODO
 * @author leorohr
 *
 */
@SuppressWarnings("serial")
@Theme("twintiment")
public class TwintimentUI extends UI {

	private Navigator navigator;
	
	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = TwintimentUI.class)
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
		getPage().setTitle("Twintiment");
		
		navigator = new Navigator(this, this);
		navigator.addView("", new SearchViewImpl());
		navigator.addView(MainViewImpl.ID, new MainViewImpl());
	
	}

}