/**
 * Copyright 2019, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.statistics;

import java.net.Socket;
import java.util.Map;

import org.topicquests.support.config.ConfigPullParser;

/**
 * @author jackpark
 *
 */
public class Stopper {
	private static final String serverName = "localhost";
	private static int port;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Stopping Statistics Server");
		ConfigPullParser p = new ConfigPullParser("config-props.xml");
		Map<String, Object> props = p.getProperties();
		String px = (String)props.get("StopperPort");
		port = Integer.parseInt(px);
		try {
			Socket s = new Socket(serverName, port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
