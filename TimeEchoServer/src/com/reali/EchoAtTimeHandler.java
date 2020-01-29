/**
 * 
 */
package com.reali;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import com.reali.rep.TimeEchoRepository;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * @author noama
 *
 */
public class EchoAtTimeHandler implements HttpHandler {
	
	private TimeMessageQueue queue;
	private TimeEchoRepository repository;
	
	public EchoAtTimeHandler(TimeMessageQueue queue, TimeEchoRepository repository) {
		this.queue = queue;
		this.repository = repository;
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String requestMethod = exchange.getRequestMethod();
		if (!requestMethod.equals("GET")) {
			sendError(exchange, "Method has to be GET");
			return;
		}
		
		URI requestURI = exchange.getRequestURI();
		String rawQuery = requestURI.getRawQuery();
		if (rawQuery == null) {
			sendError(exchange, "Query is null");
			return;
		}
		
		String message = null;
		long ts = -1;
		
		String[] params = rawQuery.split("[&]");
		if (params.length < 2) {
			sendError(exchange, "Query has to include 2 parameters");
			return;
		}
			
		for (String param : params) {
			String[] split = param.split("[=]");
			if (split.length == 2) {
				String key = split[0];
				if (key.equals("message")) {
					message = split[1];
				} else if (key.equals("tssec") && ts == -1) {
					ts = parseTimeStamp(split[1]) * 1000;
					if (ts == -1) {
						sendError(exchange, "Second parameter has to be a valid timestamp");
						return;
					}
				} else if (key.equals("tsms") && ts == -1) {
					ts = parseTimeStamp(split[1]);
					if (ts == -1) {
						sendError(exchange, "Second parameter has to be a valid timestamp");
						return;
					}
				}
			}
		}
		
		if (message == null) {
			sendError(exchange, "Message param is missing");
			return;
		}
		
		if (ts == -1) {
			sendError(exchange, "timestamp param is missing");
			return;
		}
		
		repository.add(ts, message);
		queue.addTimeMessage(ts, message);
		sendOK(exchange, "OK");
		return;
	}

	private void sendError(HttpExchange exchange, String message) throws IOException {
		sendResponse(exchange, 400, message);
	}
	
	private void sendOK(HttpExchange exchange, String message) throws IOException {
		sendResponse(exchange, 200, message);
	}
	
	private void sendResponse(HttpExchange exchange, int code, String message) throws IOException {
		exchange.sendResponseHeaders(code, message.length());
		OutputStream os = exchange.getResponseBody();
		os.write(message.getBytes());
		os.close();
	}
	
	private long parseTimeStamp(String value) {
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException e) {
			return -1;
		}
	}
}
