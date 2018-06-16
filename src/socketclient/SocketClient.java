package socketclient;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import handler.ClientHandler;

public class SocketClient {
	
	private Socket socket = null;
	private ClientHandler handler = null;
	
	public SocketClient(String host, int port, ClientHandler handler) {
		try {
			this.socket = new Socket(host, port);
			this.handler = handler;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void send(ObjectOutputStream outputStream) {
		
	}
	
}
