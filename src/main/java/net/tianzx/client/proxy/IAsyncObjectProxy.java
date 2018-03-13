package net.tianzx.client.proxy;

import net.tianzx.client.RpcFuture;

/**
 * Created with IntelliJ IDEA.
 * User: tianzx
 * Date: 3/13/18
 * Time: 3:33 PM
 */
public interface IAsyncObjectProxy {
    public RpcFuture call(String funcName, Object... args);
}
