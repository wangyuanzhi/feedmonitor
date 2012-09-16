package com.hotinno.feedmonitor.web.seed;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.hotinno.feedmonitor.dao.btseed.BtSeed;
import com.hotinno.feedmonitor.dao.btseed.BtSeedDao;

@Controller
public class SeedController {
	private static final Pattern PATTERN_MAGNET_BT = Pattern
			.compile("(xt=urn:btih:[a-zA-Z0-9]+)");

	@Autowired
	private BtSeedDao btSeedDao;

	@RequestMapping(value = "/seed")
	public ModelAndView guestbook(HttpServletRequest request) {
		return new ModelAndView("forward:./seed.zul");
		// return handleWithSpringMvc(request);
	}

	public ModelAndView handleWithSpringMvc(HttpServletRequest request) {
		if ("POST".equals(request.getMethod())) {
			String id = request.getParameter("id");
			String action = request.getParameter("action");
			String name = request.getParameter("name");
			String magnetUrl = request.getParameter("url");
			String comment = request.getParameter("comment");

			if (StringUtils.isNotEmpty(id)) {
				// TODO Update existing feed
				Long longId = Long.valueOf(id);
				if (longId != null) {
					if ("delete".equalsIgnoreCase(action)) {
						btSeedDao.deleteById(longId);
					}
					if ("clear".equalsIgnoreCase(action)) {
						btSeedDao.clearById(longId);
					}
				}
			} else {
				// Add new feed
				if (StringUtils.isNotEmpty(magnetUrl)) {

					if (StringUtils.isEmpty(name)) {
						Matcher m = PATTERN_MAGNET_BT.matcher(magnetUrl);

						if (m.find()) {
							name = m.group(1);
						} else {
							name = "Unknown";
						}
					}

					BtSeed seed = new BtSeed(name, magnetUrl);
					seed.setComment(comment);

					btSeedDao.persist(seed);
				}
			}
		}

		List<BtSeed> seeds = btSeedDao.getTop(50);

		return new ModelAndView("seed", "seeds", seeds);
	}
}
