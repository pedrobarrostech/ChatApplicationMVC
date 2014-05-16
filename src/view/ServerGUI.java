package view;
import javax.swing.*;

import model.Server;

import java.awt.*;
import java.awt.event.*;

public class ServerGUI extends JFrame  {
	
	private static final long serialVersionUID = 1L;
	// The stop and start buttons
	// Botões de iniciar e parar
	public JButton stopStart;
	
	// JTextArea for the chat room and the events
	// JTextAre para a sala e para o log de eventos
	private JTextArea chat, event;
	
	// The port number
	// Número da porta
	public JTextField tPortNumber;
	
	// My server
	// Servidor
	@SuppressWarnings("unused")
	private Server server;
	
	
	// Server constructor that receive the port and the event to listen to for connection as parameter
	// Contrutor do servidor recebe a porta e o evento como parametro
	public ServerGUI(int port, ActionListener eventControl) {
		super("Chat Server");
		server = null;
		// In the NorthPanel the PortNumber the Start and Stop buttons
		// No painel superior o numero da porta iniciada e o botão de parar o servidor
		
		JPanel north = new JPanel();
		north.add(new JLabel("Port number: "));
		tPortNumber = new JTextField("  " + port);
		north.add(tPortNumber);
		// To stop or start the server, we start with "Start"
		// Para iniciar ou parar o servidor, começando com "Iniciar"
		
		stopStart = new JButton("Start");
		stopStart.addActionListener(eventControl);
		north.add(stopStart);
		add(north, BorderLayout.NORTH);
		
		// The event and chat room
		// O evento e a sala
		JPanel center = new JPanel(new GridLayout(2,1));
		chat = new JTextArea(80,80);
		chat.setEditable(false);
		appendRoom("Chat room.\n");
		center.add(new JScrollPane(chat));
		event = new JTextArea(80,80);
		event.setEditable(false);
		appendEvent("Events log.\n");
		center.add(new JScrollPane(event));	
		add(center);
		
		// Need to be informed when the user click the close button on the frame
		// Precisamos informar quando o usuário clicou no botão de fechar
		addWindowListener((WindowListener) eventControl);
		setSize(400, 600);
		setVisible(true);
	}		

	// Append message to the two JTextArea position at the end
	// Anexando a mensagem nas duas JTextarea
	public void appendRoom(String str) {
		chat.append(str);
		chat.setCaretPosition(chat.getText().length() - 1);
	}
	public void appendEvent(String str) {
		event.append(str);
		event.setCaretPosition(chat.getText().length() - 1);
		
	}


}

