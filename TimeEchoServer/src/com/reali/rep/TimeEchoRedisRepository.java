/**
 * 
 */
package com.reali.rep;

import java.util.List;
import java.util.Set;

import com.reali.TimeMessageQueue;

import redis.clients.jedis.Jedis;

/**
 * @author noama
 *
 */
public class TimeEchoRedisRepository implements TimeEchoRepository {

	private static String KEY_PREFIX = "te-";
	private static String KEY_PATTERN = KEY_PREFIX + "*";
	
	private String host;
	private int port;
	
	public TimeEchoRedisRepository(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	@Override
	public void add(long timestamp, String message) {
		Jedis jedis = new Jedis(host, port);
		jedis.lpush(getKey(timestamp), message);
		jedis.close();
	}

	@Override
	public void remove(long timestamp) {
		Jedis jedis = new Jedis(host, port);
		jedis.del(getKey(timestamp));
		jedis.close();
	}

	private String getKey(long timestamp) {
		String key = KEY_PREFIX + timestamp;
		return key;
	}

	@Override
	public Set<String> getAllKeys() {
		Jedis jedis = new Jedis(host, port);
		Set<String> keys = jedis.keys(KEY_PATTERN);
		jedis.close();
		return keys;
	}

	@Override
	public long getTimestampFor(String key) {
		String tsStr = key.substring(KEY_PREFIX.length());
		try {
			return Long.parseLong(tsStr);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	@Override
	public List<String> getMessagesFor(String key) {
		Jedis jedis = new Jedis(host, port);
		long len = jedis.llen(key);
		List<String> messages = jedis.lrange(key, 0, len-1);
		jedis.close();
		return messages;
	}
}
