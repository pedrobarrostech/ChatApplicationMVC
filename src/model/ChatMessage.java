package model;

import java.io.*;
// This class defines the different type of messages that will be exchanged between the
// Clients and the Server. 
// When talking from a Java Client to a Java Server a lot easier to pass Java objects, no 
// need to count bytes or to wait for a line feed at the end of the frame

// Essa classes define diferentes tipos de mensagens que poderão ser trocadas entre cliente e servidor 
// Quando um cliente conversa com o servidor é bem mais facil para passar objetos java, 
// Assim não precisamos contar os bytes ou esperar por uma linha no final do frame.


public class ChatMessage implements Serializable {

	protected static final long serialVersionUID = 1112122200L;

	// The different types of message sent by the Client
	// Os diferentes tipos de messagens enviadas pelo cliente
	
	
	// WHOISIN to receive the list of the users connected
	// MESSAGE an ordinary message
	// LOGOUT to disconnect from the Server
	
	// WHOISIN para receber uma lista do usuários conectados
	// MESSAGE para uma mensagem
	// LOGOUT para desconectar
	public static final int WHOISIN = 0;

	static final int MESSAGE = 1;

	public static final int LOGOUT = 2;
	private int type;
	private String message;
	
	// construtor
	// Constructor
	public ChatMessage(int type, String message) {
		this.type = type;
		this.message = message;
	}
	
	// getters
	int getType() {
		return type;
	}
	String getMessage() {
		return message;
	}

	public static int getMESSAGE() {
		return MESSAGE;
	}

	public static int getWhoisin() {
		return WHOISIN;
	}

	public static int getLogout() {
		return LOGOUT;
	}
}

