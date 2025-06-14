package ch2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

@ChannelHandler.Sharable
public class EchoServerHandler extends ChannelHandlerAdapter {

	@Override
	public void channelRead (ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf in = (ByteBuf) msg;
		System.out.println("Server received: " + in.toString(CharsetUtil.UTF_8)); // 메시지를 콘솔에 로깅
		ctx.write(in);
	}

	@Override
	public void channelReadComplete (ChannelHandlerContext ctx) throws Exception {
		ctx.writeAndFlush(Unpooled.EMPTY_BUFFER) // 대기 중인 메시지를 원격 피어로 flush 하고 채널을 닫음
			.addListener(ChannelFutureListener.CLOSE);
	}

	@Override
	public void exceptionCaught (ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace(); // 예외 스택 추적 출력
		ctx.close(); // 채녈 닫음
	}

}
