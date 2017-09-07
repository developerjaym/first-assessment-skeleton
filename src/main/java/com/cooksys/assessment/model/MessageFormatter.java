package com.cooksys.assessment.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageFormatter {
	
	/**
	 * This class has static methods to format Messages.
	 */
	
	/**
	 * This method makes the username look the way it's supposed to.
	 * 
	 * @param un the username to be formatted
	 * @return the username with angled brackets <> around it and a space after
	 */
	public static String formatUsername(String username){
		String returnMe = "<" + username + "> ";
		return returnMe;
	}
	/**
	 * This method builds a String with the month, date, year, hour, and minute. 
	 * Referenced http://www.tutorialspoint.com/java/java_date_time.htm
	 * 
	 * @return a String representing the current time
	 */
	public static String getTimeStamp()
	{
		Date dNow = new Date();
		SimpleDateFormat ft = new SimpleDateFormat ("yyyy.MM.dd hh:mm a zzz': '");
		return ft.format(dNow);
	}
	/**
	 * For connection alerts
	 * 
	 * @param username the sender of this Message
	 * @return a properly formatted Message
	 */
	public static Message getFormattedConnectionAlert(String username)
	{
		Message message = new Message(username, Message.CONNECT, MessageFormatter.getTimeStamp() +
				MessageFormatter.formatUsername(username) + MessageEnum.CONNECTIONALERT.toString());
		return message;
	}
	/**
	 * For disconnection alerts
	 * 
	 * @param username the sender of this Message
	 * @return a properly formatted Message
	 */
	public static Message getFormattedDisconnectionAlert(String username)
	{
		Message message = new Message(username, Message.DISCONNECT, MessageFormatter.getTimeStamp() +
				MessageFormatter.formatUsername(username) + MessageEnum.DISCONNECTIONALERT.toString());
		return message;
	}
	/**
	 * For 'users' Message
	 * 
	 * @param username the sender of this Message
	 * @return a properly formatted Message
	 */
	public static Message getFormattedUsersMessage(String contents)
	{
		Message message = new Message(Message.SERVERNAME, Message.USERS, contents);
		return message;
	}
	/**
	 * For direct messages
	 * 
	 * @param username the sender of this Message
	 * @param command the command (the name of the recipient)
	 * @param contents the contents of the message
	 * @return a properly formatted Message
	 */
	public static Message getFormattedDirectMessage(String username, String command, String contents)
	{
		Message message = new Message();
		message.setUsername(username);
		message.setCommand(command);
		message.setContents(MessageFormatter.getTimeStamp() +
				MessageFormatter.formatUsername(message.getUsername()) +
				MessageEnum.DIRECTMESSAGE.toString() +
				contents);
		return message;
	}
	/**
	 * For informing users that they sent an invalid command
	 * 
	 * @param username the sender of this Message
	 * @param command the command (the name of the recipient)
	 * @param oldMessage the Message that didn't get sent properly (toString() of that Message)
	 * @return a properly formatted Message
	 */
	public static Message getFormattedDirectMessageErrorMessage(String username, String command, String oldMessage)
	{
		Message message = new Message();
		message.setUsername(username);
		message.setCommand(command);
		message.setContents(MessageFormatter.getTimeStamp() +
				MessageFormatter.formatUsername(Message.SERVERNAME)+
				MessageEnum.ERRORMESSAGE.toString() +
				oldMessage +
				MessageEnum.INVALIDCOMMAND.toString());
		return message;
	}
	/**
	 * For echo Messages
	 * 
	 * @param username the sender of this Message
	 * @param contents the contents of this Message
	 * @return a properly formatted Message
	 */
	public static Message getFormattedEchoMessage(String username, String contents)
	{
		Message message = new Message();
		message.setUsername(username);
		message.setCommand(Message.ECHO);
		message.setContents(MessageFormatter.getTimeStamp() +
				MessageFormatter.formatUsername(message.getUsername()) + MessageEnum.ECHO.toString() + contents);
		return message;
	}
	/**
	 * For broadcast Messages
	 * 
	 * @param username the sender of this Message
	 * @param contents the contents of this Message
	 * @return a properly formatted Message
	 */
	public static Message getFormattedBroadcastMessage(String username, String contents)
	{
		Message message = new Message();
		message.setUsername(username);
		message.setCommand(Message.BROADCAST);
		message.setContents(MessageFormatter.getTimeStamp() +
				MessageFormatter.formatUsername(message.getUsername()) + MessageEnum.BROADCAST.toString() + contents);
		return message;
	}
}
