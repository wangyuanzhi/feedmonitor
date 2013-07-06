package com.hotinno.feedmonitor.web.context;

import java.util.TimeZone;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class BuffaloContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		// Set default timezone
		TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// Do nothing here
	}

}
