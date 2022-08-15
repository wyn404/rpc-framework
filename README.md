# rpc-framework



## 介绍

一个基于Netty+Kryo+Zookeeper实现的轻量级分布式RPC框架。

RPC，即Remote Procedure Call（远程过程调用），调用远程计算机上的服务，就像调用本地服务一样。

这个RPC整体框架调用过程如图所示：

<div align=center><img src=".\images\process.jpg" width="500px"/></div>

1. client通过动态代理生成代理对象，通过代理对象将请求对象序列化成二进制数据，进行编码，使用Netty选择一个从注册中心注册的server的地址，异步发起网络请求。
2. server从TCP通道中接收到二进制数据，根据定义的RPC网络协议，将数据进行解码，反序列化后，分割出接口地址和参数对象，通过反射找到接口执行调用。
3. server将调用执行结果进行序列化，编码，异步发送到TCP通道中。
4. client获取到二进制数据后，解码，反序列化成结果对象。

## 特点

- 简单的代码和框架
- 使用Netty（基于NIO）替代BIO实现网络传输
- 使用Zookeeper管理相关服务地址信息
- 使用序列化机制Kryo替代JDK自带的序列化机制
- 支持高可用性、负载均衡和故障转移
- 支持不同的版本号
- 支持Netty心跳机制
- 支持服务端异步多线程处理RPC请求

## 运行项目（rpc-test）

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

```
rpc-framework
|-- rpc-client                           rpc客户端
|  |-- connect                          
|  |  \-- ConnctionManager               rpc连接管理
|  |-- discovery                        
|  |  \-- ServiceDiscovery               rpc服务发现
|  |-- handler                           
|  |  |-- AsyncRPCCallback               
|  |  |-- RpcClientHandler               rpc客户端处理
|  |  |-- RpcClientInitializer           rpc客户端初始化
|  |  \-- RpcFuture                      rpc-future用于异步rpc调用
|  |-- proxy                            
|  |  |-- ObjectProxy                    rpc对象代理
|  |  |-- RpcService                     
|  |  \-- SerializableFunction           
|  |-- route                             调度算法
|  |  |-- impl                       
|  |  |  \-- RpcLoad...ConsistentHash    rpc哈希一致性负载均衡
|  |  |  \-- RpcLoadBalanceLFU           rpcLFU负载均衡
|  |  |  \-- RpcLoadBalanceLRU           rpcLRU负载均衡
|  |  |  \-- RpcLoadBalanceRoundRobin    rpc轮询调度负载均衡
|  |  \-- RpcLoadBalance                 
|  \-- RpcClient                         实现rpc client  
|-- rpc-common                           rpc工具类  
|  |-- annotation                        注解
|  |  |-- RpcAutowired                   
|  |  \-- RpcService                     		
|  |-- codec                             
|  |  |-- Beat                           
|  |  |-- RpcDecoder                     rpc解码
|  |  |-- RpcEncoder                     rpc编码
|  |  |-- RpcReuqest                     rpc请求实体
|  |  \-- RpcResponse                    rpc响应实体
|  |-- config                      
|  |  \-- Constant                       zookeeper常量
|  |-- protocol                          
|  |  |-- RpcProtocol                    rpc协议
|  |  \-- RpcServiceInfo                 rpc服务信息
|  |-- serializer       
|  |  |-- kryo 
|  |  |  |-- KryoPoolFactory             kryo对象池工厂
|  |  |  \-- KyroSerializer              kryo序列化
|  |  |-- protostuff 
|  |  |  \-- ProtostuffSerializer        protostuff序列化
|  |  \-- serializer                     
|  |-- util                              
|  |  |-- JsonUtil                       Json工具类
|  |  |-- ServiceUtil                    service工具类
|  |  \-- ThreadPoolUtil                 线程池工具类
|  \-- zookeeper                     
|  |  \-- CuratorClient                  zookeeper集群配置
\-- rpc-server                           rpc服务类
|  |-- core                              
|  |  |-- NettyServer                    基于Netty的server
|  |  |-- RpcServerHandler               rpc服务处理
|  |  |-- RpcServerInitializer           rpc服务初始化
|  |  \-- Server                          
|  |-- registry                         
|  |  \-- ServiceRegistry                服务注册
|  \-- RpcServer                         实现rpc server
|  \-- resources                         资源配置
|  |  |-- log4j                          log4j日志
|  |  |-- rpc                            rpc端口号
|  |  \-- server-spring                  server-spring配置文件
```

 

