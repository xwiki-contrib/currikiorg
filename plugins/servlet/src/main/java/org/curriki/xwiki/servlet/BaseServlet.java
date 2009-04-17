package org.curriki.xwiki.servlet;


import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Curriki Servlet to process JavaScript (and other) requests
 */
public abstract class BaseServlet extends HttpServlet {
	private static final Log log = LogFactory.getLog(BaseServlet.class);
}
