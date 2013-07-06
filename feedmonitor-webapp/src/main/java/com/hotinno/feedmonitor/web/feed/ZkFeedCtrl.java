package com.hotinno.feedmonitor.web.feed;

import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.spring.context.annotation.EventHandler;
import org.zkoss.spring.util.GenericSpringComposer;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Messagebox;

@org.springframework.stereotype.Component("feedCtrl")
public class ZkFeedCtrl extends GenericSpringComposer {

	@Autowired
	private FeedGrid feedGrid;

	@Autowired
	private Button addFeedButton;

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
	}

	@EventHandler("addFeedButton.onClick")
	public void showGreeting(Event evt) throws WrongValueException,
			InterruptedException {
		Messagebox.show("Hello!");
	}

}
