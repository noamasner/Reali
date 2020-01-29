/**
 * 
 */
package com.reali.data;

import java.util.LinkedList;
import java.util.List;

/**
 * @author noama
 *
 */
public class TimeMessage implements Comparable<TimeMessage> {

	private long timestamp;
	private List<String> messages;
	
	public TimeMessage(long timestamp, String message) {
		this.timestamp = timestamp;
		this.messages = new LinkedList<String>();
		messages.add(message);
	}
	
	public TimeMessage(long timestamp, List<String> messages) {
		this.timestamp = timestamp;
		this.messages = new LinkedList<String>(messages);
	}

	public long getTimestamp() {
		return timestamp;
	}

	public List<String> getMessages() {
		return messages;
	}
	
	public void addMessage(String message) {
		messages.add(message);
	}
	
	public void addMessages(List<String> message) {
		messages.addAll(messages);
	}

	@Override
	public int compareTo(TimeMessage other) {
		return (int) (this.timestamp - other.timestamp);
	}
}
