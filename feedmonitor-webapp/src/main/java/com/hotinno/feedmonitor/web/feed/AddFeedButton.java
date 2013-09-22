package com.hotinno.feedmonitor.web.feed;

import org.springframework.stereotype.Component;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.api.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;

import com.hotinno.feedmonitor.dao.feed.Feed;
import com.hotinno.feedmonitor.dao.feed.FeedDao;

@Component
public class AddFeedButton extends Button implements Composer {

	private static final long serialVersionUID = 5218765905980010510L;

	@Override
	public void doAfterCompose(final org.zkoss.zk.ui.Component comp)
			throws Exception {
		comp.addEventListener(Events.ON_CLICK, new EventListener() {

			@Override
			public void onEvent(Event event) throws Exception {
				for (Object obj : comp.getParent().getChildren()) {
					HtmlBasedComponent child = (HtmlBasedComponent) obj;
					if (child.getId().equals("feedGrid")) {
						BindingListModelList model = (BindingListModelList) ((Grid) child)
								.getListModel();

						Feed feed = new Feed();
						feed.setName("auto add");
						feed.setUrl("http://oabt.org/rss.php?cid=1");
						feed.setKeywords("720 mp4 星球大战");
						FeedDao feedDao = SpringUtil.getApplicationContext()
								.getBean(FeedDao.class);
						feedDao.persist(feed);
						model.add(feed);
						break;
					}
				}
			}
		});
	}
}
