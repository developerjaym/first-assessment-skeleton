package com.cooksys.assessment.server;

import java.io.PrintWriter;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ConcurrentHashMap;

import com.cooksys.assessment.model.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ClientTracker 
{
	private ConcurrentHashMap<String, PrintWriter> clientList = new ConcurrentHashMap<>();
	private ObjectMapper mapper = new ObjectMapper();
	
	public void /*synch?*/ onClientConnected(String username, PrintWriter writer)
	{
		System.out.println("on client connected " + username);
		
		Message message = new Message(username, "connect", "<" + username + "> "+"has connected.");
		clientList.forEach((user, w)->{
			sendMessage(message, w);
		});
		clientList.put(username, writer);//maybe check to make sure username is not already in the map?
	}
	public void /*synch?*/ onClientDisconnected(String username)
	{
		System.out.println("on client disconnected: " + username);
		
		clientList.remove(username);
		Message message = new Message(username, "disconnect", "<" + username + "> " +"has disconnected.");
		clientList.forEach((user, writer)->{
			sendMessage(message, writer);
		});
	}
	public void /*synch?*/ onUsers(PrintWriter writer)
	{
		System.out.println("on users command");
		
		StringBuilder contents = new StringBuilder("currently connected users:\n");
		clientList.forEach((user, w)->{
			contents.append("<" + user + ">" + "\n");
		});
		
		sendMessage(new Message("Server", "users", contents.toString()), writer);
	}
	public void /*synch?*/ onDefault(Message message)
	{
		System.out.println("On default: " + message.getCommand());
		
		if(clientList.containsKey(message.getCommand()))
		{//user private messaging another
			//check to see if otherGuy isn't empty
			String otherGuy = message.getCommand();
			message.setContents("<" + message.getUsername() + "> " + "(whisper): " + message.getContents());
			sendMessage(message, clientList.get(otherGuy));//command should equal the username of the other guy
		}
		else
		{
			System.out.println("I have no idea what this guy is trying to send: " + message);
		}
	}
	public void onEcho(Message message, PrintWriter writer) 
	{
		message.setContents("<" + message.getUsername() + "> " + "(echo): " + message.getContents());
		sendMessage(message, writer);
	}
	public void /*synch?*/ broadcast(Message message)
	{
		System.out.println("broadcasting " + message);
		message.setContents("<" + message.getUsername() + "> " + "(all): " + message.getContents());
		//for each client in the map, write a message and FLUSH
		clientList.forEach((user, writer)->{
			sendMessage(message, writer);
		});
	}
	public void sendMessage(Message message, PrintWriter writer)
	{
		String response;
		Date date = new Date();
		message.setContents(date.toString() + ": " + message.getContents());
			//we need a timestamp
		try {
			response = mapper.writeValueAsString(message);
			writer.write(response);
			writer.flush();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}	
	}
	
}
