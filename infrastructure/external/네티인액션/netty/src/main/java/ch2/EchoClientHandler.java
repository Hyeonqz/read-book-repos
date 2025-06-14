package ch2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

@ChannelHandler.Sharable // 이 클래스의 인스턴스를 여러 채널에서 공유할 수 있음을 나타냄
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
	@Override
	protected void messageReceived (ChannelHandlerContext ctx, ByteBuf msg) throws Exception {

	}

	//INFO: 채널 활성화 알림을 받으면 메시지를 전송
	@Override
	public void channelActive (ChannelHandlerContext ctx) throws Exception {
		ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8));
	}

	//INFO: 수신한 메시지의 덤프를 로깅
	public void channelRead (ChannelHandlerContext ctx, ByteBuf in) throws Exception {
		System.out.println("Client received: " + in.toString(CharsetUtil.UTF_8));
	}

	//INFO: 예외 시 오류를 로깅하고 채널을 닫음
	@Override
	public void exceptionCaught (ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

}
