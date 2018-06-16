package main;

import rpc.RPCFramework;
import service.HelloService;
import service.impl.HelloServiceImpl;

public class Provider {
	public static void main(String[] args) throws Exception {
        HelloService service = new HelloServiceImpl();
        // RPC框架将服务暴露出来，供客户端消费
        RPCFramework.export(service, 1234);
    }
}
