# rpc-framework

------



## 介绍

------

一个基于Netty+Kryo+Zookeeper实现的轻量级分布式RPC框架。

RPC，即Remote Procedure Call（远程过程调用），调用远程计算机上的服务，就像调用本地服务一样。

这个RPC整体框架使用示意图如下图所示：

<img src="D:\Code\IdeaProjects\rpc-framework\images\framework.png" alt="D:\Code\IdeaProjects\rpc-framework\images\fr" style="zoom:67%;" />

服务端Server向注册中心注册服务，客户端Client通过注册中心获取服务相关信息，然后通过网络请求服务端Server。

## 特点

------

- 简单的代码和框架
- 使用Netty（基于NIO）替代BIO实现网络传输
- 使用Zookeeper管理相关服务地址信息
- 使用序列化机制Kryo替代JDK自带的序列化机制
- 支持高可用性、负载均衡和故障转移
- 支持不同的版本号
- 支持Netty心跳机制
- 支持服务端异步多线程处理RPC请求

## 运行项目（rpc-test）

------

### 导入项目

fork项目到自己的仓库，或者克隆项目到本地：`git clone git@github.com:wyn404/rpc-framework.git`

### 定义接口

```
public interface HelloService {
    String hello(String name);
    String hello(Person person);
    String hello(String name, Integer age);
}
```

### 实现接口

```
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
```

### 下载运行Zookeeper

在windows10环境下，去[Zookeeper官网](https://zookeeper.apache.org/releases.html)下载，启动Zookeeper服务端。

### 启动服务端

运行`RpcServerBootstrap.java`文件。

### 本地客户端获取服务

运行`RpcTest.java`和`RpcAsyncTest.java`文件。

```
final RpcClient rpcClient = new RpcClient("127.0.0.1:2181");

// Sync
final HelloService syncClient = rpcClient.createService(HelloService.class, "2.0");
String result = syncClient.hello(Integer.toString(j));

// Async
RpcService client = rpcClient.createAsyncService(HelloService.class, "2.0");
RpcFuture helloFuture = client.call("hello", Integer.toString(j));
String result = (String) helloFuture.get(3000, TimeUnit.MILLISECONDS);
```

## 项目结构

------

```
rpc-framework

|-- rpc-client                           rpc客户端
|  |-- connect                          
|  |  \-- ConnctionManager               rpc连接管理
|  |-- discovery                        
|  |  \-- ServiceDiscovery               rpc服务发现
|  |-- handler                           
|  |  |-- AsyncRPCCallback               rpc异步回调接口
|  |  |-- RpcClientHandler               rpc客户端处理
|  |  |-- RpcClientInitializer           rpc客户端初始化
|  |  \-- RpcFuture                      rpc-future用于异步rpc调用
|  |-- proxy                             代理工具类
|  |  |-- ObjectProxy                    rpc对象代理
|  |  |-- RpcService                     rpc服务接口
|  |  \-- SerializableFunction           序列化函数接口
|  |-- route                             调度算法
|  |  |-- impl                           接口类实现
|  |  |  \-- RpcLoadBalanceRoundRobin    rpc轮询调度负载均衡
|  |  \-- RpcLoadBalance                 rpc负载均衡抽象类
|  \-- RpcClient                         rpc客户端  

|-- rpc-common                           rpc工具类  
|  |-- annotation                        注解包
|  |  |-- RpcAutowired                   服务注解
|  |  \-- RpcService                     服务注解		
|  |-- codec                             代码类
|  |  |-- Beat                           rpc筛选器常量
|  |  |-- RpcDecoder                     rpc解码
|  |  |-- RpcEncoder                     rpc编码
|  |  |-- RpcReuqest                     rpc请求
|  |  \-- RpcResponse                    rpc响应
|  |-- config                      
|  |  \-- Constant                       zookeeper常量
|  |-- protocol                          协议类
|  |  |-- RpcProtocol                    rpc协议
|  |  \-- RpcServiceInfo                 rpc服务信息
|  |-- serializer                        序列化
|  |  |-- KryoPoolFactory                kryo对象池工厂
|  |  |-- KyroSerializer                 kryo序列化
|  |  \-- serializer                     序列化抽象类
|  |-- util                              工具类
|  |  |-- JsonUtil 	                     Json工具类
|  |  |-- ServiceUtil                    service工具类
|  |  \-- ThreadPoolUtil                 线程池工具类
|  \-- zookeeper                     
|  |  \-- CuratorClient                  zookeeper集群配置

|-- rpc-server                           rpc服务类
|  |-- core                              核心代码
|  |  |-- NettyServer                    基于Netty的server
|  |  |-- RpcServerHandler               rpc服务处理
|  |  |-- RpcServerInitializer           rpc服务初始化
|  |  \-- Server                         Server抽象类 
|  |-- registry                         
|  |  \-- ServiceRegistry                服务注册
|  \-- RpcServer                         实现rpc server

\-- rpc-test                             rpc测试类
|  |-- client                          
|  |  |-- RpcAsyncTest                   client异步调用
|  |  \-- RpcTest                        client同步调用
|  |-- server                          
|  |  \-- RpcServerBootstrap             启动server发布service
|  |-- service                           service包
|  |  |-- Foo                            服务接口
|  |  |-- FooService                     服务实现
|  |  |-- HelloService                   服务接口
|  |  |-- HelloServiceImpl               服务实现
|  |  \-- Person                      
|  \-- resources                         资源配置
|  |  |-- log4j                          log4j日志
|  |  |-- rpc                            rpc端口号
|  |  \-- server-spring                  server-spring配置文件
```

 

