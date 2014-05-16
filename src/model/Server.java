package model;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

import view.ServerGUI;


public class Server {
	// A unique ID for each connection
	// Um único índice para cada conexão
	private static int uniqueId;
	
	// An ArrayList to keep the list of the Client
	// Um ArrayList para os clientes conectados
	private ArrayList<ClientThread> al;

	private ServerGUI sg;
	
	// To display time
	// Para mostrar a data 
	private SimpleDateFormat sdf;
	
	// The port number to listen for connection
	// O número para porta para conexão 
	private int port;
	
	// The boolean that will be turned of to stop the server
	// O boleando que será transformador para parar o servidor
	private boolean keepGoing;
	

	
	public Server(int port, ServerGUI sg) {
		this.sg = sg;
		this.port = port;
		sdf = new SimpleDateFormat("HH:mm:ss");
		al = new ArrayList<ClientThread>();
	}
	
	public Server(int port) {
		this(port, null);
	}

	public void start() {
		keepGoing = true;
		// Create socket server and wait for connection requests 
		// Criando um SocketServer e esperando por requisições de conexão
		try 
		{
			// The socket used by the server
			// Socket usado pelo servidor
			ServerSocket serverSocket = new ServerSocket(port);

			// Infinite loop to wait for connections
			// Loop infinito para esperar as conexões
			while(keepGoing) 
			{
				// Format message saying we are waiting
				// Mensagem informando que o servidor está esperando por clientes
				display("Server waiting for Clients on port " + port + ".");
				
				Socket socket = serverSocket.accept();  	
				// Accept connection
				// Aceitando a conexão
				
				// If I was asked to stop
				// Se for requisitado para parar
				if(!keepGoing)
					break;
				ClientThread t = new ClientThread(socket);  
				// Make a thread of it
				// Criando uma thread para isso
				
				al.add(t);									
				// Save it in the ArrayList
				// Salvando no ArrayList
				t.start();
			}
			// I was asked to stop
			// Tentando parar o servidor
			try {
				serverSocket.close();
				for(int i = 0; i < al.size(); ++i) {
					ClientThread tc = al.get(i);
					try {
					tc.sInput.close();
					tc.sOutput.close();
					tc.socket.close();
					}
					catch(IOException ioE) {
						// Not much I can do
						// Nada para tratar
					}
				}
			}
			catch(Exception e) {
				display("Exception closing the server and clients: " + e);
			}
		}
		// Something went bad
		// Alguma coisa ocorreu errado
		catch (IOException e) {
            String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
			display(msg);
		}
	}		

	public void stop() {
		keepGoing = false;
		// Connect to myself as Client to exit statement 
		// Socket socket = serverSocket.accept();
		// Conectando em mim mesmo para confirmação da saída
		// Socket socket = serverSocket.accept();
		try {
			new Socket("localhost", port);
		}
		catch(Exception e) {
			// Nothing I can really do
			// Nada para tratar
		}
	}
	
	// Display an event (not a message) to the GUI
	// Método para exibir um evento(Não uma mensagem) na interface gráfica
	private void display(String msg) {
		String time = sdf.format(new Date()) + " " + msg;
		if(sg == null)
			System.out.println(time);
		else
			sg.appendEvent(time + "\n");
	}
	
	// To broadcast a message to all Clients
	// Para difundir a mensagem para todos o clientes
	private synchronized void broadcast(String message) {
		
		// Add HH:mm:ss and \n to the message
		// Adicionamento HH:mm:ss à mensagem
		String time = sdf.format(new Date());
		String messageLf = time + " " + message + "\n";

		if(sg == null)
			System.out.print(messageLf);
		else
			sg.appendRoom(messageLf);    
			// Append in the room window
			// Anexão na sala de bate-papo
		
		// We loop in reverse order in case we would have to remove a Client
		// Because it has disconnected
		// Usamos o loop de ordem reversa no caso de remoção de algum cliente
		// Por que ele foi desconectado
		for(int i = al.size(); --i >= 0;) {
			ClientThread ct = al.get(i);
			
			// Try to write to the Client if it fails remove it from the list
			// Tentando escrever para o cliente, se falhar removemos da lista
			if(!ct.writeMsg(messageLf)) {
				al.remove(i);
				display("Disconnected Client " + ct.username + " removed from list.");
			}
		}
	}

	// For a client who logoff using the LOGOUT message
	// Para o cliente que saiu usando a mensagem de LOGOUT
	synchronized void remove(int id) {
		// Scan the array list until we found the Id
		// Scaneando o ArrayList até encontrar o índice
		for(int i = 0; i < al.size(); ++i) {
			ClientThread ct = al.get(i);
			// Found it
			// Procurando por 
			if(ct.id == id) {
				al.remove(i);
				return;
			}
		}
	}
	
	

	// One instance of this thread will run for each client 
	// Uma instância dessa thread que irá rodar em cada cliente
	public class ClientThread extends Thread {
		// The socket where to listen/talk
		// O socket para ouvir/falar
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
		
		// My unique id (easier for deconnection)
		// Índice único (fácil para desconectar)
		int id;
		
		// The Username of the Client
		// O usuário do cliente
		String username;
		
		// The only type of message a will receive
		// O único tipo de messagem que recebemos
		ChatMessage cm;
		
		// The date I connect
		// A data de conexão
		String date;

		// Constructore
		// Construtor
		ClientThread(Socket socket) {
			// A unique id
			// Índice único
			id = ++uniqueId;
			this.socket = socket;
			// Creating both Data Stream 
			// Criando um fluxo de dados
			System.out.println("Thread trying to create Object Input/Output Streams");
			try
			{
				// Create output first
				// Criando a saída primeiro
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput  = new ObjectInputStream(socket.getInputStream());
				// Read the username
				// Lendo o usuário
				username = (String) sInput.readObject();
				display(username + " just connected.");
			}
			catch (IOException e) {
				display("Exception creating new Input/output Streams: " + e);
				return;
			}
			// Have to catch ClassNotFoundException
			// Precisamos tratar a excessão ClassNotFoundException 
			catch (ClassNotFoundException e) {
			}
            date = new Date().toString() + "\n";
		}

		// What will run forever
		// Que vai rodar eternamente
		public void run() {
			// To loop until LOGOUT
			// Loop até deslogar
			boolean keepGoing = true;
			while(keepGoing) {
				// Read a String (which is an object)
				// Lendo uma String (Como um objeto)
				try {
					cm = (ChatMessage) sInput.readObject();
				}
				catch (IOException e) {
					display(username + " Exception reading Streams: " + e);
					break;				
				}
				catch(ClassNotFoundException e2) {
					break;
				}
				// The messaage part of the ChatMessage
				// Parte da mensagem de ChatMessage
				String message = cm.getMessage();

				// Switch on the type of message receive
				// Escolhendo entre o tipo de mensagens a ser recebida
				switch(cm.getType()) {

				case ChatMessage.MESSAGE:
					broadcast(username + ": " + message);
					break;
				case ChatMessage.LOGOUT:
					display(username + " disconnected with a LOGOUT message.");
					keepGoing = false;
					break;
				case ChatMessage.WHOISIN:
					writeMsg("List of the users connected at " + sdf.format(new Date()) + "\n");
					// Scan al the users connected
					// Scaneando todas a mensagem conectadas
					for(int i = 0; i < al.size(); ++i) {
						ClientThread ct = al.get(i);
						writeMsg((i+1) + ") " + ct.username + " since " + ct.date);
					}
					break;
				}
			}
			// Remove myself from the arrayList containing the list of the connected Clients
			// Remove-me do ArrayList que contem a lista de usuário conectados
			remove(id);
			close();
		}
		
		// Try to close everything
		// Tentando fechar tudo
		private void close() {
			// Try to close the connection
			// Tentando fechar a conexão
			try {
				if(sOutput != null) sOutput.close();
			}
			catch(Exception e) {}
			try {
				if(sInput != null) sInput.close();
			}
			catch(Exception e) {};
			try {
				if(socket != null) socket.close();
			}
			catch (Exception e) {}
		}

		// Write a String to the Client output stream
		// Escrevendo uma String para Client OutPut Stream 
		private boolean writeMsg(String msg) {
			
			// If Client is still connected send the message to it
			// Se o cliente ainda está conectado envie uma mensagem
			if(!socket.isConnected()) {
				close();
				return false;
			}
			// Write the message to the stream
			// Escrevendo a mensage para a stream
			try {
				sOutput.writeObject(msg);
			}
			// If an error occurs, do not abort just inform the user
			// Se ocorrer erros, não aborte apenas informe o usuário
			catch(IOException e) {
				display("Error sending message to " + username);
				display(e.toString());
			}
			return true;
		}
	}
}


