package com.hotinno.dataschema.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.hotinno.dataschema.dao.DataSchemaDao;

@Controller
public class DataSchemaController {
	@Autowired
	private DataSchemaDao schemaDao;

	@RequestMapping(value = "/dataschema")
	public ModelAndView guestbook(HttpServletRequest request) {
		// Handle a new guest (if any):
		String sql = request.getParameter("sql");
		List result = new ArrayList();
		if (sql != null) {
			result = schemaDao.run(sql);
			request.setAttribute("sql", sql);
		}

		List<String> strList = new LinkedList<String>();
		for (Object object : result) {
			if (object.getClass().isArray()) {
				strList.add(Arrays.toString((Object[]) object));
			} else {
				strList.add(object.toString());
			}
		}

		return new ModelAndView("dataschema", "result", strList);
	}

}
