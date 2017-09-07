package com.cooksys.assessment.server;

import java.io.PrintWriter;
import java.util.concurrent.ConcurrentHashMap;

import com.cooksys.assessment.model.Message;
import com.cooksys.assessment.model.MessageEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ClientTracker 
{
	private ConcurrentHashMap<String, PrintWriter> clientList = new ConcurrentHashMap<>();
	private ObjectMapper mapper = new ObjectMapper();
	private String probableUsername = "unknown";
	
	public void /*synch?*/ onClientConnected(String username, PrintWriter writer)
	{
		Message message = new Message(username, Message.CONNECT, Message.formatUsername(username) + MessageEnum.CONNECTIONALERT.toString());
		setProbableUsername(username);
		sendToAll(message);
		clientList.put(username, writer);//maybe check to make sure username is not already in the map?
	}
	public void /*synch?*/ onClientDisconnected(String username)
	{
		clientList.remove(username);
		Message message = new Message(username, Message.DISCONNECT, Message.formatUsername(username) + MessageEnum.DISCONNECTIONALERT.toString());
		sendToAll(message);
	}
	public void /*synch?*/ onUsers(PrintWriter writer)
	{
		StringBuilder contents = new StringBuilder(MessageEnum.USERS.toString());
		clientList.forEach((user, w)->{
			contents.append(Message.formatUsername(user));
			contents.append("\n");
		});

		sendMessage(new Message("Server", Message.USERS, contents.toString()), writer);
	}
	public void /*synch?*/ onDefault(Message message)
	{
		if(clientList.containsKey(message.getCommand()))
		{//user private messaging another
			//check to see if otherGuy isn't empty
			String otherGuy = message.getCommand();
			message.setContents(Message.formatUsername(message.getUsername()) + MessageEnum.DIRECTMESSAGE.toString() + message.getContents());
			sendMessage(message, clientList.get(otherGuy));//command should equal the username of the other guy
		}
		else
		{
			System.out.println("I have no idea what this guy is trying to send: " + message);
			//should I echo an error message?
		}
	}
	public void onEcho(Message message, PrintWriter writer) 
	{
		message.setContents(Message.formatUsername(message.getUsername()) + MessageEnum.ECHO.toString() + message.getContents());
		sendMessage(message, writer);
	}
	public void /*synch?*/ broadcast(Message message)
	{
		message.setContents(Message.formatUsername(message.getUsername()) + MessageEnum.BROADCAST.toString() + message.getContents());
		sendToAll(message);
	}
	private void sendToAll(Message message) 
	{
		clientList.forEach((user, writer)->{
			sendMessage(message, writer);
		});
	}
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
	public String getProbableUsername() {
		return probableUsername;
	}
	public void setProbableUsername(String probableUsername) {
		this.probableUsername = probableUsername;
	}

}
