package com.hotinno.feedmonitor.util;

import javax.servlet.http.HttpServletRequest;

public class WebUtil {

	public static String getClientIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip != null && !"".equals(ip) && !"unknown".equalsIgnoreCase(ip)) {
			String[] tempArray = ip.split(",");
			for (int i = 0; i < tempArray.length; i++) {
				if (!"unknown".equalsIgnoreCase(tempArray[i])) {
					ip = tempArray[i].replaceAll("\\s", "");
					break;
				}
			}
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}

		return ip;
	}

}
