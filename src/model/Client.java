package model;

import java.net.*;
import java.io.*;

import view.ClientGUI;

public class Client  {

	// For I/O
	// Para entrada e saída
	public ObjectInputStream sInput;		
	// To read from the socket
	// Para o socket
	
	public ObjectOutputStream sOutput;		
	// To write on the socket
	// Para escrever no socket
	
	public Socket socket;
	
	private ClientGUI cg;
	
	// The server, the port and the username
	// O servidor, a porta e o usuário
	public String server;
	public String username;
	public int port;

	
	public Client(String server, int port, String username, ClientGUI cg) {
		this.server = server;
		this.port = port;
		this.username = username;
		this.cg = cg;
	}
	


	// To send a message to the console or the GUI	
	// Método para enviar uma mensagem para o console ou para a interface gráfica	 
	public void display(String msg) {
			cg.append(msg + "\n");	
	}
	
	// To send a message to the server
	// Método para enviar uma mensagem para o servidor
	public void sendMessage(ChatMessage msg) {
		try {
			sOutput.writeObject(msg);
		}
		catch(IOException e) {
			display("Exception writing to server: " + e);
		}
	}

	
}

