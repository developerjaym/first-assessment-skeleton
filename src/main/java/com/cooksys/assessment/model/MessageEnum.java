package com.cooksys.assessment.model;

public enum MessageEnum {
	/**
	 * This class keeps (almost) all my Message Strings in one convenient location.
	 * These Strings are used when formatting a Message's contents.
	 */
	ECHO("(echo): "),  BROADCAST("(all): "),
	DIRECTMESSAGE("(whisper): "), CONNECTIONALERT("has connected"),
	DISCONNECTIONALERT("has disconnected"),
	USERS("currently connected users:\n"),
	INVALIDCOMMAND(" has an invalid command");

    private final String text;

    /**
     * @param text this String will be used when building a message to display to the user
     */
    private MessageEnum(final String text) {
        this.text = text;
    }
    @Override
    public String toString() {
        return text;
    }
}
