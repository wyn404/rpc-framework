package com.rpc.test.service;

import com.rpc.common.annotation.RpcAutowired;

public class FooService implements Foo{
    @RpcAutowired(version = "1.0")
    private HelloService helloService1;

    @RpcAutowired(version = "2.0")
    private HelloService helloSerive2;

    @Override
    public String say(String s){
        return helloService1.hello(s);
    }

}
