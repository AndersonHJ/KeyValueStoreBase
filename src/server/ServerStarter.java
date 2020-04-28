package server;

import java.io.IOException;

import util.Logger;

/**
 * The class of server starter, to start TCP and UDP threads
 * @author Shiqi Luo
 */
public class ServerStarter {
	
	/**
	 * Main function
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			if(args.length != 1){
				throw new IllegalArgumentException("Only accept 1 argument: port");
			}
			KeyValueTCPThread tcpServer = new KeyValueTCPThread(new KeyValueStoreService(), Integer.valueOf(args[0]));
			new Thread(tcpServer).start();
			KeyValueUDPThread udpServer = new KeyValueUDPThread(new KeyValueStoreService(), Integer.valueOf(args[0]));
			new Thread(udpServer).start();
		} catch (IllegalArgumentException | IOException e) {
			Logger.logServerError(e.getMessage(), "null");
		}
	}

}
