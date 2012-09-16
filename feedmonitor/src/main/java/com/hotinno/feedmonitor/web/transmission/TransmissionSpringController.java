package com.hotinno.feedmonitor.web.transmission;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TransmissionSpringController {

	@RequestMapping(value = "/transmission")
	public ModelAndView guestbook(HttpServletRequest request) {
		return new ModelAndView("forward:./transmission.zul");
	}
}
