package control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import model.Server;
import view.ServerGUI;


public class ChatServer implements ActionListener, WindowListener {
	
	private Server server;
	private ServerGUI serverGUI;
	
	public ChatServer() {
		this.serverGUI = new ServerGUI(1500,this);
		this.server = null;
	}

	public void actionPerformed(ActionEvent event) {
				// If running we have to stop
				// Se o servidor ja estava rodando ele é parado
				if(server != null) {
					server.stop();
					server = null;
					serverGUI.tPortNumber.setEditable(true);
					serverGUI.stopStart.setText("Start");
					return;
				}
				
		      	// OK start the server	
				// OK iniciando o servidor
				int port;
				try {
					port = Integer.parseInt(serverGUI.tPortNumber.getText().trim());
				}
				catch(Exception e) {
					serverGUI.appendEvent("Invalid port number");
					return;
				}
				
				// Create a new Server
				// Criando um novo servidor
				server = new Server(port, serverGUI);
				
				// And start it as a thread
				// E iniciando como uma thread
				new ServerRunning().start();
				serverGUI.stopStart.setText("Stop");
				serverGUI.tPortNumber.setEditable(false);
		
		
	}
	
	 // If the user click the X button to close the application
	 // I need to close the connection with the server to free the port
	
	// Se o usuário clicar no botão X para fechar a aplicação
	// Precisamos fechar a conexão como o servidor para liberar a porta	 
	public void windowClosing(WindowEvent e) {
		// if my Server exist
		if(server != null) {
			try {
				server.stop();			
				// Ask the server to close the conection
				// Fechando a conexão do servidor
			}
			catch(Exception eClose) {
			}
			server = null;
		}
		
		// Dispose the frame
		// Destruindo o frame
		serverGUI.dispose();
		System.exit(0);
	}
	
	// I can ignore the other WindowListener method
	// Podemos ignorar os outros métodos WindowListener
	public void windowClosed(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	

	// A thread to run the Server
	// Thread responsável por rodar o servidor
	public class ServerRunning extends Thread {
		public void run() {
			server.start();        
			// Should execute until if fails, the server failed
			// Precisa ser executado se falhar, o server falha
			
			serverGUI.stopStart.setText("Start");
			serverGUI.tPortNumber.setEditable(true);
			serverGUI.appendEvent("Server crashed\n");
			server = null;
		}
	}
	
}
