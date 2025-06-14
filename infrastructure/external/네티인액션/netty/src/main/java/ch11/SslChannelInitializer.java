package ch11;

import javax.net.ssl.SSLEngine;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

public class SslChannelInitializer extends ChannelInitializer<Channel> {
	private final SslContext sslContext;
	private final boolean stratTls;

	public SslChannelInitializer (SslContext sslContext, boolean stratTls) {
		this.sslContext = sslContext;
		this.stratTls = stratTls;
	}

	@Override
	protected void initChannel (Channel ch) throws Exception {
		SSLEngine engine = sslContext.newEngine(ch.alloc());
		ch.pipeline()
			.addFirst("ssl", new SslHandler(engine, stratTls));
	}


}
