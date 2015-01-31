package com.twintiment.view.views;

import java.util.Arrays;
import java.util.List;

import com.twintiment.presenter.TwintimentPresenter;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class SearchViewImpl extends CustomComponent implements SearchView, Button.ClickListener {


	private VerticalLayout mainLayout;
	private Button stopButton;
	private Button startButton;
	private TextField keywordTF;
	private Table table;
	private TwintimentPresenter presenter = TwintimentPresenter.getInstance(this);
	
	private static final long serialVersionUID = -6224565670170192401L;
	
	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 */
	public SearchViewImpl() {
		buildMainLayout();
		
		setCompositionRoot(mainLayout);
		
		//Initialise table
		table.addContainerProperty("Message", String.class, "");
		table.addContainerProperty("Score", Double.class, 0.0d);
		table.setColumnWidth("Score", 75);
		table.setColumnExpandRatio("Message", 1.0f);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		
		startButton.addClickListener(this);
		stopButton.addClickListener(this);
		
	}

	@Override
	public void buttonClick(ClickEvent event) {
//		UI.getCurrent().getNavigator().navigateTo(MainView.ID);
		
		if(event.getButton().equals(startButton)) {
			presenter.startStreaming();
		}
		else if(event.getButton().equals(stopButton)) {
			presenter.stopStreaming();
		}
	}

	@Override
	public List<String> getFilterTerms() {

		String[] arr = keywordTF.getValue().split(" ");
		return Arrays.asList(arr);
	}


	@Override
	public void addTableRow(Object[] cells) {
		table.addItem(cells, null);
	}

	private VerticalLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new VerticalLayout();
		mainLayout.setImmediate(false);
		mainLayout.setSizeFull();
		mainLayout.setMargin(false);
		
		// top-level component properties
		setWidth("100%");
		setHeight("100%");
		
		//map
		//API-Key and clientId will be necessary when deploying this app
		GoogleMap map = new GoogleMap(null, null, null);
		map.setSizeFull();
		map.setCenter(new LatLon(51.512093, -0.11703));
        map.setZoom(5);
		mainLayout.addComponent(map);
		
		// table
		table = new Table();
		table.setImmediate(false);
		table.setSizeFull();
		
		//input layout
		startButton = new Button("Start", this);
		stopButton = new Button("Stop", this);
		keywordTF = new TextField();
		VerticalLayout inputLayout = new VerticalLayout(keywordTF,
														new HorizontalLayout(startButton, stopButton));
		inputLayout.setSizeUndefined();
		
		//combine table and input stuff in an hbox
		HorizontalLayout hlayout = new HorizontalLayout(table, inputLayout);	
		hlayout.setSizeFull();
		hlayout.setExpandRatio(table, 0.8f);
		hlayout.setExpandRatio(inputLayout, 0.2f);
		hlayout.setComponentAlignment(inputLayout, Alignment.MIDDLE_CENTER);
		
		mainLayout.addComponent(hlayout);
		mainLayout.setExpandRatio(map, 0.7f);
		mainLayout.setExpandRatio(hlayout, 0.3f);
		
		return mainLayout;
	}

	
}
