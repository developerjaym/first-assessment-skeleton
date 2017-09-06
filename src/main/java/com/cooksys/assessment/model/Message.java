package com.cooksys.assessment.model;

public class Message {

	private String username;
	private String command;
	private String contents;
	
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

	@Override
	public String toString() {
		return "Message [username=" + username + ", command=" + command + ", contents=" + contents + "]";
	}

}
