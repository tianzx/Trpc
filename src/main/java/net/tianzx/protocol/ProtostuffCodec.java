package net.tianzx.protocol;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.objenesis.Objenesis;
import org.springframework.objenesis.ObjenesisStd;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: tianzx
 * Date: 3/14/18
 * Time: 10:50 AM
 */
public class ProtostuffCodec implements RpcCodec {
    private static final Log logger = LogFactory.getLog(ProtostuffCodec.class);

    public static class SharedProtostuffCodecHolder {
        public static final ProtostuffCodec PROTOSTUFF_CODEC = new ProtostuffCodec();
    }

    private static Map<Class<?>, Schema<?>> classSchemaMap = new ConcurrentHashMap<>();

    private static Objenesis objenesis = new ObjenesisStd(true);

    private <T> Schema<T> getScheme(Class<T> cls) {
        Schema<T> schema = (Schema<T>) classSchemaMap.get(cls);
        if (schema == null) {
            schema = RuntimeSchema.createFrom(cls);
            classSchemaMap.put(cls, schema);
        }
        logger.debug("get schema of class:" + cls + " : " + schema);
        return schema;
    }

    @Override

    public <T> byte[] encode(T o) {
        Class<T> cls = (Class<T>) o.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        logger.debug("Try to get schema of class:" + cls);
        try {
            Schema<T> schema = getScheme(cls);
            return ProtostuffIOUtil.toByteArray(o, schema, buffer);
        } finally {
            buffer.clear();
        }

    }

    @Override
    public <T> Object decode(byte[] bytes, Class<T> clazz) {
        T message = objenesis.newInstance(clazz);
        Schema<T> schema = getScheme(clazz);
        ProtostuffIOUtil.mergeFrom(bytes, message, schema);
        logger.debug("decode ok:" + message);
        return message;
    }
}