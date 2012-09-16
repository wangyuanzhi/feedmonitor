package com.hotinno.feedmonitor.util;

import javax.servlet.http.HttpServletRequest;

public class WebUtil {

	public static String getClientIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		// 判断是否为反向代理,多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值，究竟哪个才是真正的用户端的真实IP呢？
		// 是取X-Forwarded-For中第一个非unknown的有效IP字符串
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
