package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import Model.OperationType;
import Model.Request;
import util.Logger;

/**
 * Key value store thread using UDP protocol
 * @author Shiqi Luo
 */
public class KeyValueUDPThread implements Runnable {
	// UDP part
	private DatagramSocket udpSocket = null;
	private DatagramPacket udpRequest = null;
	private DatagramPacket udpRsponse = null;
	
	private byte[] buffer = null;
	
	private KeyValueStoreService keyValueStore = null;

	/**
	 * Constructor of Key value UDP thread
	 * @param service
	 * @param port
	 * @throws SocketException
	 */
	public KeyValueUDPThread(KeyValueStoreService service, int port) throws SocketException {
		this.keyValueStore = service;
		this.udpSocket = new DatagramSocket(port);
		this.buffer = new byte[1000];
	}

	@Override
	public void run() {
		while(true){
			try{
				this.udpRequest = new DatagramPacket(this.buffer, this.buffer.length);
				this.udpSocket.receive(this.udpRequest);

				Logger.logServerInfo("get query:[" + new String(this.buffer) + "]", this.udpRequest.getAddress().toString());
				Request request = Request.parseQuery(new String(this.buffer));
				
				if(request.getOperationType().equals(OperationType.get)){
					this.buffer = this.keyValueStore.get(request.getKey()).getBytes();
					this.udpRsponse = new DatagramPacket(this.buffer, this.buffer.length, this.udpRequest.getAddress(), this.udpRequest.getPort());
					this.udpSocket.send(this.udpRsponse);
				}
				else if(request.getOperationType().equals(OperationType.put)){
					boolean result = this.keyValueStore.put(request.getKey(), request.getValue());
					this.buffer = ("put operation " + (result == true ? "successed" : "failed")).getBytes();
					this.udpRsponse = new DatagramPacket(this.buffer, this.buffer.length, this.udpRequest.getAddress(), this.udpRequest.getPort());
					this.udpSocket.send(this.udpRsponse);
				}
				else if(request.getOperationType().equals(OperationType.delete)){ 
					boolean result = this.keyValueStore.delete(request.getKey());
					this.buffer = ("delete operation " + (result == true ? "successed" : "failed(key is not existed)")).getBytes();
					this.udpRsponse = new DatagramPacket(this.buffer, this.buffer.length, this.udpRequest.getAddress(), this.udpRequest.getPort());
					this.udpSocket.send(this.udpRsponse);
				}
				
				Logger.logServerInfo("response:[" + new String(this.buffer) + "]", this.udpRequest.getAddress().toString());
			} catch (Exception e) {
				try {
					this.buffer = e.getMessage().getBytes();
					this.udpRsponse = new DatagramPacket(this.buffer, this.buffer.length, this.udpRequest.getAddress(), this.udpRequest.getPort());
					this.udpSocket.send(this.udpRsponse);
					Logger.logServerError(e.getMessage(), this.udpRequest.getAddress().toString());
					this.udpSocket.close();
				} catch (IOException e1) {
					Logger.logServerError(e1.getMessage(), this.udpRequest.getAddress().toString());
				}
			}
		}
	}
}
