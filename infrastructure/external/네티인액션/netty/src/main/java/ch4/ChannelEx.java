package ch4;

import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.CharsetUtil;

public class ChannelEx {
	public static void main (String[] args) {

		ChannelEx channelEx = new ChannelEx();

		channelEx.recordChannel();



	}


	private void recordChannel() {
		Channel channel1 = null;
		ByteBuf buf = Unpooled.copiedBuffer("your data", CharsetUtil.UTF_8);
		ChannelFuture cf = channel1.writeAndFlush(buf);

		cf.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete (ChannelFuture future) throws Exception {
				if(future.isSuccess()) {
					System.out.println("Write successful");
				} else {
					System.err.print("Write failed");
					future.cause().printStackTrace();
				}
			}
		});
	}

	private void multiThreadRecord() {
		final Channel channel = null;
		final ByteBuf buf = Unpooled.copiedBuffer("your data", CharsetUtil.UTF_8).retain();

		Runnable writer = new Runnable() {
			@Override
			public void run () {
				channel.write(buf.duplicate());
			}
		};
		Executor executor = Executors.newCachedThreadPool();

		//한 스레드에서 기록
		executor.execute(writer); // 쓰기 작업을 실행기에 전달해 한 스레드에서 실행

		//다른 스레드에서 기록
		executor.execute(writer); // 다른 쓰기 작업을 전달해 다른 스레드에서 실행
	}

}
