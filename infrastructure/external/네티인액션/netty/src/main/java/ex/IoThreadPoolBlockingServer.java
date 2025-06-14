package ex;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IoThreadPoolBlockingServer {
	public static void main (String[] args) throws IOException {
		ServerSocket serverSocket = new ServerSocket(8082);
		ExecutorService threadPool = Executors.newFixedThreadPool(3);

		while(true) {
			Socket socket = serverSocket.accept();
			threadPool.submit( () -> handleRequest(socket));
		}
	}

	private static void handleRequest (Socket socket) {
		try(InputStream inputStream = socket.getInputStream();
		OutputStream outputStream = socket.getOutputStream()) {

			int data;

			while( (data = inputStream.read()) != -1) {
				data = Character.isLetter(data) ? toUpperCase(data) : data;
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	private static int toUpperCase (int data) {
		return Character.toUpperCase(data);
	}

}
