/**
 * Copyright 2019, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.statistics;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author jackpark
 *
 */
public class StatServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private StatServerEnvironment environment;
	private StatServletHandler handler;
	
	/**
	 * 
	 */
	public StatServlet(StatServerEnvironment env) {
		environment = env;
		handler = new StatServletHandler(environment);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		handler.executePost(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		handler.executeGet(request, response);
	}

	public void destroy() {
		environment.shutDown();
		//handler.shutDown();
	}

}
