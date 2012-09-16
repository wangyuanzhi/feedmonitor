package com.hotinno.feedmonitor.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hotinno.feedmonitor.dao.heartbeat.HeartBeatDao;


@Component
public class HeartBeatMonitor {
	@Autowired
	private HeartBeatDao heartBeatDao;

	@Scheduled(cron = "0 0 0 * * ?")
	void cleanHeartBeat() {
//		Calendar calendar = Calendar.getInstance();
//		calendar.add(Calendar.MONTH, -1);
//
//		heartBeatDao.deleteBefore(new Timestamp(calendar.getTimeInMillis()));
	}
}
