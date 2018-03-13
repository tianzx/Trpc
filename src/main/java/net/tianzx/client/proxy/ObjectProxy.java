package net.tianzx.client.proxy;

import net.tianzx.client.ConnectManage;
import net.tianzx.client.RpcClientHandler;
import net.tianzx.client.RpcFuture;
import net.tianzx.model.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: tianzx
 * Date: 3/13/18
 * Time: 3:38 PM
 */
public class ObjectProxy<T> implements IAsyncObjectProxy, InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(ObjectProxy.class);
    private Class<T> clazz;

    public ObjectProxy(Class<T> clazz) {
        this.clazz = clazz;
    }


    @Override
    public RpcFuture call(String funcName, Object... args) {
        return null;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class == method.getDeclaringClass()) {
            String name = method.getName();
            if ("equals".equals(name)) {
                return proxy == args[0];
            } else if ("hashCode".equals(name)) {
                return System.identityHashCode(proxy);
            } else if ("toString".equals(name)) {
                return proxy.getClass().getName() + "@" +
                        Integer.toHexString(System.identityHashCode(proxy)) +
                        ", with InvocationHandler " + this;
            } else {
                throw new IllegalStateException(String.valueOf(method));
            }
        }
        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);
        logger.debug(method.getDeclaringClass().getName());
        logger.debug(method.getName());
        RpcClientHandler handler = ConnectManage.getInstance().chooseHandler();

    }
}
