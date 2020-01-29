package com.reali;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;

import com.reali.rep.TimeEchoRedisRepository;
import com.reali.rep.TimeEchoRepository;
import com.reali.rep.TimeEchoRepositoryConnection;
import com.sun.net.httpserver.HttpServer;

public class TimeEchoServer {

	private static int SERVER_PORT = -1;
	private static String REDIS_HOST = null;
	private static int REDIS_PORT = -1;

	public static void main(String[] args) throws IOException {
		parseArgs(args);

		TimeEchoRepository repository = new TimeEchoRedisRepository(REDIS_HOST, REDIS_PORT);
		TimeMessageQueue queue = new TimeMessageQueue();
		load(repository, queue);

		Thread workerThread = new Thread(new TimeEchoWorker(queue, repository));
		workerThread.start();

		HttpServer server = HttpServer.create(new InetSocketAddress(SERVER_PORT), 0);
		System.out.println("server started at " + SERVER_PORT);
		server.createContext("/echoAtTime", new EchoAtTimeHandler(queue, repository));
		server.setExecutor(null);
		server.start();
	}

	public static void load(TimeEchoRepository repository, TimeMessageQueue queue) {
		try (TimeEchoRepositoryConnection connection = repository.getConnection()) {
			long now = System.currentTimeMillis();
			Set<String> keys = connection.getAllKeys();
			for (String key : keys) {
				long ts = connection.getTimestampFor(key);
				if (ts > now) {
					queue.addTimeMessage(ts, connection.getMessagesFor(key));
				} else if (ts > 0 && ts <= now) {
					printMissedMessages(now, ts, connection.getMessagesFor(key));
					connection.remove(ts);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void printMissedMessages(long now, long ts, List<String> messages) {
		StringBuilder sb = new StringBuilder();
		sb.append("Messages for timestamp ").append(ts).append(":");
		sb.append(" (printed lately at ").append(now).append(" because the server was down!)");
		System.out.println(sb.toString());
		for (String message : messages) {
			System.out.println(message);
		}
		System.out.println();
	}

	private static void parseArgs(String[] args) {
		if (args.length != 3) {
			System.out.println("Usage: TimeEchoServer [server_port] [redis_host] [redis_port]");
			System.exit(1);
		}
		SERVER_PORT = parseInt(args[0]);
		REDIS_HOST = args[1];
		REDIS_PORT = parseInt(args[2]);
		if (SERVER_PORT < 0 || REDIS_PORT < 0) {
			System.out.println("Usage: TimeEchoServer [server_port] [redis_host] [redis_port]");
			System.exit(1);
		}
	}

	private static int parseInt(String value) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return -1;
		}
	}
}
