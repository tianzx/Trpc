package net.tianzx.client;

/**
 * Created with IntelliJ IDEA.
 * User: tianzx
 * Date: 3/13/18
 * Time: 2:07 PM
 */
public interface AsyncRPCCallback {
    void success(Object result);

    void fail(Exception e);

}
