package ch6;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

// INFO 공유 가능한 ChannelHandler
@ChannelHandler.Sharable
public class SharableHandler extends ChannelHandlerAdapter {
	@Override
	public void channelRead (ChannelHandlerContext ctx, Object msg) throws Exception {
		super.channelRead(ctx, msg);
		System.out.println("Channel read messagae: " + msg);
		ctx.fireChannelRead(msg); // 메소드 호출을 로깅하고 다음 ChannelHandler 로 전달
	}

}

