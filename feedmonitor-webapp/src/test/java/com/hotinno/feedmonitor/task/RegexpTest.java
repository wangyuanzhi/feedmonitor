package com.hotinno.feedmonitor.task;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

public class RegexpTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testRegexp() throws Exception {
		Pattern p = Pattern.compile("(xt=urn:btih:[a-zA-Z0-9]+)");

		String str = "magnet:?xt=urn:btih:85fefc3af002379f77234cf8824b2ab5abe8995a&tr.0=http://tracker.openbittorrent.com/announce&tr.1=udp://tracker.openbittorrent.com:80/announce&tr.2=http://tracker.thepiratebay.org/announce&tr.3=http://tracker.publicbt.com/announce&tr.4=http://tracker.prq.to/announce&tr.5=http://torrent-download.to:5869/announce";

		Matcher m = p.matcher(str);

		if (m.find()) {
			System.out.println(m.group(1));
		}
	}
}
