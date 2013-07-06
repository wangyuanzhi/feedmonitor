package com.hotinno.feedmonitor.web.feed;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Grid;

import com.hotinno.feedmonitor.dao.feed.Feed;
import com.hotinno.feedmonitor.dao.feed.FeedDao;

@Component
public class FeedGrid extends Grid implements Composer {

	private static final long serialVersionUID = 8481008130846652429L;

	@Autowired
	private FeedDao feedDao;

	@Override
	public void doAfterCompose(org.zkoss.zk.ui.Component comp) throws Exception {
		List<Feed> feeds = feedDao.getAll();

		((Grid) comp).setModel(new BindingListModelList(feeds, true));
	}
}
