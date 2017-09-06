package com.cooksys.assessment.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cooksys.assessment.model.Message;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ClientHandler implements Runnable {
	private Logger log = LoggerFactory.getLogger(ClientHandler.class);

	private Socket socket;

	private ClientTracker clientTracker;
	
	public ClientHandler(Socket socket, ClientTracker ct) {
		super();
		this.socket = socket;
		clientTracker = ct;
	}

	public void run() {
		try {

			ObjectMapper mapper = new ObjectMapper();
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

			while (!socket.isClosed()) {
				String raw = reader.readLine();
				Message message = mapper.readValue(raw, Message.class);

				switch (message.getCommand()) {
					case "connect":
						log.info("user <{}> connected", message.getUsername());
						clientTracker.onClientConnected(message.getUsername(), writer);
						break;
					case "disconnect":
						log.info("user <{}> disconnected", message.getUsername());
						clientTracker.onClientDisconnected(message.getUsername());
						this.socket.close();
						break;
					case "broadcast":
						log.info("user <{}> broadcasted", message.getUsername());
						clientTracker.broadcast(message);
						break;
					case "users":
						log.info("user <{}> users", message.getUsername());
						clientTracker.onUsers(writer);
						break;
					case "echo":
						log.info("user <{}> echoed message <{}>", message.getUsername(), message.getContents());
						clientTracker.onEcho(message, writer);
						break;
					default:
						log.info("user <{}> default", message.getUsername());//GET THIS WORKING, PROBLEM IS LIKELY CLIENT SIDE
						clientTracker.onDefault(message);
						break;
				}
			}

		} catch (IOException e) {
			log.error("Something went wrong :/", e);
		}
	}

}
