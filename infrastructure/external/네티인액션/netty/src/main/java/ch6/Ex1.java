package ch6;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

public class Ex1 extends ChannelHandlerAdapter { // ChannelHandlerAdapter 로 확장

	// 수신한 메시지를 삭제
	@Override
	public void channelRead (ChannelHandlerContext ctx, Object msg) throws Exception {
		ReferenceCountUtil.release(msg);
	}

}
