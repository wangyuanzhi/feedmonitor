/*
 * Copyright 2002-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hotinno.feedmonitor.batch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Launches a batch job for a given date.
 */
public class Main {
	private static final Log log = LogFactory.getLog(Main.class);

	public static void main(String[] args) throws Throwable {
		try {
			log.error("Entering batch...");

			String env = System.getenv("VCAP_SERVICES");
			log.error("************************************************************************");
			log.error("VCAP_SERVICES is: " + env);
			log.error("************************************************************************");

			AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(
					BuffaloBatchConfiguration.class);

			org.apache.commons.dbcp.BasicDataSource ds = (org.apache.commons.dbcp.BasicDataSource) applicationContext
					.getBean("myDataSource");

			log.error(String.format("URL: %s", ds.getUrl()));
			log.error(String.format("Username: %s", ds.getUsername()));
			log.error(String.format("Password: %s", ds.getPassword()));

			applicationContext.start();

			log.error("Running...");
		} catch (Throwable t) {
			System.err.println(t);
			t.printStackTrace();
			log.error("Error occurred.", t);
		}
	}
}
