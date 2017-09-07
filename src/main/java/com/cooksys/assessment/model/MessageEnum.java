package com.cooksys.assessment.model;

public enum MessageEnum {
	ECHO("(echo): "),  BROADCAST("(all): "),
	DIRECTMESSAGE("(whisper): "), CONNECTIONALERT("has connected"),
	DISCONNECTIONALERT("has disconnected"),
	USERS("currently connected users:\n");

    private final String text;

    /**
     * @param text
     */
    private MessageEnum(final String text) {
        this.text = text;
    }
    @Override
    public String toString() {
        return text;
    }
}
