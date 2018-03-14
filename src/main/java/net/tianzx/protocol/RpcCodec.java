package net.tianzx.protocol;

/**
 * Created with IntelliJ IDEA.
 * User: tianzx
 * Date: 3/14/18
 * Time: 10:45 AM
 */
public interface RpcCodec {
    <T> byte[] encode(T o);

    <T> Object decode(byte[] bytes, Class<T> clazz);
}
