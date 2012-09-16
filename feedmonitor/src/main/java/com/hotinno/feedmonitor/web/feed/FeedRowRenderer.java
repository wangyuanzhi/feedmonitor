package com.hotinno.feedmonitor.web.feed;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Component;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.api.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Textbox;

import com.hotinno.feedmonitor.dao.feed.Feed;
import com.hotinno.feedmonitor.dao.feed.FeedDao;
import com.hotinno.feedmonitor.task.FeedsMonitor;

@Component
public class FeedRowRenderer implements RowRenderer {

	public static final String LABEL_DELETE = "Delete";
	public static final String LABEL_CLEAR = "Clear Last Update";
	public static final String LABEL_CHECK = "Check Now!";

	@Override
	public void render(Row row, Object data) throws Exception {
		Feed feed = (Feed) data;

		renderWithData(row, feed);
	}

	public void renderWithData(Row row, Feed feed) {
		Long id = feed.getId();
		if (id == null) {
			id = 0L;
		}

		row.setId("feed_" + id);

		Textbox rssUrl = new Textbox(feed.getUrl());
		rssUrl.setId("url_" + id);
		rssUrl.setInplace(true);
		rssUrl.setWidth("99%");
		rssUrl.addEventListener(Events.ON_CHANGE,
				new FeedOnChangeEventListener(rssUrl, "url", id));
		rssUrl.addEventListener(Events.ON_FOCUS, new FeedOnChangeEventListener(
				rssUrl, "url", id));

		Textbox keywords = new Textbox(feed.getKeywords());
		keywords.setId("keywords_" + id);
		keywords.setInplace(true);
		keywords.setWidth("99%");
		keywords.addEventListener(Events.ON_CHANGE,
				new FeedOnChangeEventListener(keywords, "keywords", id));
		keywords.addEventListener(Events.ON_FOCUS,
				new FeedOnChangeEventListener(keywords, "keywords", id));

		Datebox lastUpdate = new Datebox(feed.getLastUpdated());
		lastUpdate.setId("lastUpdated_" + id);
		lastUpdate.setTimeZone("GMT+8");
		lastUpdate.setFormat("yyyy.MM.dd HH:mm:ss");
		lastUpdate.setReadonly(true);
		lastUpdate.setButtonVisible(false);
		lastUpdate.setWidth("99%");

		Textbox comment = new Textbox(feed.getComment());
		comment.setId("comment_" + id);
		comment.setInplace(true);
		comment.setWidth("99%");
		comment.addEventListener(Events.ON_CHANGE,
				new FeedOnChangeEventListener(comment, "comment", id));
		comment.addEventListener(Events.ON_FOCUS,
				new FeedOnChangeEventListener(comment, "comment", id));

		Button deleteBtn = new Button();
		deleteBtn.setLabel(LABEL_DELETE);
		deleteBtn.addEventListener(Events.ON_CLICK,
				new DeleteFeedEventListener(deleteBtn, id));

		Button clearBtn = new Button();
		clearBtn.setLabel(LABEL_CLEAR);
		clearBtn.addEventListener(Events.ON_CLICK,
				new ClearLastUpdateFeedEventListener(clearBtn, id));

		Button checkBtn = new Button();
		checkBtn.setLabel(LABEL_CHECK);
		checkBtn.addEventListener(Events.ON_CLICK, new CheckFeedEventListener(
				checkBtn, id));

		Div btnDiv = new Div();
		btnDiv.appendChild(deleteBtn);
		btnDiv.appendChild(clearBtn);
		btnDiv.appendChild(checkBtn);

		btnDiv.setAlign("left");

		row.appendChild(rssUrl);
		row.appendChild(keywords);
		row.appendChild(lastUpdate);
		row.appendChild(comment);
		row.appendChild(btnDiv);
	}
}

abstract class FeedEventListener implements EventListener {
	public static Feed getTargetFeed(HtmlBasedComponent component, long id) {
		Grid grid = (Grid) component.getFellow("feedGrid", true);
		BindingListModelList model = (BindingListModelList) grid.getListModel();

		Feed feed = null;

		for (int i = 0; i < model.getSize(); i++) {
			Feed f = (Feed) model.getElementAt(i);
			Long feedId = f.getId();
			if ((feedId == null && id == 0) || feedId == id) {
				feed = f;
				break;
			}
		}
		return feed;
	}

	public static BindingListModelList getGridModel(HtmlBasedComponent component) {
		Grid grid = (Grid) component.getFellow("feedGrid", true);
		BindingListModelList model = (BindingListModelList) grid.getListModel();

		return model;
	}
}

class DeleteFeedEventListener extends FeedEventListener {

	private final Button button;
	private final long id;

	public DeleteFeedEventListener(Button button, long id) {
		this.button = button;
		this.id = id;
	}

	@Override
	public void onEvent(Event event) throws Exception {
		if (Events.ON_CLICK.equals(event.getName())) {
			if (event.getTarget() instanceof Button) {
				Button b = (Button) event.getTarget();

				if (b == button) {
					final Feed feed = getTargetFeed(button, id);

					Messagebox
							.show(String
									.format("Are you sure to delete Feed which\n\nurl = %s,\n\nkeywords = %s?",
											feed.getUrl(), feed.getKeywords()),
									"Delete Feed", Messagebox.YES
											| Messagebox.NO,
									Messagebox.QUESTION, Messagebox.NO,
									new org.zkoss.zk.ui.event.EventListener() {
										public void onEvent(Event e) {
											if (Messagebox.ON_YES.equals(e
													.getName())) {
												FeedDao feedDao = SpringUtil
														.getApplicationContext()
														.getBean(FeedDao.class);

												feedDao.deleteById(id);

												getGridModel(button).remove(
														feed);
											} else if (Messagebox.ON_CANCEL
													.equals(e.getName())) {
												// Cancel is clicked
											}
										}
									});
				}
			}
		}

	}
}

class ClearLastUpdateFeedEventListener extends FeedEventListener {

	private final Button button;
	private final long id;

	public ClearLastUpdateFeedEventListener(Button button, long id) {
		this.button = button;
		this.id = id;
	}

	@Override
	public void onEvent(Event event) throws Exception {
		if (Events.ON_CLICK.equals(event.getName())) {
			if (event.getTarget() instanceof Button) {
				Button b = (Button) event.getTarget();

				if (b == button) {
					final Feed feed = getTargetFeed(button, id);
					feed.setLastUpdated(null);

					FeedDao feedDao = SpringUtil.getApplicationContext()
							.getBean(FeedDao.class);
					feedDao.merge(feed);

					Row row = (Row) button.getFellow("feed_" + id);

					for (Object obj : row.getChildren()) {
						HtmlBasedComponent child = (HtmlBasedComponent) obj;
						if (child.getId().equals("lastUpdated_" + id)) {
							((Datebox) child).setValue(null);
							break;
						}
					}
				}
			}
		}
	}
}

class CheckFeedEventListener extends FeedEventListener {

	private final Button button;
	private final long id;

	public CheckFeedEventListener(Button button, long id) {
		this.button = button;
		this.id = id;
	}

	@Override
	public void onEvent(Event event) throws Exception {
		if (Events.ON_CLICK.equals(event.getName())) {
			if (event.getTarget() instanceof Button) {
				Button b = (Button) event.getTarget();

				if (b == button) {
					final Feed feed = getTargetFeed(button, id);
					feed.setLastUpdated(null);

					FeedsMonitor feedMonitor = SpringUtil
							.getApplicationContext()
							.getBean(FeedsMonitor.class);

					feedMonitor.checkFeeds(id);

					FeedDao feedDao = SpringUtil.getApplicationContext()
							.getBean(FeedDao.class);

					Feed f = feedDao.getById(id);

					Row row = (Row) button.getFellow("feed_" + id);

					for (Object obj : row.getChildren()) {
						HtmlBasedComponent child = (HtmlBasedComponent) obj;
						if (child.getId().equals("lastUpdated_" + id)) {
							((Datebox) child).setValue(f.getLastUpdated());
							break;
						}
					}
				}
			}
		}
	}
}

class FeedOnChangeEventListener extends FeedEventListener {
	private final Textbox component;
	private final String attributeName;
	private final long id;

	public FeedOnChangeEventListener(Textbox textbox, String attributeName,
			long id) {
		this.component = textbox;
		this.attributeName = attributeName;
		this.id = id;
	}

	@Override
	public void onEvent(Event event) throws Exception {
		if (Events.ON_CHANGE.equals(event.getName())) {
			Feed feed = getTargetFeed(component, id);

			String value = BeanUtils.getProperty(feed, attributeName);

			if (value == null || !value.equals(component.getValue())) {
				BeanUtils
						.setProperty(feed, attributeName, component.getValue());

				FeedDao feedDao = SpringUtil.getApplicationContext().getBean(
						FeedDao.class);

				if (feed.getId() == null) {
					feed.setName(feed.getKeywords());
					feedDao.persist(feed);
				} else {
					feedDao.merge(feed);
				}

				component.setStyle("font-weight:bold");
			}
		} else if (Events.ON_FOCUS.equals(event.getName())) {
			component.setStyle("");
		}
	}

}