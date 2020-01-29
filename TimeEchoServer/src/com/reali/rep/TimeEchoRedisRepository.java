/**
 * 
 */
package com.reali.rep;

/**
 * @author noama
 *
 */
public class TimeEchoRedisRepository implements TimeEchoRepository {

	private String host;
	private int port;
	
	public TimeEchoRedisRepository(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	@Override
	public TimeEchoRepositoryConnection getConnection() {
		return new TimeEchoRedisRepositoryConnection(host, port);
	}

}
