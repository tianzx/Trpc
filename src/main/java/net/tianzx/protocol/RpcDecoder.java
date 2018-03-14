package net.tianzx.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tianzx
 * Date: 3/14/18
 * Time: 11:06 AM
 */
public class RpcDecoder extends ByteToMessageDecoder {
    private static final Log logger = LogFactory.getLog(RpcDecoder.class);
    private Class<?> clazz;
    private RpcCodec codec;

    public RpcDecoder(Class<?> clazz, RpcCodec codec) {
        this.clazz = clazz;
        this.codec = codec;
    }

    public RpcDecoder(Class<?> clazz) {
//        this.clazz = clazz;
        this(clazz, ProtostuffCodec.SharedProtostuffCodecHolder.PROTOSTUFF_CODEC);
        logger.info("Using default Codec: Protostuff");
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() < 4) {
            return;
        }
        byteBuf.markReaderIndex();
        int dataLength = byteBuf.readInt();
        if (byteBuf.readableBytes() < dataLength) {
            byteBuf.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        Object obj = codec.decode(data, clazz);
        list.add(obj);
    }
}
