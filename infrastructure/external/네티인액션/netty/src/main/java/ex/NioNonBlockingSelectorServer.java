package ex;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class NioNonBlockingSelectorServer {
	private static final Map<SocketChannel, ByteBuffer> sockets = new ConcurrentHashMap<>();

	public static void main (String[] args) throws IOException {

		// 서버 생성
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

		// 서버 8080포트에 바인딩
		serverSocketChannel.bind(new InetSocketAddress(8080));

		// NIO 모드로 전환
		serverSocketChannel.configureBlocking(false);

		// Selector 생성
		try (Selector selector = Selector.open()) {
			// 채널 관리자(Selector) 에게 ServerSocketChannel 등록
			// Accept, connection 에만 관심이 있으므로 OP_ACCEPT 를 등록한다.
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

			while(true) {
				// 채널관리자(Selector) 에 등록된 채널들의 이벤트를 감지한다. (이벤트 발생전까지는 Blocking)
				selector.select();

				// 채널관리자(Selector) 에 등록된 채널들의 이벤트를 순회한다.
				Set<SelectionKey> selectionKeys = selector.selectedKeys();

				for(Iterator<SelectionKey> it = selectionKeys.iterator(); it.hasNext(); ) {
					SelectionKey key = it.next();

					if(key.isValid()) {
						handleAcceptEvent(key); // 연결이 들어온 경우
					}
					if(key.isReadable()) {
						handleReadEvent(key); // 읽기 이벤트 발생한 경우
					}
					if(key.isWritable()) {
						handleWriteEvent(key); // 쓰기 이벤트 발생한 경우
					}
					it.remove();
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	// 수용하는 것이니 ServerSocketChannel 로부터 시작.
	private static void handleAcceptEvent (SelectionKey key) throws IOException {
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel)key.channel();
		SocketChannel socketChannel = serverSocketChannel.accept();

		socketChannel.configureBlocking(false);

		socketChannel.register(key.selector(), SelectionKey.OP_READ);

		// 매 Socket 마다 하나의 ByteBuffer 를 할당한다. -> 용량 80으로 제한
		sockets.put(socketChannel, ByteBuffer.allocate(80));
	}

	// 이미 ServerSocketChannel 에서 수용된 후를 다룸으로 SocketChannel 부터 시작
	private static void handleReadEvent (SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel)key.channel();
		ByteBuffer byteBuffer = sockets.get(socketChannel);

		int data = socketChannel.read(byteBuffer);

		if(data==-1) {
			closeSocket(socketChannel);
			sockets.remove(socketChannel);
		}

		// position==0 으로 함으로써 읽기 모드로 전환한다.
		byteBuffer.flip();

		// 대문자 변환 작업 수행
		toUpperCase(byteBuffer);

		socketChannel.configureBlocking(false);
		key.interestOps(SelectionKey.OP_WRITE);

	}

	private static void handleWriteEvent (SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel)key.channel();
		ByteBuffer byteBuffer = sockets.get(socketChannel);

		socketChannel.write(byteBuffer);

		// 전부 다 write 한 경우
		while(!byteBuffer.hasRemaining()) {
			byteBuffer.compact();
			key.interestOps(SelectionKey.OP_READ);
		}
	}

	private static void closeSocket (SocketChannel channel) {
		try {
			channel.close();
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
