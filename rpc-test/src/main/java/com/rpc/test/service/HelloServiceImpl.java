package com.rpc.test.service;

import com.rpc.common.annotation.RpcService;

@RpcService(value = HelloService.class, version = "1.0")
public class HelloServiceImpl implements HelloService{

    public HelloServiceImpl() {}

    @Override
    public String hello(String name) {
        return "hello" + name;
    }

    @Override
    public String hello(Person person) {
        return "Hello" + person.getFirstName() + " " + person.getLastName();
    }

    @Override
    public String hello(String name, Integer age) {
        return name + " is " + age;
    }
}
