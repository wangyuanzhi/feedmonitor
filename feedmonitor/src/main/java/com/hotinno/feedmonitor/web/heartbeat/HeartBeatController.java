package com.hotinno.feedmonitor.web.heartbeat;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.hotinno.feedmonitor.dao.heartbeat.HeartBeat;
import com.hotinno.feedmonitor.dao.heartbeat.HeartBeatDao;
import com.hotinno.feedmonitor.task.BtSeedsMonitor;
import com.hotinno.feedmonitor.util.WebUtil;

@Controller
public class HeartBeatController {
	@Autowired
	private HeartBeatDao heartBeatDao;

	@Autowired
	private BtSeedsMonitor btSeedsMonitor;

	@RequestMapping(value = "/heartbeat")
	public ModelAndView guestbook(HttpServletRequest request) {
		if ("POST".equals(request.getMethod())) {
			// Handle a new guest (if any):
			String token = request.getParameter("token");
			if ("3405638492761444561L".equals(token)) {
				String ip = WebUtil.getClientIpAddr(request);
				List<HeartBeat> heartBeats = heartBeatDao.getLastHeartBearts(1);
				if (heartBeats.size() > 0 && StringUtils.equals(ip, heartBeats.get(0).getIp())) {
					heartBeats.get(0).setLastBeat(null);
					heartBeatDao.merge(heartBeats.get(0));
				} else {
					heartBeatDao.persist(new HeartBeat(ip));
				}

				// Notify seeds monitor that transmission is alive.
				btSeedsMonitor.checkSeeds();
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
}
