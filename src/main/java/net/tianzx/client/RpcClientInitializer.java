package net.tianzx.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;


/**
 * Created with IntelliJ IDEA.
 * User: tianzx
 * Date: 3/14/18
 * Time: 10:27 AM
 */
public class RpcClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline cp = socketChannel.pipeline();
//        cp.addLast(new RpcE);
    }
}
