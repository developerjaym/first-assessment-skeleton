package com.cooksys.assessment.model;

import java.util.Calendar;

public class Message {

	private String username;
	private String command;
	private String contents;
	
	public static final String ECHO = "echo";
	public static final String BROADCAST = "broadcast";
	public static final String CONNECT ="connect";
	public static final String DISCONNECT = "disconnect";
	public static final String USERS = "users";
	
	public Message()
	{}//Jay-made constructor, according to JavaBeans convention
	
	/**
	 * 
	 * @param username the username as a String
	 * @param command the command (think command line interface)
	 * @param contents the message contents (what the other user will actually want to read)
	 */
	public Message(String username, String command, String contents) 
	{
		this.username = username;
		this.command = command;
		this.contents = contents;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}
	
	public static String formatUsername(String un){
		String returnMe = "<" + un + "> ";
		
		return returnMe;
	}
	public static String getTimeStamp()
	{
		Calendar calendar = Calendar.getInstance();
		int month = (calendar.get(Calendar.MONTH))+1;
		StringBuilder sb = new StringBuilder("");
		System.out.println("Month: " + month);
		sb.append(month);
		sb.append("-");
		sb.append(calendar.get(Calendar.DATE));
		sb.append("-");
		sb.append(calendar.get(Calendar.YEAR));
		sb.append(" ");
		sb.append(calendar.get(Calendar.HOUR_OF_DAY));
		sb.append(":");
		sb.append(calendar.get(Calendar.MINUTE));
		sb.append(": ");
		return sb.toString();
	}
	@Override
	public String toString() {
		return "Message [username=" + username + ", command=" + command + ", contents=" + contents + "]";
	}

}
