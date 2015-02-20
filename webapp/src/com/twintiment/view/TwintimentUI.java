package com.twintiment.view;

import javax.servlet.annotation.WebServlet;

import com.twintiment.view.views.MainViewImpl;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;

/** 
 * TODO
 * @author leorohr
 *
 */
@Theme("twintiment")
@Push
public class TwintimentUI extends UI {
	
	private static final long serialVersionUID = -2836625114402609738L;
	private Navigator navigator;
	private MainViewImpl mainView;
	
	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = TwintimentUI.class, widgetset = "com.twintiment.view.widgetset.TwintimentWidgetset")
	public static class Servlet extends VaadinServlet {

		private static final long serialVersionUID = 5156911352436665589L;
	}

	@Override
	protected void init(VaadinRequest request) {
		getPage().setTitle("Twintiment");
		navigator = new Navigator(this, this);
		
		mainView = new MainViewImpl();
		navigator.addView("", mainView);
	}

}