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
import com.cooksys.assessment.model.MessageEnum;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ClientHandler implements Runnable {
	private Logger log = LoggerFactory.getLogger(ClientHandler.class);
	private Socket socket;
	private ClientTracker clientTracker;
	private String clientname = "unknown";
	/**
	 * 
	 * @param socket the Socket for this particular client
	 * @param clientTracker the ClientTracker used by the Server to keep track of all clients
	 */
	public ClientHandler(Socket socket, ClientTracker clientTracker) {
		super();
		this.socket = socket;
		this.clientTracker = clientTracker;
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
						setClientName(message.getUsername());
						clientTracker.onClientConnected(getClientName(), writer);
						greet(message);
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
			log.info("user <{}> probably disconnected", getClientName());
			clientTracker.onClientDisconnected(getClientName());
			try {
				this.socket.close();
			} catch (IOException e1) {
				System.out.println("possible problem closing socket after user exited");
				e1.printStackTrace();
			}
		}
	}

	private void greet(Message message) {
		if(!message.getUsername().equals(getClientName()))//let user know their name has changed
			clientTracker.onDefault(new Message(Message.SERVERNAME, getClientName(), clientname + MessageEnum.NAMECHANGE.toString()  + "\n" + MessageEnum.APOLOGY.toString()));
		else
			clientTracker.onDefault(new Message(Message.SERVERNAME, getClientName(), MessageEnum.WELCOME.toString() + clientname));
	}
	/**
	 * This method is useful because the ClientTracker sometimes needs to know which user it belongs to.
	 * When the user disconnects with 'exit' rather than 'disconnect' we need the username to send a proper
	 * disconnection alert to all users.
	 * 
	 * @return the username associated with this ClientHandler
	 */
	public String getClientName() {
		return clientname;
	}
	/**
	 * This method is useful because the ClientHandler sometimes needs to know which user it belongs to.
	 * When the user disconnects with 'exit' rather than 'disconnect' we need the username to send a proper
	 * disconnection alert to all users.
	 * This method also prevents two users from having the same name.
	 * 
	 * @param clientname the username associated with this ClientHandler
	 */
	public void setClientName(String clientname) {
		if(clientTracker.hasAlready(clientname))
			setClientName(MessageEnum.NAMEDIFFERENTIATOR+clientname);
		else
			this.clientname = clientname;
	}
}
