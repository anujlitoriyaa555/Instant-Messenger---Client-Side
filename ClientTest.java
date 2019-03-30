import java.io.*;
import java.awt.*;
import java.net.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.JFrame;

class Client extends JFrame{
	
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String serverIP;
	private Socket connection;
	
	public Client(String host){
		super("client side");
		serverIP = host;
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
		     new ActionListener(){
				 public void actionPerformed(ActionEvent event){
					 sendMessage(event.getActionCommand());
					 userText.setText("");
				 }
			 }
		);
		
		add(userText , BorderLayout.NORTH);
		chatWindow  = new JTextArea();
		chatWindow.setEditable(false);
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		setSize(500,400);
		setVisible(true);
	}
	
	// start running
	
	public void startRunning(){
		try{
			connectToServer();
			setUpStreams();
			whileChatting();
		}catch(EOFException eofException){
			showMessage("\n client terminated connection");
		}catch(IOException ioEx){
			ioEx.printStackTrace();
		}finally{
			closeCrap();
		}
	}
	
	// connect to server
	
	private void connectToServer(){
		showMessage("trying to connect \n");
		
		connection = new Socket(InetAddress.getByName(serverIP), 6789);
		
		showMessage("Connected to :" + connection.getInetAddress().getHostName());
	}
	
	// set up streams
	
	private void setUpStreams() throws IOException{
		
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		
		input  = new ObjectInputStream(connection.getInputStream());
		showMessage("\n streams are good to go\n");
	}
	
	// while chatting
	
	private void whileChatting() throws IOException{
		ableToType(true);
		do{
			try{
				message = (String) input.readObject();
				showMessage("\n" + message);
			}catch(ClassNotFoundException classNotfoundException){
				showMessage("\n not a valid object");
			}
		}while(!message.equals("SERVER - END"));
	}
	
	// close crap
	
	private void closeCrap(){
		showMessage("\ Closing session..");
		ableToType(false);
		try{
			output.close();
			input.close();
		    connection.close();
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	//  send message
	
	private void sendMessage(String message){
		try{
			output.writeObject("CLIENT - " + message);
			output.flush();
			showMessage("\nCLIENT - " + message);
		}catch(IOException ioException){
			chatWindow.append("\n something messed up");
		}
	}
	
	// show message
	
	private void showMessage(final String m){
		SwingUtilities.invokeLater(
		 new Runnable(){
			 public void run(){
				 chatWindow.append(m);
			 }
		 }
		);
	}
	
	// able to type 
	
	private void ableToType(final boolean tof){
		SwingUtilities.invokeLater(
		  new Runnable(){
			  public void run(){
				  userText.setEditable(tof)
			  }
		  }
		);
	}
}

class ClientTest{
	public static void main(String args[]){
		Client anu;
		anu = new Client("127.0.0.1");
		anu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		anu.startRunning();
	}
}
	
	
	
