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
					case Message.CONNECT://"connect":
						log.info("user <{}> connected", message.getUsername());
						clientTracker.onClientConnected(message.getUsername(), writer);
						break;
					case Message.DISCONNECT://"disconnect":
						log.info("user <{}> disconnected", message.getUsername());
						clientTracker.onClientDisconnected(message.getUsername());
						this.socket.close();
						break;
					case Message.BROADCAST://"broadcast":
						log.info("user <{}> broadcasted", message.getUsername());
						clientTracker.broadcast(message);
						break;
					case Message.USERS://"users":
						log.info("user <{}> users", message.getUsername());
						clientTracker.onUsers(writer);
						break;
					case Message.ECHO://"echo":
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
			
			log.info("user <{}> probably disconnected", clientTracker.getProbableUsername());
			clientTracker.onClientDisconnected(clientTracker.getProbableUsername());
			try {
				this.socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println("possible problem closing socket after loser exited");
				e1.printStackTrace();
			}
		}
	}

}
