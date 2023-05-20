/**
 * Copyright 2019, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package tests;

import java.net.URLEncoder;

/**
 * @author jackpark
 *
 */
public class Stringolizer {

	/**
	 * 
	 */
	public Stringolizer() {
		String test = "{ \"verb\":\"test\",\"clientId\":\"changeme\" }";
		String add1 = "{ \"verb\":\"addToKey\",\"field\":\"WG3\",\"clientId\":\"changeme\" }";
		String get1 = "{ \"verb\":\"getKey\",\"field\":\"WG3\",\"clientId\":\"changeme\" }";
		String get2 = "{ \"verb\":\"getStats\",\"clientId\":\"changeme\" }";
		try {
			test = URLEncoder.encode(test, "UTF-8");
			System.out.println(test);
			add1 = URLEncoder.encode(add1, "UTF-8");
			System.out.println(add1);
			get1 = URLEncoder.encode(get1, "UTF-8");
			System.out.println(get1);
			get2 = URLEncoder.encode(get2, "UTF-8");
			System.out.println(get2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

}
