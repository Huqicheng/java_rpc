package service.impl;

import service.HelloService;

public class HelloServiceImpl implements HelloService{

	@Override
	public String hello(String name) {
		
		return "Hello " + name;
	}

}
