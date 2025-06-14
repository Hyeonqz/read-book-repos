package ch6;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class SimpleDiscardHandler extends SimpleChannelInboundHandler<Object> {

	@Override
	protected void messageReceived (ChannelHandlerContext ctx, Object msg) throws Exception {
	}

	@Override
	public void channelRead (ChannelHandlerContext ctx, Object msg) throws Exception {
		super.channelRead(ctx, msg);
		// 리소스를 명시적으로 해체할 필요가 없음.
	}

}
