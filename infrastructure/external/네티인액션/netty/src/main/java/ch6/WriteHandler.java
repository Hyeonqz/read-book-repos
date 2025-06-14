package ch6;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class WriteHandler extends ChannelHandlerAdapter {
	// ctx 참조를 나중에 이용하기 위해 캐싱
	private ChannelHandlerContext ctx;

	@Override
	public void handlerAdded (ChannelHandlerContext ctx) throws Exception {
		this.ctx = ctx;
	}

	// 저장한 ctx 를 이용해 메시지를 전송
	public void send(String msg) {
		ctx.writeAndFlush(msg);
	}

}
