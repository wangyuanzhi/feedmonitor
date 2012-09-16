package com.hotinno.feedmonitor.web.feed;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.hotinno.feedmonitor.dao.feed.Feed;
import com.hotinno.feedmonitor.dao.feed.FeedDao;

@Controller
public class FeedController {
	@Autowired
	private FeedDao feedDao;

	@RequestMapping(value = "/feed")
	public ModelAndView guestbook(HttpServletRequest request) {
		return new ModelAndView("forward:./feed.zul");
		// return handleWithSpringMvc(request);
	}

	public ModelAndView handleWithSpringMvc(HttpServletRequest request) {
		if ("POST".equals(request.getMethod())) {
			String id = request.getParameter("id");
			String action = request.getParameter("action");
			String name = request.getParameter("name");
			String url = request.getParameter("url");
			String keywords = request.getParameter("keywords");
			String comment = request.getParameter("comment");

			if (StringUtils.isNotEmpty(id)) {
				Long longId = Long.valueOf(id);
				if (longId != null) {
					if ("delete".equalsIgnoreCase(action)) {
						feedDao.deleteById(longId);
					}
					if ("clear".equalsIgnoreCase(action)) {
						feedDao.clearById(longId);
					}
				}
				// TODO Update existing feed
			} else {
				// Add new feed
				if (StringUtils.isEmpty(name)) {
					name = url;
				}

				if (StringUtils.isNotEmpty(url)
						&& StringUtils.isNotEmpty(keywords)) {
					Feed feed = new Feed(name, url, keywords);
					feed.setComment(comment);

					feedDao.persist(feed);
				}
			}
		}

		List<Feed> feeds = feedDao.getAll();

		return new ModelAndView("feed", "feeds", feeds);
	}
}
