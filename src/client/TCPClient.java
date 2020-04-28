package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import util.ClientHelper;
import util.Logger;

/**
 * Class of TCP client
 * @author Shiqi Luo
 */
public class TCPClient {
	private Socket socket;
	private DataInputStream inputFromServer = null;
	private DataOutputStream output = null;
	
	private String address = null;
	private int port = 0;
	
	/**
	 * Constructor of TCP client
	 * @param address
	 * @param port
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public TCPClient(String address, int port) throws UnknownHostException, IOException{
		this.socket = new Socket(address, port);
		this.socket.setSoTimeout(5*1000);
		this.inputFromServer = new DataInputStream(socket.getInputStream());
		this.output = new DataOutputStream(socket.getOutputStream());
		this.address = ""+address;
		this.port = port;
	}
	
	/**
	 * Run method of SocketClient, listen to command line message, 
	 * send it to server and print response string
	 * @throws IOException
	 */
	public void run() throws IOException {
		String response = null;
		String query = "";
		
		query = ClientHelper.getQuery();

		while(query != null){
			try{
				output.writeUTF(query);
				response = inputFromServer.readUTF();
				Logger.printClientInfo("response from server -- " + response);
				
				inputFromServer.close();
				output.close();
				socket.close();

			} catch (SocketTimeoutException e) {
				Logger.printClientInfo("Socket time out. Please try again.");
				
				inputFromServer.close();
				output.close();
				socket.close();
			}

			query = ClientHelper.getQuery();
			
			this.socket = new Socket(address, port);
			this.inputFromServer = new DataInputStream(socket.getInputStream());
			this.output = new DataOutputStream(socket.getOutputStream());
			this.socket.setSoTimeout(5*1000);
		}

		// close streams and sockets after received response
		output.close();
		inputFromServer.close();
		socket.close();
	}

	/**
	 * Main function
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			if(args.length != 2){
				throw new IllegalArgumentException("Only accept 2 arguments: ip address and port!");
			}
			TCPClient client = new TCPClient(args[0], Integer.valueOf(args[1]));
			client.run();
		} catch (IOException | IllegalArgumentException e) {
			Logger.printClientInfo(e.getLocalizedMessage());
		}
	}

}
