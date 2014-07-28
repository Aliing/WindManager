package com.ericdaugherty.sshwebproxy;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

/**
 * Common Servlet base class.  Implements common functions.
 *
 * @author Eric Daugherty
 */
public class SshBaseServlet extends HttpServlet implements SshConstants {

    //***************************************************************
    // Variables
    //***************************************************************

    /**
	 *
	 */
	private static final long serialVersionUID = 1L;

    //***************************************************************
    // Helper Methods
    //***************************************************************

    /**
     * Returns the SshConnection that is associated with this request, or null
     * if the session can not be found.
     *
     * @param request HttpServletRequest instance.
     * @param sshSession SshSession instance.
     * @return the requested SshSession, or null.
     */
    protected SshConnection getConnection( HttpServletRequest request, SshSession sshSession ) {
        String connectionInfo = request.getParameter( PARAMETER_CONNECTION );
        SshConnection sshConnection = null;

		if ( connectionInfo != null && connectionInfo.trim().length() > 0 ) {
            sshConnection = sshSession.getSshConnection( connectionInfo );
        }

        return sshConnection;
    }

}