package handler;

import java.io.ObjectInputStream;
import java.net.Socket;

public interface ClientHandler {
	public void onRecieved(Socket socket);
}
