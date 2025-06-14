package ex;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class IoSimpleBlockingServer {
	public static void main (String[] args) throws IOException {
		ServerSocket serverSocket = new ServerSocket(8080);

		while(true) {
			Socket socket = serverSocket.accept();
			handleRequest(socket);
		}
	}

	private static void handleRequest (Socket socket) {
		try(InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream()) {
			int data;

			while((data = in.read()) != -1)  {
				data = Character.isLetter(data) ? toUpperCase(data):data;
				out.write(data);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static int toUpperCase (int data) {
		return Character.toUpperCase(data);
	}

}