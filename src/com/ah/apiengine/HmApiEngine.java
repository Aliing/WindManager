package com.ah.apiengine;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ah.apiengine.parse.PacketParser;
import com.ah.util.Tracer;
import com.ah.util.coder.AhCodePrinter;

public class HmApiEngine extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Tracer	log = new Tracer(HmApiEngine.class.getSimpleName());

	private String remoteAddr;

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		InputStream in = request.getInputStream();
		BufferedInputStream bis = new BufferedInputStream(in);

		try {
			remoteAddr = request.getRemoteAddr();
			log.info("service", "Received a API-Client request from " + remoteAddr + " with session " + request.getRequestedSessionId());
			int contextLen = request.getContentLength();

			// Invalid request message length.
			if (contextLen < 15) {
				log.error("service", "Invalid request message length " + contextLen + ". It should be no less than 15 at least. Discarding this message.");
				return;
			}

			log.info("service", "Received number of " + contextLen + " bytes request from API-Client " + remoteAddr);
			byte[] reqMsg = new byte[contextLen];

			// Read request message error.
			if (bis.read(reqMsg, 0, contextLen) == -1) {
				log.error("service", "[" + remoteAddr + "]Failed to read API-Client request message.");
				return;
			}

			ByteBuffer reqBB = ByteBuffer.allocate(contextLen);
			reqBB.put(reqMsg);
			reqBB.flip();

			// Print request packet for debugging.
			if (log.getLogger().isDebugEnabled()) {
				AhCodePrinter.printHexString(reqBB.duplicate(), log);
			}

			// Handle API-Client request.
			handRequest(request, response, reqBB);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ioe) {
					log.error("service", "IO Close Error", ioe);
				}
			}

			try {
				bis.close();
			} catch (IOException ioe) {
				log.error("service", "IO Close Error", ioe);
			}
		}
	}

	private void handRequest(HttpServletRequest servletRequest, HttpServletResponse servletResponse, ByteBuffer reqBB) {
		log.info("handRequest", "[" + remoteAddr + "]Parsing API-Client request.");
		PacketParsable parser = new PacketParser(servletRequest);

		try {
			// Parse request.
			log.info("handRequest", "Parsing request.");
			Request request = parser.parseRequest(reqBB);

			// Execute request.
			log.info("handRequest", "Executing " + request.getMsgName());
			ByteBuffer respBB = request.execute();

			if (respBB.position() != 0) {
				respBB.flip();
			}

			// Print response packet for debugging.
			if (log.getLogger().isDebugEnabled()) {
				AhCodePrinter.printHexString(respBB.duplicate(), log);
			}		

			byte[] respMsg = new byte[respBB.limit()];
			respBB.get(respMsg);

			// Send response to API-Client.
			ServletOutputStream sos = servletResponse.getOutputStream();
			log.info("handRequest", "Sending number of " + respMsg.length + " bytes response to API-Client " + remoteAddr);
			sos.write(respMsg);
			sos.flush();
			sos.close();

			// Callback is required to some special operations, e.g. DNS Update.
			log.info("callback start...");
			request.callback();
			log.info("callback end");
		} catch (Exception e) {
			log.error("handRequest", "[" + remoteAddr + "]Handling API-Client request failure.", e);
		}
	}

}