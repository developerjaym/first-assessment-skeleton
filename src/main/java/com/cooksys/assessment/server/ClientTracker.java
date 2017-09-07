package com.cooksys.assessment.server;

import java.io.PrintWriter;
import java.util.concurrent.ConcurrentHashMap;

import com.cooksys.assessment.model.Message;
import com.cooksys.assessment.model.MessageEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ClientTracker 
{
	/**
	 * This class conveniently stores a map of all the server's current clients.
	 *  It also handles all the commands received by ClientHandler.
	 */
	private ConcurrentHashMap<String, PrintWriter> clientList = new ConcurrentHashMap<>();//this stores all the current clients <username, writer to that user's socket>
	private ObjectMapper mapper = new ObjectMapper();//this is used to convert between Java Objects (namely, Message) and JSON
	
	/**
	 * This method is called when the ClientHandler reads a 'connect' command.
	 * It builds a Message (a connection alert) to send to everyone, 
	 * it sends that connection alert to all, and finally, it adds this new user to the client list.
	 * 
	 * @param username the username of this new client
	 * @param writer the PrintWriter that writes to this client's socket
	 */
	public void onClientConnected(String username, PrintWriter writer)
	{
		Message message = new Message(username, Message.CONNECT, Message.formatUsername(username) + MessageEnum.CONNECTIONALERT.toString());
		sendToAll(message);
		clientList.put(username, writer);
	}
	/**
	 * This method is called when the ClientHandler reads a 'disconnect' command.
	 * It removes the user from the client list, builds a Message (a disconnection alert) to send to everyone,
	 * and finally, it sends that disconnection alert to all.
	 * 
	 * @param username the username of the client
	 */
	public void onClientDisconnected(String username)
	{
		clientList.remove(username);
		Message message = new Message(username, Message.DISCONNECT, Message.formatUsername(username) + MessageEnum.DISCONNECTIONALERT.toString());
		sendToAll(message);
	}
	/**
	 * This method is called when the ClientHandler reads a 'users' command.
	 * It goes through each client in the client list and adds their name to a String.
	 * This String then becomes the contents of a message that the server sends to the user.
	 * 
	 * @param writer the PrintWriter belonging to this client.
	 */
	public void onUsers(PrintWriter writer)
	{
		StringBuilder contents = new StringBuilder(MessageEnum.USERS.toString());
		clientList.forEach((user, w)->{
			contents.append(Message.formatUsername(user));
			contents.append("\n");
		});
		sendMessage(new Message("Server", Message.USERS, contents.toString()), writer);
	}
	/**
	 * This method is called when the user wants to direct message someone (or when the user sends an invalid command).
	 * If there is a user by the name given in the Message's command, then that user is sent a message.
	 * Else, the user is sent an 'invalid command' message from the server and we should alert the user.
	 * 
	 * @param message the Message to send directly to another user. This Message's command will be a username.
	 */
	public void onDefault(Message message)
	{
		if(clientList.containsKey(message.getCommand()))
		{//If this command corresponds to a user on the client list
			String otherGuy = message.getCommand();
			message.setContents(Message.formatUsername(message.getUsername()) + MessageEnum.DIRECTMESSAGE.toString() + message.getContents());
			sendMessage(message, clientList.get(otherGuy));
		}
		else
		{//The command is invalid, send an error message back
			message.setContents(message.toString() + MessageEnum.INVALIDCOMMAND.toString());
			sendMessage(message, clientList.get(message.getUsername()));
		}
	}
	/**
	 * This method is called when the ClientHandler receives an 'echo' command.
	 * The method formats the message just sent and sends it right back to the user.
	 * @param message the Message received by the ClientHandler
	 * @param writer the PrintWriter belonging to the user
	 */
	public void onEcho(Message message, PrintWriter writer) 
	{
		message.setContents(Message.formatUsername(message.getUsername()) + MessageEnum.ECHO.toString() + message.getContents());
		sendMessage(message, writer);
	}
	/**
	 * This method is called when the ClientHandler receives a 'broadcast' command.
	 * It formats the Message and sends it to all.
	 * @param message the Message received by the ClientHandler
	 */
	public void broadcast(Message message)
	{
		message.setContents(Message.formatUsername(message.getUsername()) + MessageEnum.BROADCAST.toString() + message.getContents());
		sendToAll(message);
	}
	/**
	 * This method sends a Message to all currently connected users.
	 * It loops through the client list and writes the Message to each PrintWriter.
	 * @param message the Message to be sent to all.
	 */
	private void sendToAll(Message message) 
	{
		clientList.forEach((user, writer)->{
			sendMessage(message, writer);
		});
	}
	/**
	 * This is the method that actually writes to a PrintWriter.
	 * It conveniently timestamps each Message, too.
	 * 
	 * @param message the Message to be sent
	 * @param writer the PrintWriter belonging to whichever user this Message is meant for
	 */
	public void sendMessage(Message message, PrintWriter writer)
	{
		message.setContents(Message.getTimeStamp() + message.getContents());
		try {
			String response = mapper.writeValueAsString(message);
			writer.write(response);
			writer.flush();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}	
	}
}
