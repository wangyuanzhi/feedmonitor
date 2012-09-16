/**
 *
 */
package com.hotinno.feedmonitor.task;

import java.io.IOException;
import java.net.MalformedURLException;

import junit.framework.TestCase;

import com.hotinno.feedmonitor.task.FeedsMonitor;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;

/**
 * @author Bob
 *
 */
public class FeedsMonitorTest extends TestCase {

	/*
	 * (non-Javadoc)
	 *
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for
	 * {@link com.hotinno.feedmonitor.task.FeedsMonitor#getSyndFeed(java.lang.String)}
	 * .
	 *
	 * @throws IOException
	 * @throws FeedException
	 * @throws MalformedURLException
	 */
	public void testGetSyndFeed() throws MalformedURLException, FeedException,
			IOException {
		FeedsMonitor monitor = new FeedsMonitor();

//		SyndFeed feed = monitor.getSyndFeed("http://oabt.org/rss.php?cid=1");
//		System.out.println(feed);

		SyndFeed feed1 = monitor.getSyndFeed("http://oabt.org/?cid=1");
		System.out.println(feed1);
	}

}
