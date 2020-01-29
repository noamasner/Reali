/**
 * 
 */
package com.reali.rep;

import java.util.List;
import java.util.Set;

/**
 * @author noama
 *
 */
public interface TimeEchoRepository {

	public void add(long timestamp, String message);
	public void remove(long timestamp);
	public Set<String> getAllKeys();
	public long getTimestampFor(String key);
	public List<String> getMessagesFor(String key);
	
}
