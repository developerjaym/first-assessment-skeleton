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
	
	private String probableUsername = "unknown";//this is used when the user enters 'exit' without first disconnecting
	
	
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
					case Message.CONNECT:
						log.info("user <{}> connected", message.getUsername());
						setProbableUsername(message.getUsername());
						clientTracker.onClientConnected(message.getUsername(), writer);
						break;
					case Message.DISCONNECT:
						log.info("user <{}> disconnected", message.getUsername());
						clientTracker.onClientDisconnected(message.getUsername());
						this.socket.close();
						break;
					case Message.BROADCAST:
						log.info("user <{}> broadcasted", message.getUsername());
						clientTracker.broadcast(message);
						break;
					case Message.USERS:
						log.info("user <{}> users", message.getUsername());
						clientTracker.onUsers(writer);
						break;
					case Message.ECHO:
						log.info("user <{}> echoed message <{}>", message.getUsername(), message.getContents());
						clientTracker.onEcho(message, writer);
						break;
					default:
						log.info("user <{}> default", message.getUsername());
						clientTracker.onDefault(message);
						break;
				}
			}

		} catch (IOException e) {
			log.error("Something went wrong :/", e);
			
			/*This IOException typically occurs when a user disconnects after typing 'exit' rather than 'disconnect'
			 * Such users should be removed from the client list and a disconnection alert should be sent to all*/
			log.info("user <{}> probably disconnected", getProbableUsername());
			clientTracker.onClientDisconnected(getProbableUsername());
			try {
				this.socket.close();
			} catch (IOException e1) {
				System.out.println("possible problem closing socket after loser exited");
				e1.printStackTrace();
			}
		}
	}
	/**
	 * This method is useful because the ClientTracker sometimes needs to know which user it belongs to.
	 * When the user disconnects with 'exit' rather than 'disconnect' we need the username to send a proper
	 * disconnection alert to all users.
	 * 
	 * @return the username associated with this ClientTracker
	 */
	public String getProbableUsername() {
		return probableUsername;
	}
	/**
	 * This method is useful because the ClientTracker sometimes needs to know which user it belongs to.
	 * When the user disconnects with 'exit' rather than 'disconnect' we need the username to send a proper
	 * disconnection alert to all users.
	 * 
	 * @param probableUsername the username associated with this ClientTracker
	 */
	public void setProbableUsername(String probableUsername) {
		this.probableUsername = probableUsername;
	}
}
