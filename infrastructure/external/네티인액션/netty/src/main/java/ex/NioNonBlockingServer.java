package ex;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NioNonBlockingServer {
	public static void main (String[] args) throws IOException {
		//서버 생성
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.bind(new InetSocketAddress(8080));
		
		//NIO 모드로 전환
		serverSocketChannel.configureBlocking(false);
		
		//SocketChannel 별로 하나의 ByteBuffer 사용
		Map<SocketChannel, ByteBuffer> sockets = new ConcurrentHashMap<>();
		
		while(true) {
			//accept() 는 들어오는 연결 요청 수락
			//NIO 모드이기에 accept() 는 blocking 되지 않고, null 리턴
			SocketChannel socketChannel = serverSocketChannel.accept();
			
			//새로운 소켓이 연결된 경우
			if(socketChannel != null) {
				//연결된 Socket 을 NIO 하게 처리
				socketChannel.configureBlocking(false);
				
				//매 Socket 마다 하나의 ByteBuffer 할당
				sockets.put(socketChannel, ByteBuffer.allocate(80));
			}
			
			// 연결된 SocketChannel 을 순회하면서, 연결이 끊기 SocketChannel 제거
			sockets.keySet().removeIf(it -> !it.isOpen());
			
			// 연결된 SocketChannel 을 순회하면서, 데이터를 읽고 작업을 수행한 다음 소켓에 다시 쓰기 작업 수행
			sockets.forEach( (socketCh, byteBuffer) -> {
				try {
					// NIO 모드이기에 Blocking 모드와 다르게 read() 메소드 호출시 blocking 되지 않는다.
					int data = socketCh.read(byteBuffer);
					
					//연결이 끊긴 경우
					if(data == -1) {
						closeSocket(socketCh);
					}
					// 데이터가 들어온 경우
					else if (data != 0) {
						byteBuffer.flip(); // position 0 으로 Read 모드로 전환
						// 작업 수행
						toUpperCase(byteBuffer);
						
						while(byteBuffer.hasRemaining()) {
							socketCh.write(byteBuffer);
						}
						byteBuffer.compact();
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
		}
	}

	private static void closeSocket(SocketChannel socket) {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static void toUpperCase(final ByteBuffer byteBuffer) {
		// ByteBuffer내 모든 데이터를 읽어서 대문자로 변환한다.
		for (int x = 0; x < byteBuffer.limit(); x++) {
			byteBuffer.put(x, (byte) toUpperCase(byteBuffer.get(x)));
		}
	}

	private static int toUpperCase(int data) {
		return Character.isLetter(data) ? Character.toUpperCase(data) : data;
	}

}
