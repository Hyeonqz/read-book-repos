package ch11;

import java.io.Serializable;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.marshalling.MarshallerProvider;
import io.netty.handler.codec.marshalling.MarshallingDecoder;
import io.netty.handler.codec.marshalling.MarshallingEncoder;
import io.netty.handler.codec.marshalling.UnmarshallerProvider;

public class MarsahllingInitializer extends ChannelInitializer<Channel> {
	private final MarshallerProvider marshallerProvider;
	private final UnmarshallerProvider unmarshallerProvider;

	public MarsahllingInitializer (MarshallerProvider marshallerProvider, UnmarshallerProvider unmarshallerProvider) {
		this.marshallerProvider = marshallerProvider;
		this.unmarshallerProvider = unmarshallerProvider;
	}

	@Override
	protected void initChannel (Channel ch) throws Exception {
		ChannelPipeline channelPipeline = ch.pipeline();
		channelPipeline.addFirst(new MarshallingDecoder(unmarshallerProvider));
		channelPipeline.addLast(new MarshallingEncoder(marshallerProvider));
		channelPipeline.addLast(new ObjectHandler());
	}

	public static final class ObjectHandler extends SimpleChannelInboundHandler<Serializable> {

		@Override
		protected void messageReceived (ChannelHandlerContext ctx, Serializable msg) throws Exception {

		}

	}

}
