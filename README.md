### 已实现
**使用 Netty 替代传统 Socket 实现网络传输**

**Netty 重用 Channel 避免重复连接服务端**

**增加 Netty 心跳机制 : 保证客户端和服务端的连接不被断掉，避免重连。**

**通过CGlib实现动态代理调用远程方法**

**使用 **`CompletableFuture`** 包装接受客户端返回结果**

**可选择使用 Nacos 或 Zookeeper 管理相关服务地址信息**

**包含多种可选的序列化机制 Kyro, FTS, Hessian, protostuff**

**包含多种可选的压缩机制 snappy, lzo, gzip, bzip2, deflate**

**实现多种负载均衡算法 ：随机负载均衡算法, 轮询算法, 一致性哈希算法, P2C算法**

**实现限流, 可选择令牌桶算法或使用sentinel**

**实现对 SPI 机制的运用, 实现服务组件实现类与服务组件调用的解耦**

**通过MySQL实现分布式事务, 通过**`@GlobalTransaction`**开启**

**支持重试机制：当请求因为网络调用丢失时，尝试重新发送请求，以确保服务调用的成功**

**支持幂等：为每个请求生成唯一的 ID，在服务端对请求 ID 进行幂等性校验， 避免同一请求被重复执行。** 

**集成 Spring 通过注解扫描包注册服务, 服务消费**

**增加Java Spring Boot Starter 简化客户端调用(通过注解选择开启关闭), 通过配置文件yml配置序列化, 注册中心, 压缩方式等功能**

### 未实现
**服务监控中心（类似dubbo admin）**

