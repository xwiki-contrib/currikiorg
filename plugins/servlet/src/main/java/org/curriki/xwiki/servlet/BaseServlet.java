package org.curriki.xwiki.servlet;


import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Curriki Servlet to process JavaScript (and other) requests
 */
public abstract class BaseServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(BaseServlet.class);
}
