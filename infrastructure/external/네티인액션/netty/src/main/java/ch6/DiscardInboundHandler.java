package ch6;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;

@ChannelHandler.Sharable
public class DiscardInboundHandler extends ChannelInboundHandlerAdapter {
	@Override
	public void channelRead (ChannelHandlerContext ctx, Object msg) throws Exception {
		ReferenceCountUtil.release(msg); // 리소스 해제
	}

	public static class DiscardOutHandler extends ChannelOutboundHandlerAdapter {
		@Override
		public void write (ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
			ReferenceCountUtil.release(msg);
			promise.setSuccess();
		}

	}

}
