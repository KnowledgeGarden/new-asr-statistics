/**
 * Copyright 2019, TopicQuests Foundation
 *  This source code is available under the terms of the Affero General Public License v3.
 *  Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
 */
package org.topicquests.ks.statistics;

import java.util.*;

import org.topicquests.ks.statistics.api.IStatServerModel;
import org.topicquests.support.RootEnvironment;

/**
 * @author jackpark
 *
 */
public class StatServerEnvironment extends RootEnvironment {
	private IStatServerModel model;
	private StopperListener stopper;
	boolean isStopped = false;
	
	/**
	 * Default constructor
	 */
	public StatServerEnvironment() {
		super("config-props.xml", "logger.properties");
		Boot();
	}
	
	/**
	 * Custom constructor
	 * @param configPath
	 * @param logPath
	 */
	public StatServerEnvironment(String configPath, String logPath) {
		super(configPath, logPath);
		Boot();
	}
	
	void Boot() {
		stopper = new StopperListener(this);
		System.out.println("A");
		try {
			model = new StatServerModel(this);
			System.out.println("B");
		} catch (Exception e) {
			logError(e.getMessage(), e);
			throw new RuntimeException(e);
		}
		System.out.println("C");
		isStopped = false;
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			
			@Override
			public void run() {
				shutDown();
			}
		});

	}

	IStatServerModel getModel() {
		return model;
	}

	
	public void shutDown() {
		System.out.println("StatServer shutting down "+isStopped);
		if (!isStopped)  {
			isStopped = true;
			try {
				model.shutDown();
			} catch (Exception e) {
				logError(e.getMessage(), e);
				e.printStackTrace();
			}
		}
		
	}
}
