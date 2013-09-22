package com.hotinno.feedmonitor.batch.feed;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
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

@Component("feedChecker")
public class FeedChecker implements ItemWriter<String> {
	private final Log log = LogFactory.getLog(getClass().getName());

	@Autowired
	private FeedDao feedDao;

	@Autowired
	private BtSeedDao btSeedDao;

	public void write(List<? extends String> urls) throws Exception {
		log.debug("Total URLs is: " + urls.size());

		for (String url : urls) {
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
				Set<BtSeed> seeds = new HashSet<BtSeed>(entries.size());

				for (Feed feed : feedDao.getAllByUrl(url)) {

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

	private SyndFeed getSyndFeed(String url) throws FeedException, IOException,
			MalformedURLException {
		SyndFeedInput input = new SyndFeedInput();
		SyndFeed syndFeed = input.build(new XmlReader(new URL(url)));
		return syndFeed;
	}
}
