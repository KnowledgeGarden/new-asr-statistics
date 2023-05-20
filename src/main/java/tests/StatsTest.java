/**
 * Copyright 2019, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package tests;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.topicquests.support.ResultPojo;
import org.topicquests.support.api.IResult;

/**
 * @author jackpark
 * Server must be running
 */
public class StatsTest {
	private final String 
		BASE_URL = "http://localhost:7898/",
		ADD	= "%7B+%22verb%22%3A%22addToKey%22%2C%22field%22%3A%22WG3%22%2C%22clientId%22%3A%22changeme%22+%7D",
		GETKEY = "%7B+%22verb%22%3A%22getKey%22%2C%22field%22%3A%22WG3%22%2C%22clientId%22%3A%22changeme%22+%7D",
		GETDICT = "%7B+%22verb%22%3A%22getStats%22%2C%22clientId%22%3A%22changeme%22+%7D";
	/**
	 * 
	 */
	public StatsTest() {
		// add some stats
		String query = BASE_URL+ADD;
		IResult r;
		for (int i=0; i< 150; i++) {
			r = client(query);
			System.out.println("A "+r.getErrorString()+" | "+r.getResultObject());
		}
		// see that key
		query = BASE_URL+GETKEY;
		r = client(query);
		System.out.println("B "+r.getErrorString()+" | "+r.getResultObject());
		// see the dictionary
		query = BASE_URL+GETDICT;
		r = client(query);
		System.out.println("C "+r.getErrorString()+" | "+r.getResultObject());
		System.exit(0);
	}
//A  | {"cargo":"ok"}
//B  | {"cargo":"150"}
//C  | {"WG3":150}	
	
	IResult client(String query) {
		IResult result = new ResultPojo();
		BufferedReader rd = null;
		HttpURLConnection con = null;

		try {
			URL urx = new URL(query);
			con = (HttpURLConnection) urx.openConnection();
			con.setReadTimeout(500000); //29 seconds for 1m words - leave lots of time
			con.setRequestMethod("GET");
			con.setDoInput(true);
			con.setDoOutput(true);
			con.connect();
			rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
			StringBuilder buf = new StringBuilder();

			String line;
			while ((line = rd.readLine()) != null) {
				buf.append(line + '\n');
			}

			result.setResultObject(buf.toString());
		} catch (Exception var18) {
			var18.printStackTrace();
			result.addErrorString(var18.getMessage());
		} finally {
			try {
				if (rd != null) {
					rd.close();
				}

				if (con != null) {
					con.disconnect();
				}
			} catch (Exception var17) {
				var17.printStackTrace();
				result.addErrorString(var17.getMessage());
			}

		}
		return result;
	}

}
