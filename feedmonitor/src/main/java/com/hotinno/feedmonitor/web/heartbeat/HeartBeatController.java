package com.hotinno.feedmonitor.web.heartbeat;

import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.ModelAndView;

import com.hotinno.feedmonitor.dao.feed.FeedDao;
import com.hotinno.feedmonitor.dao.heartbeat.HeartBeat;
import com.hotinno.feedmonitor.dao.heartbeat.HeartBeatDao;
import com.hotinno.feedmonitor.task.BtSeedsMonitor;
import com.hotinno.feedmonitor.util.WebUtil;

@Controller
public class HeartBeatController {
	private static Log log = LogFactory.getLog(HeartBeatController.class);

	@Autowired
	private HeartBeatDao heartBeatDao;

	@Autowired
	private FeedDao feedDao;

	@Autowired
	private BtSeedsMonitor btSeedsMonitor;

	@RequestMapping(value = "/heartbeat")
	public ModelAndView hearbeat(final HttpServletRequest request) {
		if ("POST".equals(request.getMethod())) {
			// Handle a new guest (if any):
			String token = request.getParameter("token");
			if ("3405638492761444561L".equals(token)) {
				String ip = WebUtil.getClientIpAddr(request);
				List<HeartBeat> heartBeats = heartBeatDao.getLastHeartBearts(1);
				if (heartBeats.size() > 0
						&& StringUtils.equals(ip, heartBeats.get(0).getIp())) {
					heartBeats.get(0).setLastBeat(null);
					heartBeatDao.merge(heartBeats.get(0));
				} else {
					heartBeatDao.persist(new HeartBeat(ip));
				}

				// Notify seeds monitor that transmission is alive.
				new Thread(new Runnable() {
					@Override
					public void run() {
						log.info("Checking seeds...");
						btSeedsMonitor.checkSeeds();
					}
				}).start();

				// Kick-off a thread to check feed monitor
				new Thread(new Runnable() {
					@Override
					public void run() {
						checkFeedMonitor(request);
					}
				}).start();
			}
		}

		List<HeartBeat> heatbeats = heartBeatDao.getLastHeartBearts(10);

		if (heatbeats.size() > 0) {
			request.setAttribute("latestIp", heatbeats.get(0).getIp());
			request.setAttribute("total", heatbeats.get(0).getId());
		}

		request.setAttribute("lastUpdate", new Date());

		// Prepare the result view (heartbeat.jsp):
		return new ModelAndView("heartbeat", "heatbeats", heatbeats);
	}

	public void checkFeedMonitor(HttpServletRequest request) {
		Date lastFetchTime = feedDao.getLastFetchTime();

		if (System.currentTimeMillis() - lastFetchTime.getTime() > 20 * 60 * 1000) {
			log.warn("It is more than 20 minutes that did not check feeds.");

			ApplicationContext applicationContext = WebApplicationContextUtils
					.getWebApplicationContext(request.getSession()
							.getServletContext());

			log.warn("Class of appContext is: "
					+ applicationContext.getClass().getName());

			ApplicationContext parentContext = applicationContext.getParent();

			if (parentContext != null
					&& parentContext instanceof ConfigurableApplicationContext) {
				log.warn("Refresh appContext...");
				((ConfigurableApplicationContext) parentContext).refresh();
			}

			log.warn("Done.");
		}
	}

	private void refreshServlet() throws ServletException {
		// TODO
	}
}
