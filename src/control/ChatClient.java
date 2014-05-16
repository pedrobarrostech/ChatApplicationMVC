package control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


import model.ChatMessage;
import model.Client;
import view.ClientGUI;

public class ChatClient implements ActionListener {
	
	private Client client;
	private ClientGUI clientGUI;
	
	public ChatClient() {
		this.clientGUI = new ClientGUI("localhost", 1500, this);
		this.client = null;
	}

	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		// If it is the Logout button
		// Se for o botão sair
		if(o == clientGUI.logout) {
			client.sendMessage(new ChatMessage(ChatMessage.getLogout(), ""));
			return;
		}
		
		// If it the who is in button
		// Se for o botão Quem está?
		if(o == clientGUI.whoIsIn) {
			client.sendMessage(new ChatMessage(ChatMessage.getWhoisin(), ""));				
			return;
		}

		// Ok it is coming from the JTextField
		// Se for da JTextTextField
		if(clientGUI.connected) {
			// Just have to send the message
			// Apenas envia a mensagem
			client.sendMessage(new ChatMessage(ChatMessage.getMESSAGE(), clientGUI.tf.getText()));				
			clientGUI.tf.setText("");
			return;
		}
		

		if(o == clientGUI.login) {
			// Ok it is a connection request
			// Conectando
			String username = clientGUI.tf.getText().trim();
			
			// Empty username ignore it
			// Se o usuário for vazio ignore
			if(username.length() == 0)
				return;
			
			// Empty serverAddress ignore it
			// Se o endereço do servidor for vazio ignore
			String server = clientGUI.tfServer.getText().trim();
			if(server.length() == 0)
				return;
			
			// Empty or invalid port numer, ignore it
			// Se o número da por for inválido ignore
			String portNumber = clientGUI.tfPort.getText().trim();
			if(portNumber.length() == 0)
				return;
			int port = 0;
			try {
				port = Integer.parseInt(portNumber);
			}
			catch(Exception en) {
				return;   
				// Nothing I can do if port number is not valid
				// Nada para fazer se número da por estiver incorreto
			}

			// Try creating a new Client with GUI
			// Tentando criar uma interface gráfica
			client = new Client(server, port, username, clientGUI);
			
			// Test if we can start the Client
			// Testando se podemos iniciar o cliente
			if(!start()) 
				return;
			clientGUI.tf.setText("");
			clientGUI.label.setText("Enter your message below");
			clientGUI.connected = true;
			
			
			// Disable login button
			// Desabilitando o botão de login
			clientGUI.login.setEnabled(false);
			
			// enable the 2 buttons
			// Habilitando dois botões
			clientGUI.logout.setEnabled(true);
			clientGUI.whoIsIn.setEnabled(true);
			
			// Disable the Server and Port JTextField
			// Desabilitando o JTextFied do servidor e da porta
			clientGUI.tfServer.setEditable(false);
			clientGUI.tfPort.setEditable(false);
			
			// Action listener for when the user enter a message
			// ActionListener para quando o usuário entrar com uma mensagem
			clientGUI.tf.addActionListener(this);
		}

	}
	
	public void connectionFailed() {
		clientGUI.login.setEnabled(true);
		clientGUI.logout.setEnabled(false);
		clientGUI.whoIsIn.setEnabled(false);
		clientGUI.label.setText("Enter your username below");
		clientGUI.tf.setText("Anonymous");
		
		// Reset port number and host name as a construction time
		// Resetando o número da porta e do host como foi construído
		clientGUI.tfPort.setText("" + clientGUI.defaultPort);
		clientGUI.tfServer.setText(clientGUI.defaultHost);
		// Let the user change them
		// Deixando o usuário poder alterar
		clientGUI.tfServer.setEditable(false);
		clientGUI.tfPort.setEditable(false);
		clientGUI.tf.removeActionListener(this);
		clientGUI.connected = false;
	}
	
	// To start the dialog
	// Para iniciar o diálogo
	public boolean start() {
		
		// Try to connect to the server
		// Tentando conectar com o servidor
		try {
			client.socket = new Socket(client.server, client.port);
		} 
		
		// If it failed not much I can so
		// Se a conexão falhar exibe a excessão 
		catch(Exception ec) {
			client.display("Error connectiong to server:" + ec);
			return false;
		}
		
		String msg = "Connection accepted " + client.socket.getInetAddress() + ":" + client.socket.getPort();
		client.display(msg);
	
		// Creating both Data Stream 	
		// Criando um fluxo de dados
		try
		{
			client.sInput  = new ObjectInputStream(client.socket.getInputStream());
			client.sOutput = new ObjectOutputStream(client.socket.getOutputStream());
		}
		catch (IOException eIO) {
			client.display("Exception creating new Input/output Streams: " + eIO);
			return false;
		}

		// Creates the Thread to listen from the server 	
		// Criando um thread que irá ficar ouvido o servidor
		new ListenFromServer().start();
		// Send our username to the server this is the only message that we
		// will send as a String. All other messages will be ChatMessage objects
		
		// Enviando nosso usuário para o servidor, esse será a unica mensagem enviada
		// como String. As demais mensagem serão enviadas como objetos ChatMessage
		try{
			client.sOutput.writeObject(client.username);
		}
		catch (IOException eIO) {
			client.display("Exception doing login : " + eIO);
			disconnect();
			return false;
		}
		
		// Success we inform the caller that it worked
		// Sucesso, nós informamos ao cliente que a conexão foi estabelecida com sucesso.
		return true;
	}
	
	private void disconnect() {
		try { 
			if(client.sInput != null) client.sInput.close();
		}
		catch(Exception e) {} // not much else I can do 
		try {
			if(client.sOutput != null) client.sOutput.close();
		}
		catch(Exception e) {} // not much else I can do
        try{
			if(client.socket != null) client.socket.close();
		}
		catch(Exception e) {} // not much else I can do
		
		// Inform the GUI
        // Informando a interface gráfica
		if(this != null)
			this.connectionFailed();
			
	}
		
		public class ListenFromServer extends Thread {

			public void run() {
				while(true) {
					try {
						String msg = (String) client.sInput.readObject();
						clientGUI.append(msg);
						
					}
					catch(IOException e) {
						client.display("Server has close the connection: " + e);
						if(clientGUI != null) 
							ChatClient.this.connectionFailed();
						break;
					}
					// Can't happen with a String object but need the catch anyhow
					// Não acontece com um objeto do tipo String mas precisamos tratar de qualquer maneira
					catch(ClassNotFoundException e2) {
					}
				}
			}
		}

	
}

