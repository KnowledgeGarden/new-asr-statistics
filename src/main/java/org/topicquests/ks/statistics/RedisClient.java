/*
 * Copyright 2024 TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.statistics;

import redis.clients.jedis.JedisPooled;

/**
 * @author jackpark
 * @see https://github.com/redis/jedis
 * @see https://redis.io/docs/latest/develop/connect/clients/java/jedis/
 * @see https://redis.io/docs/latest/commands/?group=list
 */
public class RedisClient {
	private StatServerEnvironment environment;
	private JedisPooled jedis;
	private final String JEDIS_BASE;
	private final int JEDIS_PORT = 6379;
	/**
	 * 
	 */
	public RedisClient(StatServerEnvironment env) {
		environment = env;
		JEDIS_BASE = environment.getStringProperty("RedisBase");
		jedis = new JedisPooled(JEDIS_BASE, JEDIS_PORT);
	}

	/**
	 * Add fresh {@code cargo} to {@code topic}
	 * @param topic
	 * @param cargo
	 * @return
	 */
	public long add(String topic, String cargo) {
		return jedis.lpush(topic, cargo);
	}
	
	/**
	 * Pop first topic off list
	 * @param topic
	 * @return can return {@code null}
	 */
	public String getNext(String topic) {
		return jedis.lpop(topic);
	}
}
