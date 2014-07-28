package com.ah.ha.hc;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ah.be.communication.event.portal.BeHAStatusInfoEvent;
import com.ah.bo.mgmt.QueryUtil;

public class HealthCheckServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		PrintWriter writer = res.getWriter();

		if ("c".equals(req.getParameter("t"))) {
			// deal with CAPWAP status
			writer.print(BeHAStatusInfoEvent.OK);
		} else if ("t".equals(req.getParameter("t"))) {
			// deal with tomcat status
			writer.print(BeHAStatusInfoEvent.OK);
		} else if ("d".equals(req.getParameter("t"))) {
			// deal with db connection status
			try {
				QueryUtil.executeNativeQuery("select current_date");

				writer.print(BeHAStatusInfoEvent.OK);
			} catch (Exception e) {
				writer.print(BeHAStatusInfoEvent.FAILED);
			}
		}
	}

}