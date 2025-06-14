/*
package ch1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class Ex1 {
	static final int portNumber = 9000;

	public static void main (String[] args) throws IOException {
		ServerSocket serverSocket = new ServerSocket(portNumber); // 포트 연결 수신
		Socket clientSocket = serverSocket.accept(); // accept 호출은 연결될 때 까지 진행을 블로킹

		*/
/* 위 소켓으로부터 스트림 객체를 파생한다 *//*

		BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

		String request = in.readLine();
		String response = "";

		while(request !=  null) { // 처리 루프 시작
			if("Done".equals(request))  // 클라이언트가 "Done" 을 전송한 경우 처리 루프 종료
				break;
		}
		response = processReqeust(request); // 요청이 서버의 처리 메소드로 전달
		out.println(response); // 서버의 응답이 클라이언트로 전달

		Channel channel = "";
		ChannelFuture future = channel.connect(new InetSocketAddress("192.168.0.1",25));
		future.addListener(new ChannelFutureListener() {

			@Override
			public void operationComplete (ChannelFuture channelFuture) throws Exception {
				if(channelFuture.isSuccess()) {
					// 작업이 성공적인 경우 데이터를 저장할 ByteBuf 생성
				} else {
					Throwable cause = future.cause(); // 오류 원인 접근
					cause.printStackTrace();
				}
			}
		});
	}

}

class ConnectHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelActive (ChannelHandlerContext ctx) throws Exception { // 새로운 연결이 이뤄지면 channelActive 가 호출된다.
		super.channelActive(ctx);
		System.out.println("Client" + ctx.channel().remoteAddress() + " connected");
	}

}
*/
