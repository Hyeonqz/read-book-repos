package ch4;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

public class PlainOioServer {
	public void serve(int port) throws IOException {
		final ServerSocket serverSocket = new ServerSocket(port);
		try{
			for(;;) {
				final Socket clientSocket = serverSocket.accept();
				System.out.println("Accepted connection from " + clientSocket.getRemoteSocketAddress() + "Client Socket : " + clientSocket);

				new Thread(new Runnable() {

					@Override
					public void run () {
						OutputStream out;
						try {
							out = clientSocket.getOutputStream();
							out.write("Hi\r\n".getBytes(Charset.forName("UTF-8")));
							out.flush();
							clientSocket.close();
						} catch (IOException e) {
							throw new RuntimeException(e);
						} finally {
							try {
								clientSocket.close();
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
						}
					}
				}).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
