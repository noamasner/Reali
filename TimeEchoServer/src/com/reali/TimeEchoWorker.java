/**
 * 
 */
package com.reali;

import java.io.IOException;
import java.util.List;

import com.reali.data.TimeMessage;
import com.reali.rep.TimeEchoRepository;
import com.reali.rep.TimeEchoRepositoryConnection;

/**
 * @author noama
 *
 */
public class TimeEchoWorker implements Runnable {

	private TimeMessageQueue queue;
	private TimeEchoRepository repository;
	
	public TimeEchoWorker(TimeMessageQueue queue, TimeEchoRepository repository) {
		this.queue = queue;
		this.repository = repository;
	}

	@Override
	public void run() {
		try(TimeEchoRepositoryConnection repositoryConnection = repository.getConnection()) {
			while (true) {
				TimeMessage timeMessage = queue.getNextMessageInTime();
				printTimeMessage(timeMessage);
				repositoryConnection.remove(timeMessage.getTimestamp());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void printTimeMessage(TimeMessage timeMessage) {
		long now = System.currentTimeMillis();
		StringBuilder sb = new StringBuilder();
		sb.append("Messages for timestamp ").append(timeMessage.getTimestamp()).append(":");
		if (timeMessage.getTimestamp() < now) {
			sb.append(" (printed lately at ").append(now).append(" probably because of system scheduling)");
		}
		System.out.println(sb.toString());
		List<String> messages = timeMessage.getMessages();
		for (String message : messages) {
			System.out.println(message);
		}
		System.out.println();
	}
}
