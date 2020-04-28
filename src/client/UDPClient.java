package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import util.ClientHelper;
import util.Logger;

/**
 * Class of UDP client
 * @author Shiqi Luo
 */
public class UDPClient {
	private DatagramSocket socket;
	private DatagramPacket request = null;
	private DatagramPacket response = null;
	
	private byte[] buffer = null;
	
	private InetAddress serverAddress = null;
	private int port = 0;
	
	/**
	 * Constructor of UDPClient
	 * @param address
	 * @param port
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public UDPClient(String address, int port) throws UnknownHostException, IOException {
		this.socket = new DatagramSocket();
		this.socket.setSoTimeout(5*1000);
		this.buffer = new byte[1000];
		this.serverAddress = InetAddress.getByName(address);
		this.port = port;
	}
	
	/**
	 * Run method of UDP client, listen to command line message, 
	 * send it to server and print response string
	 * @throws IOException
	 */
	public void run() throws IOException {
		String query = ClientHelper.getQuery();
		
		while(query != null){
			this.buffer = query.getBytes();
			
			try{
				this.request = new DatagramPacket(this.buffer, query.length(), this.serverAddress, this.port);
				this.socket.send(this.request);
	
				this.response = new DatagramPacket(this.buffer, this.buffer.length);
				this.socket.receive(this.response);
				
				Logger.printClientInfo("response from server -- " + new String(response.getData()));
			} catch (SocketTimeoutException e){
				Logger.printClientInfo("Socket time out. Please try again.");
			}
			
			query = ClientHelper.getQuery();
			
			this.socket = new DatagramSocket();
			this.socket.setSoTimeout(5*1000);
		}
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
			Logger.printClientInfo(e.getMessage());
		}
	}

}
