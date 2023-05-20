/**
 * Copyright 2019, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.*;
import java.util.zip.GZIPOutputStream;


import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
/**
 * @author jackpark
 *
 */
public class JSONUtil {
	//Not thread safe
	private PrintWriter out = null;
	/**
	 * 
	 */
	public JSONUtil() {
	}

	/**
	 * Does not return <code>null</code>
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public JSONObject load(String filePath) throws Exception {
		JSONObject result = null;
		File myFile = new File(filePath);
		if (myFile.exists()) {
			FileInputStream fis = new FileInputStream(myFile);
			BufferedReader rdr = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
			String json = rdr.readLine();
			//System.out.println("StatUtil- "+filePath+" "+(json != null));
			rdr.close();
			JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
			try {
				result = (JSONObject)parser.parse(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else
			result = new JSONObject();
		//System.out.println("StatUtil "+filePath+" "+result);
		return result;
	}
	
	public void save(String filePath, JSONObject jo) throws Exception {
		File myFile = new File(filePath);
		System.out.println("SAVING STATISTICS "+myFile);
		FileOutputStream fos = new FileOutputStream(myFile);
		PrintWriter out = new PrintWriter(fos);
		out.println(jo.toJSONString());
		out.flush();
		out.close();
	}
	
	
	public void startCache(String filePath) throws Exception {
		File myFile = new File(filePath);
		FileOutputStream fos = new FileOutputStream(myFile);
		GZIPOutputStream gos = new GZIPOutputStream(fos);
		out = new PrintWriter(gos);		
	}
	/**
	 * This adds lines of JSON strings to a file
	 * @param jo
	 * @throws Exception
	 */
	public void cache(JSONObject jo) throws Exception {
		out.println(jo.toJSONString());
		// don't close it
	}
	
	/**
	 * Flush the cache
	 * @throws Exception
	 */
	public void flushCache() throws Exception {
		System.out.println("FlushingCache "+out);
		if (out != null) {
			out.flush();
			out.close();
			out = null;
		}
	}
}
