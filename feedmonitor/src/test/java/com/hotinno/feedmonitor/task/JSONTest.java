package com.hotinno.feedmonitor.task;

import org.json.JSONObject;
import org.json.JSONString;

import junit.framework.TestCase;

public class JSONTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testJson() throws Exception {
		JSONObject o = new JSONObject("{'result':'success','arguments':{'torrent-added':{'id':18,'name':'55c09c00f4920c9ac607498d238f0ad62e2582be','hashString':'55c09c00f4920c9ac607498d238f0ad62e2582be'}},'tag':0}");
		System.out.println(o.get("result"));
	}
}
