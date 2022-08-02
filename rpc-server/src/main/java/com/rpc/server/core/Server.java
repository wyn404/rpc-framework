package com.rpc.server.core;

public abstract class Server {
    /**
     *  start server
     */
    public abstract void start() throws Exception;


    /**
     *  stop server
     */
    public abstract void stop() throws Exception;
}
