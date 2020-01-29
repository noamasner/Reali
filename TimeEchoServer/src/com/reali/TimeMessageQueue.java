/**
 * 
 */
package com.reali;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import com.reali.data.TimeMessage;

/**
 * @author noama
 *
 */
public class TimeMessageQueue {

	private static long DELTA = 10;
	
	private PriorityQueue<TimeMessage> queue;
	private Map<Long, TimeMessage> map;

	public TimeMessageQueue() {
		queue = new PriorityQueue<TimeMessage>();
		map = new HashMap<Long, TimeMessage>();
	}

	public synchronized void addTimeMessage(long timestamp, String message) {
		TimeMessage timeMessage = map.get(timestamp);
		if (timeMessage != null) {
			timeMessage.addMessage(message);
		} else {
			timeMessage = new TimeMessage(timestamp, message);
			queue.add(timeMessage);
			map.put(timestamp, timeMessage);
			notifyAll();
		}
	}
	
	public synchronized void addTimeMessage(long timestamp, List<String> messages) {
		TimeMessage timeMessage = map.get(timestamp);
		if (timeMessage != null) {
			timeMessage.addMessages(messages);
		} else {
			timeMessage = new TimeMessage(timestamp, messages);
			queue.add(timeMessage);
			map.put(timestamp, timeMessage);
			notifyAll();
		}
	}

	public synchronized TimeMessage getNextMessageInTime() {
		try {
			while (true) {
				if (queue.isEmpty()) {
					wait();
				} else {
					TimeMessage top = queue.peek();
					long timestamp = top.getTimestamp();
					long now = System.currentTimeMillis();
					long timeLeft = timestamp - now;
					if (timeLeft <= 0) {
						return queue.poll();
					} else if (timeLeft <= DELTA){
						continue;
					} else {
						wait(timeLeft - DELTA);
					}
				}
			}
		} catch (InterruptedException e) {
			return null;
		}
	}
}
