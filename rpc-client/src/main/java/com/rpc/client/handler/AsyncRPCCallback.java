package com.rpc.client.handler;

public interface AsyncRPCCallback {

    void success(Object result);

    void fail(Exception e);
}
