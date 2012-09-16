package com.hotinno.feedmonitor.batch.feed;

import java.util.List;

import org.springframework.batch.item.support.ListItemReader;

//@Component
public class FeedReader extends ListItemReader<String> {

	public FeedReader(List<String> list) {
		super(list);
	}

}
