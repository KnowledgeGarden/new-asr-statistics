/**
 * Copyright 2019, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.statistics;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.topicquests.ks.statistics.api.IStatServerModel;
import org.topicquests.support.ResultPojo;
import org.topicquests.support.api.IResult;
import org.topicquests.util.JSONUtil;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
/**
 * @author jackpark
 *
 */
public class StatServerModel implements IStatServerModel {
	private StatServerEnvironment environment;
	private JSONUtil util;
	private JSONObject data;
	private boolean isDirty = false;
	private boolean isCacheDirty = false;
	private int cacheCounter = 0;
	private final int MAX_CACHE = 50;
	private final String clientId;
	private RedisClient redis;
	private final String
		STATISTICS_PATH,
		REDIS_TOPIC,
		BASE_PATH;

	/**
	 * 
	 */
	public StatServerModel(StatServerEnvironment env) throws Exception {
		environment = env;
		REDIS_TOPIC = environment.getStringProperty("REDIS_TOPIC");
		clientId = environment.getStringProperty("ClientId");
		util = new JSONUtil();
		redis = new RedisClient(environment);
		STATISTICS_PATH = environment.getStringProperty("StatisticsPath");
		BASE_PATH = environment.getStringProperty("BasePath");
		System.out.println("M1 "+STATISTICS_PATH);

		bootStatistics();
		System.out.println("M2");

		bootCache();
		System.out.println("M3");
		//schedule hourly data caching
		//https://stackoverflow.com/questions/32228345/run-java-function-every-hour
		ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
		ses.scheduleAtFixedRate(new Runnable() {
		    @Override
		    public void run() {
		    	//save to an hourly file collection
		        cacheStats();
		        //snapshot the data
		        try { 
		        	saveStatistics();
		        } catch (Exception e) {
		        	environment.logError(e.getMessage(), e);
		        }
		    }
		}, 0, 1, TimeUnit.HOURS);

	}

	void bootStatistics() throws Exception {
		environment.logDebug("BootingStats "+STATISTICS_PATH);
		data = util.load(STATISTICS_PATH);
		isDirty = false;
		isCacheDirty =  false;
	}
	
	void bootCache() throws Exception {
		//reset the counter
		cacheCounter = 0;
		//make a new filepath
		String filePath = BASE_PATH+Long.toString(System.currentTimeMillis())+".json.gz";
		//save it
		util.startCache(filePath);
		//start fresh
		isCacheDirty =  false;
	}
	
	void cacheStatistics() throws Exception {
		if (isCacheDirty) {
			if (++cacheCounter >= MAX_CACHE) {
				//save what's in there
				util.flushCache();
				//restart the cache
				bootCache();
			}
			synchronized(data) {
				//now fill it
				util.cache(data);
			}
		}
	}
	
	void saveStatistics() throws Exception {
		environment.logDebug("SavingStats "+isDirty+" "+data.size());
		if (isDirty) {
			synchronized(data) {
				util.save(STATISTICS_PATH, data);
				redis.add(REDIS_TOPIC, data.toJSONString());
			}
			isDirty = false;
		}
	}
	
	
	/**
	 * <p>This is the core API</p>
	 * <p>When <code>key</code> is sent in, a counter for that key
	 * is incremented</p>
	 * @param key
	 * @param clientId
	 */
	void addToKey(JSONObject  request) {
		String key = request.getAsString(IStatServerModel.FIELD);
		synchronized(data) {
			Long count = (Long)data.get(key);
			if (count == null) 
				count = new Long(0);
			count++;
			data.put(key, count);
			isDirty = true;
			isCacheDirty = true;
			System.out.println("Adding "+isDirty+" "+key);
		}
	}
	
	/**
	 * <p>Fetch the count of a particular field</p>
	 * <p>If field does not exists, returns 0</p>
	 * @param request
	 * @return
	 */
	Long getKey(JSONObject request) {
		String key = request.getAsString(IStatServerModel.FIELD);
		synchronized(data) {
			Long count = (Long)data.get(key);
			if (count == null)
				count = 0L;
			return count;
		}
	}

	String handleUpdate(JSONObject request) {
		System.out.println("HR "+request);
		String result = "OK"; //default
		String json = request.getAsString(IStatServerModel.CARGO).toString();
		JSONParser p = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
		JSONArray a = null;
		try {
			a = (JSONArray)p.parse(json);
			System.out.println("HR-1 "+a);

			result = doUpdate(a);
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
			return IStatServerModel.ERROR+" "+e.getMessage();
		}
		return result;
	}
	
	/**
	 * 
	 * @param stats a collection of key/value JSONObjects
	 * @return
	 */
	String doUpdate(JSONArray stats) {
		String result = "OK"; //default
		synchronized(data) {
			JSONArray st;
			int len = stats.size();
			String key;
			long value;
			for (int i=0;i<len;i++) {
				st = (JSONArray)stats.get(i);
				key = st.get(0).toString();
				value = Long.parseLong((String)st.get(1));
				Long count = (Long)data.get(key);
				if (count == null) 
					count = Long.valueOf(0);
				count += value;
				data.put(key, count);
				isDirty = true;
				isCacheDirty = true;
			}
		}
		return result;
	}
	/**
	 * Returns a {@link JSONObject} filled with the current snapshot
	 * of key-value pairs of all keys.
	 * @return
	 */
	JSONObject getStats() {
		JSONObject result = new JSONObject();
		JSONParser p = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
		synchronized(data) {
			try {
				// clone data
				result = (JSONObject)p.parse(data.toJSONString());
			} catch (Exception e) {
				environment.logError(e.getMessage(), e);
				e.printStackTrace();
			}
		}
		return result;
	}


	@Override
	public IResult handleRequest(JSONObject request) {
		System.out.println("MHR "+request);
		IResult result = new ResultPojo();
		JSONObject jo = new JSONObject(); // default empty
		environment.logDebug("StatServerModel.handleNewRequest "+request);
		String verb = request.getAsString(IStatServerModel.VERB);
		System.out.println("MHR-2 "+verb);

		String clientIx = request.getAsString(IStatServerModel.CLIENT_ID);
		if (clientIx.equals(clientId)) {
			System.out.println("MHR-3 "+verb);
			if (verb.equals(IStatServerModel.GET_STATS))
				jo = getStats();
			else if (verb.equals(IStatServerModel.ADD_TO_KEY)) {
				addToKey(request);
				jo.put(IStatServerModel.CARGO, "ok");
			} else if (verb.equals(IStatServerModel.GET_KEY)) {
				Long v = getKey(request);
				jo.put(IStatServerModel.CARGO, v.toString());
			} else if (verb.equals(IStatServerModel.UPDATE)) {
				String x = handleUpdate(request);
				jo.put(IStatServerModel.CARGO, x);
			} else if (verb.equals(IStatServerModel.TEST))  {
				jo = new JSONObject();
				jo.put(IStatServerModel.CARGO, "Yup");
			} else {
				jo = new JSONObject();
				jo.put(IStatServerModel.CARGO, "BAD VERB: "+verb);
			}
		}  else {
			jo.put(IStatServerModel.ERROR, "Invalid Client");
		}
		result.setResultObject(jo);

		return result;
	}

	
	@Override
	public void shutDown() throws Exception{
		System.out.println("Model shutdown "+isDirty);
		saveStatistics();
		util.flushCache();
	}

	@Override
	public void cacheStats() {
		try {
			cacheStatistics();
		} catch (Exception e) {
			e.printStackTrace();
			environment.logError(e.getMessage(), e);
		}
	}

}
