package com.cooksys.assessment.model;

public class Message {
	/**
	 * This class represents a Message, either one sent from one user to another or from the server to a user.
	 * It has a username, a command, and some contents.
	 * There are some constants here, too, to help sort the Messages received by ClientHandler.
	 */
	private String username;
	private String command;
	private String contents;
	
	/*The switch statement in ClientHandler requires constant values*/
	public static final String ECHO = "echo";
	public static final String BROADCAST = "broadcast";
	public static final String CONNECT ="connect";
	public static final String DISCONNECT = "disconnect";
	public static final String USERS = "users";
	public static final String SERVERNAME = "SERVER";
	public static final String[] COMMANDLIST = {ECHO, BROADCAST, CONNECT, DISCONNECT, USERS, SERVERNAME};//for easier error checking
	
	/**
	 * This empty constructor is meant to make this class comply with the JavaBeans convention.
	 */
	public Message()
	{
		this.username = "defaultuser";
		this.command = "defaultcommand";
		this.contents = "defaultcontents";
	}
	
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
	/**
	 * 
	 * @return the username of the user who sent this message
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * 
	 * @param username the name of the user, should not be a command word
	 */
	public void setUsername(String username) {
		/*for(String s : COMMANDLIST)
			if(s.equalsIgnoreCase(username))
				throw new IllegalArgumentException("Invalid username.\nUsername cannot be a command word. " + username);*/
		this.username = username;
	}
	/**
	 * 
	 * @return command of this message (i.e. "broadcast" or "users")
	 */
	public String getCommand() {
		return command;
	}
	/**
	 * 
	 * @param command the command word for this message (i.e. "broadcast" or "users")
	 */
	public void setCommand(String command) {
		this.command = command;
	}
	/**
	 * 
	 * @return the contents of the message (what the user or server actually wants someone to read)
	 */
	public String getContents() {
		return contents;
	}
	/**
	 * 
	 * @param contents  the contents of the message (what the user or server actually wants someone to read)
	 */
	public void setContents(String contents) {
		this.contents = contents;
	}
	@Override
	public String toString() {
		return "Message [username=" + username + ", command=" + command + ", contents=" + contents + "]";
	}

}
