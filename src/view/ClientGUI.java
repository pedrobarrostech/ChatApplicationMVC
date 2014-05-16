package view;

import javax.swing.*;

import control.ChatClient;

import java.awt.*;
import java.awt.event.*;


// The Client with its GUI

public class ClientGUI extends JFrame  {

	private static final long serialVersionUID = 1L;
	// Will first hold "Username:", later on "Enter message"
	// Primeiro aparece usuário, depois entre com uma mensagem
	public JLabel label;
	
	// To hold the Username and later on the messages
	// Para recuperar o usuário e as mensagens
	public JTextField tf;
	
	// To hold the server address an the port number
	// Para recuperar o endereço do servidor e o número da porta
	public JTextField tfServer;
	public JTextField tfPort;
	
	// To Logout and get the list of the users
	// Botões para deslogar e para recuperar a lista de usuário online
	public JButton login;
	public JButton logout;
	public JButton whoIsIn;
	
	// For the chat room
	// Para a sala de bate-papo
	private JTextArea ta;
	
	// If it is for connection
	// Para a verificar o status da conexão
	public boolean connected;

	// The default port number
	// Número de porta padrão
	public int defaultPort;
	public String defaultHost;

	// Constructor connection receiving a socket number
	// Construtor recebendo um socket
	public ClientGUI(String host, int port, ChatClient chatClient) {

		super("Chat Client");
		defaultPort = port;
		defaultHost = host;
		
		// The NorthPanel with:
		// Painel Superior
		JPanel northPanel = new JPanel(new GridLayout(3,1));
		
		// The server name and the port number
		// O nome do servidor e o número da porta
		JPanel serverAndPort = new JPanel(new GridLayout(1,5, 1, 3));
		
		// The two JTextField with default value for server address and port number
		// Duas JTextField com valores padrões do endereço do servidor e número da porta
		tfServer = new JTextField(host);
		tfPort = new JTextField("" + port);
		tfPort.setHorizontalAlignment(SwingConstants.RIGHT);

		serverAndPort.add(new JLabel("Server Address:  "));
		serverAndPort.add(tfServer);
		serverAndPort.add(new JLabel("Port Number:  "));
		serverAndPort.add(tfPort);
		serverAndPort.add(new JLabel(""));
		
		northPanel.add(serverAndPort);

		// the Label and the TextField
		// A Label e o TextField
		label = new JLabel("Enter your username below", SwingConstants.CENTER);
		northPanel.add(label);
		tf = new JTextField("Anonymous");
		tf.setBackground(Color.WHITE);
		northPanel.add(tf);
		add(northPanel, BorderLayout.NORTH);

		// The CenterPanel which is the chat room
		// O Painel central com a sala de bate-papo
		ta = new JTextArea("Welcome to the Chat room\n", 80, 80);
		JPanel centerPanel = new JPanel(new GridLayout(1,1));
		centerPanel.add(new JScrollPane(ta));
		ta.setEditable(false);
		add(centerPanel, BorderLayout.CENTER);

		// the 3 buttons
		// Os 3 botões
		login = new JButton("Login");
		login.addActionListener((ActionListener) chatClient);
		logout = new JButton("Logout");
		logout.addActionListener((ActionListener) chatClient);
		logout.setEnabled(false);		
		// you have to login before being able to logout
		// Precisa estar logado antes de habilitar o botão sair
		whoIsIn = new JButton("Who is in");
		whoIsIn.addActionListener((ActionListener) chatClient);
		whoIsIn.setEnabled(false);		
		// you have to login before being able to Who is in
		// Precisa estar logado antes de habilitar o botão Quem está?

		JPanel southPanel = new JPanel();
		southPanel.add(login);
		southPanel.add(logout);
		southPanel.add(whoIsIn);
		add(southPanel, BorderLayout.SOUTH);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(600, 600);
		setVisible(true);
		tf.requestFocus();

	}
	
	public void connectionFailed(ActionListener eventControl) {
		login.setEnabled(true);
		logout.setEnabled(false);
		whoIsIn.setEnabled(false);
		label.setText("Enter your username below");
		tf.setText("Anonymous");
		// Reset port number and host name as a construction time
		// Restaurando o número da porta e o hostname como padrão
		tfPort.setText("" + defaultPort);
		tfServer.setText(defaultHost);
		
		// Let the user change them
		// Deixando o usuário editar as informações do servidor
		tfServer.setEditable(false);
		tfPort.setEditable(false);
		
		tf.removeActionListener(eventControl);
		connected = false;
	}
	
	
	// Método responsável por escreve na TextArea
	// Method to write in the TextArea
	public void append(String str) {
		ta.append(str);
		ta.setCaretPosition(ta.getText().length() - 1);
	}
	
	

}

