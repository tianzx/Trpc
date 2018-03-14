package net.tianzx.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created with IntelliJ IDEA.
 * User: tianzx
 * Date: 3/14/18
 * Time: 11:01 AM
 */
public class RpcEncoder extends MessageToByteEncoder {
    private static final Log logger = LogFactory.getLog(RpcEncoder.class);
    private Class<?> cls;
    private RpcCodec codec;

    public RpcEncoder(Class<?> cls, RpcCodec codec) {
        this.cls = cls;
        this.codec = codec;
    }

    public RpcEncoder(Class<?> cls) {
        this(cls, ProtostuffCodec.SharedProtostuffCodecHolder.PROTOSTUFF_CODEC);
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        if (cls.isInstance(o)) {
            byte[] bytes = codec.encode(o);
            logger.debug("Success to encode the object:" + o + " to byte array:" + bytes);
            byteBuf.writeInt(bytes.length);
            byteBuf.writeBytes(bytes);
            logger.debug("Success to write the bytes to byteBuf:" + bytes);
        }
    }
}
