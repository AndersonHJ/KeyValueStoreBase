package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import Model.OperationType;
import Model.Request;
import util.Logger;

/**
 * Key value store thread using TCP protocol
 * @author Shiqi Luo
 */
public class KeyValueTCPThread implements Runnable {
	// TCP part
	private Socket client = null;
	private ServerSocket tcpServerSocket = null;
	private DataInputStream input = null;
	private DataOutputStream output = null;
	
	private KeyValueStoreService keyValueStore = null;

	/**
	 * Constructor of Key value TCP thread
	 * @param service
	 * @param port
	 * @throws IOException
	 */
	public KeyValueTCPThread(KeyValueStoreService service, int port) throws IOException {
		this.keyValueStore = service;
		this.tcpServerSocket = new ServerSocket(port);
	}
	
	@Override
	public void run() {
		String query = "";
		String response = "";
		
		while(true){
			try{
				this.client = this.tcpServerSocket.accept();
				this.input = new DataInputStream(client.getInputStream());
				this.output = new DataOutputStream(client.getOutputStream());
				
				query = this.input.readUTF();
				Logger.logServerInfo("get query:[" + query + "]", this.client.getRemoteSocketAddress().toString());

				Request request = Request.parseQuery(query);
				
				if(request.getOperationType().equals(OperationType.get)){
					String value = this.keyValueStore.get(request.getKey());
					response = ((value == null) ? "Don't have the value of key -> \"" + request.getKey() : value) + "\"";
					this.output.writeUTF(response);
				}
				else if(request.getOperationType().equals(OperationType.put)){
					boolean result = this.keyValueStore.put(request.getKey(), request.getValue());
					response = "put operation " + (result == true ? "successed" : "failed");
					this.output.writeUTF(response);
				}
				else if(request.getOperationType().equals(OperationType.delete)){
					boolean result = this.keyValueStore.delete(request.getKey());
					response = "delete operation " + (result == true ? "successed" : "failed(key is not existed)");
					this.output.writeUTF(response);
				}
				
				Logger.logServerInfo("response:[" + response + "]", this.client.getRemoteSocketAddress().toString());

				this.output.close();
				this.input.close();
				this.client.close();
				
			} catch(EOFException e2) {
				e2.printStackTrace();
				try {
					this.output.close();
					this.input.close();
					this.client.close();
				} catch (IOException e3) {
					Logger.logServerError(e3.getMessage(), this.client.getRemoteSocketAddress().toString());
				}
				
			}catch (Exception e) {
				try {
					this.output.writeUTF(e.getMessage());
					Logger.logServerError(e.getMessage(), this.client.getRemoteSocketAddress().toString());

					this.output.close();
					this.input.close();
					this.client.close();
				} catch (IOException e1) {
					Logger.logServerError(e1.getMessage(), this.client.getRemoteSocketAddress().toString());
				}
			}
		}
		
	}
}
