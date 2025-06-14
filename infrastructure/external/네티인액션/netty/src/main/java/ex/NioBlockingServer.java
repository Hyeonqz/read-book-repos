package ex;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class NioBlockingServer {
	public static void main (String[] args) throws IOException {
		// NIO 서버 소켓 생성 (NIO Channel)
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

		// NIO 서버를 8080 포트에 바인딩 시킨다
		serverSocketChannel.bind(new InetSocketAddress(8080));

		while (true) {
			// 블로킹은 일반 Socket 사용함
			SocketChannel socketChannel = serverSocketChannel.accept();
			handleRequest(socketChannel);
		}
	}

	private static void handleRequest (SocketChannel socketChannel) {
		// 용량 80으로 제한
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(80);

		try {
			while(socketChannel.read(byteBuffer) != -1) {
				// NIO 소켓으로부터 읽은 데이터를 쓰기 위해 flip()
				byteBuffer.flip();
				// position 을 0으로 세팅, limit 은 읽은 데이터 크기 만큼

				toUpperCase(byteBuffer);

				// 남아있는 NIO 소켓에 데이터를 쓴다.
				while(byteBuffer.hasRemaining()) {
					socketChannel.write(byteBuffer);
				}

				// 버퍼 position 0으로 세팅
				byteBuffer.compact();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static void toUpperCase (final ByteBuffer byteBuffer) {
		// ByteBuffer 안에 데이터들을 모두 읽어 대문자로 변환한다.
		for (int x = 0; x <byteBuffer.limit() ; x++) {
			byteBuffer.put(x, (byte) toUpperCase(byteBuffer.get(x)));
		}
	}

	private static int toUpperCase (int data) {
		return Character.isLetter(data) ? Character.toUpperCase(data) : data;
	}

}
