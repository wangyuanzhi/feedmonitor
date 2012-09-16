package com.hotinno.feedmonitor.task;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hotinno.feedmonitor.dao.btseed.BtSeed;
import com.hotinno.feedmonitor.dao.btseed.BtSeedDao;
import com.hotinno.feedmonitor.dao.feed.Feed;
import com.hotinno.feedmonitor.dao.feed.FeedDao;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

@Component
public class FeedsMonitor {
	private static Log log = LogFactory.getLog(FeedsMonitor.class);

	@Autowired
	private FeedDao feedDao;

	@Autowired
	private BtSeedDao btSeedDao;

	@Scheduled(cron = "0 */15 * * * ?")
	public void runFeedsMonitor() {
		List<Feed> feeds = feedDao.getAll();

		log.warn(String.format("Checking %s feeds...", feeds.size()));

		checkFeeds(feeds);
	}

	public void checkFeeds(long id) {
		List<Feed> feeds = feedDao.getAllById(id);

		checkFeeds(feeds);
	}

	private void checkFeeds(List<Feed> feeds) {
		Map<String, List<Feed>> feedMap = groupFeeds(feeds);

		for (String url : feedMap.keySet()) {
			try {
				long current = System.currentTimeMillis();

				if (log.isDebugEnabled()) {
					log.debug(String.format("Start checking %s...", url));
				}
				SyndFeed syndFeed = getSyndFeed(url);
				if (log.isDebugEnabled()) {
					log.debug(String.format("End checking %s...", url));
				}

				@SuppressWarnings("unchecked")
				List<SyndEntry> entries = syndFeed.getEntries();
				List<BtSeed> seeds = new ArrayList<BtSeed>(entries.size());

				for (Feed feed : feedMap.get(url)) {

					String[] keywords = feed.getKeywords().split("[ ,]");

					for (SyndEntry entry : entries) {
						if (feed.getLastUpdated() == null
								|| entry.getPublishedDate().after(
										feed.getLastUpdated())) {
							boolean match = true;

							String title = entry.getTitle().toLowerCase();
							for (String keyword : keywords) {
								if (title.indexOf(keyword.toLowerCase()) < 0) {
									match = false;
									break;
								}
							}

							if (match) {
								String magnetUrl = entry.getLink();
								if (!btSeedDao.isMagnetUrlExisted(magnetUrl)) {
									BtSeed seed = new BtSeed(entry.getTitle(),
											magnetUrl);
									seed.setProcessed(false);
									seeds.add(seed);
								}
							}
						}
					}

					feed.setLastUpdated(new Timestamp(current));
					feedDao.merge(feed);
				}

				if (seeds.size() > 0) {
					btSeedDao.persist(seeds);
				}
			} catch (Exception e) {
				log.error(String.format(
						"Error occurred while checking feed: %s", url), e);
			}
		}
	}

	private Map<String, List<Feed>> groupFeeds(List<Feed> feeds) {
		Map<String, List<Feed>> resultMap = new HashMap<String, List<Feed>>();

		for (Feed seed : feeds) {
			List<Feed> seedList = resultMap.get(seed.getUrl());
			if (seedList == null) {
				seedList = new LinkedList<Feed>();
				resultMap.put(seed.getUrl(), seedList);
			}
			seedList.add(seed);
		}

		return resultMap;
	}

	public static SyndFeed getSyndFeed(String url) throws FeedException,
			IOException, MalformedURLException {
		SyndFeedInput input = new SyndFeedInput();
		SyndFeed syndFeed = input.build(new XmlReader(new URL(url)));
		return syndFeed;
	}
}
