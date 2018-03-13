package net.tianzx.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.tianzx.model.RpcResponse;

/**
 * Created with IntelliJ IDEA.
 * User: tianzx
 * Date: 3/13/18
 * Time: 11:12 AM
 */
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {

    }
}
