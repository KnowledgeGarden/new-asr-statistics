/**
 * Copyright 2019, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package tests;

import org.topicquests.ks.statistics.StatServerEnvironment;

/**
 * @author jackpark
 *
 */
public class BootTest {
	private StatServerEnvironment environment;
	
	/**
	 * 
	 */
	public BootTest() {
		environment = new StatServerEnvironment();
		System.out.println("A "+environment.getProperties());
		environment.shutDown();
		System.exit(0);
	}

}
