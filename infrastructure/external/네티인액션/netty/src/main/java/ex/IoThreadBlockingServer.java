package ex;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class IoThreadBlockingServer {
	public static void main (String[] args) throws IOException {
		ServerSocket serverSocket = new ServerSocket(8081);

		while(true) {
			// blocking call -> 새 클라이언트 접속 까지 blocking
			Socket socket = serverSocket.accept();
			// Socket 이 연결되면 스레드 생성됨
			new Thread( () -> handleRequest(socket)).start();
		}
	}

	private static void handleRequest (Socket socket) {
		try(InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream()) {

			int data;

			// read() 데이터를 읽을 때 까지 다른 요청 다 blocking 됨. 만약 소켓이 닫혀 있다면 -1
			while( (data = in.read()) != -1) {
				data = Character.isLetter(data) ? toUpperCase(data) :data;
				out.write(data);
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		} ;

	}

	private static int toUpperCase(int data) {
		return Character.toUpperCase(data);
	}

}
