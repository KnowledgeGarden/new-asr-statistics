/**
 * Copyright 2019, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.statistics.api;

import org.topicquests.support.api.IResult;

import net.minidev.json.JSONObject;

/**
 * @author jackpark
 * Theoretically speaking, this can be polled by, say,
 * a web portal on an hourly basis.
 */
public interface IStatServerModel {
	public static final String
		VERB			= "verb",
		CLIENT_ID		= "clientId",
		GET_STATS		= "getStats",
		GET_KEY			= "getKey",
		ADD_TO_KEY		= "addToKey",
		FIELD			= "field",
		TEST			= "test",
		ERROR			= "error",
		CARGO			= "cargo"; //return object a JSON blob of stats
	
	
	/**
	 * A request takes one form:<br>
	 * {verb:"add", word:<word>, clientId:<clientId>}<br/>
	 * It responds with {resp:"ok", value:<value>, isNewWord:<true/false>}<br/>
	 * @param request
	 * @return
	 */
	IResult handleRequest(JSONObject request);

	/**
	 * When this is called, if the stats database has new
	 * data since the previous call, the database is appended to
	 * a growing <timestamp>.json.gz file
	 */
	void cacheStats();
	
	public void shutDown() throws Exception;

}
