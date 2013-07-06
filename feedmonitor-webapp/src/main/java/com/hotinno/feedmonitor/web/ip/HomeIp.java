package com.hotinno.feedmonitor.web.ip;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.hotinno.feedmonitor.dao.heartbeat.HeartBeat;
import com.hotinno.feedmonitor.dao.heartbeat.HeartBeatDao;

/**
 * Servlet implementation class IP
 */
public class HomeIp extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Autowired
	private HeartBeatDao heartBeatDao;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public HomeIp() {
		super();
	}

	@Override
	public void init() throws ServletException {
		super.init();

		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		List<HeartBeat> heartBeats = heartBeatDao.getLastHeartBearts(1);
		String homeIp;
		if (heartBeats.size() > 0) {
			homeIp = heartBeats.get(0).getIp();
		} else {
			homeIp = "0.0.0.0";
		}
		response.getOutputStream().print(homeIp);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
