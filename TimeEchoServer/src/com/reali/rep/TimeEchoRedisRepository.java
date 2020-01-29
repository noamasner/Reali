/**
 * 
 */
package com.reali.rep;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;

/**
 * @author noama
 *
 */
public class TimeEchoRedisRepository implements TimeEchoRepository {

	private static String KEY_PREFIX = "te-";
	private static String KEY_PATTERN = KEY_PREFIX + "*";
	
	private Jedis jedis;
	
	public TimeEchoRedisRepository(String host, int port) {
		jedis = new Jedis(host, port);
	}
	
	private Jedis getJedis() {
		if (!jedis.isConnected()) {
			jedis.connect();
		}
		return jedis;
	}
	
	@Override
	public void add(long timestamp, String message) {
		Jedis jedis = getJedis();
		jedis.lpush(getKey(timestamp), message);
	}

	@Override
	public void remove(long timestamp) {
		Jedis jedis = getJedis();
		jedis.del(getKey(timestamp));
	}

	@Override
	public Set<String> getAllKeys() {
		Jedis jedis = getJedis();
		Set<String> keys = jedis.keys(KEY_PATTERN);
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
		Jedis jedis = getJedis();
		long len = jedis.llen(key);
		List<String> messages = jedis.lrange(key, 0, len-1);
		return messages;
	}
	
	private String getKey(long timestamp) {
		String key = KEY_PREFIX + timestamp;
		return key;
	}

	@Override
	public void close() throws IOException {
		jedis.close();
	}
}
