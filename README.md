Java RPC
------------------------------------------------
Implementation of an RPC (Remote Procedure Call Protocal) Framework.


## What is Remote Procedure Call Protocal?
In distributed computing, a remote procedure call (RPC) is when a computer program causes a procedure (subroutine) to execute in a different address space (commonly on another computer on a shared network), which is coded as if it were a normal (local) procedure call, without the programmer explicitly coding the details for the remote interaction. [1, wikipedia]

## Applications
[Apache Dubbo™ (incubating) is a high-performance, java based open source RPC framework.](https://dubbo.incubator.apache.org/#!/?lang=en-us)

## Required Components
    java.net.ServerSocket
    java.net.Socket
    java.lang.reflect.InvocationHandler
    java.lang.reflect.Proxy
  
## Export a Service by the Provider

```java
ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
try {
    System.out.println("\nServer get request： ");
    // which method did the consumer call?
    String methodName = input.readUTF();
    System.out.println("methodName : " + methodName);
    
    // what's the parameter types for this calling?
    Class<?>[] parameterTypes = (Class<?>[])input.readObject();
    
    System.out.println("parameterTypes : " + Arrays.toString(parameterTypes));
    
    // what's the values of each parameter?
    Object[] arguments = (Object[])input.readObject();
    
    System.out.println("arguments : " + Arrays.toString(arguments));


    /* Server handles the request*/
    ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
    try {
        // get the method from Service with the methodName and parameterTypes given by the consumer
        Method method = service.getClass().getMethod(methodName,parameterTypes);
        
        // invoke the method, and get the output
        Object result = method.invoke(service, arguments);
        
        System.out.println("\nServer response ：");
        System.out.println("result : " + result);
        
        // respond to the consumer
        output.writeObject(result);
    } catch (Throwable t) {
        output.writeObject(t);
    } finally {
        output.close();
}
```

## Refer the Service by the Consumer

```java
// JDK proxy (return an invokable instance of the interfaceClass)
T proxy = (T)Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[] {interfaceClass}, 
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {
                        // create a socket client
                        Socket socket = new Socket(host, port);
                        try {
                            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                            try {
                                // Send Requests
                                System.out.println("\nClient send the request ： ");
                                
                                // Write method name
                                output.writeUTF(method.getName());
                                System.out.println("methodName : " + method.getName());
                                
                                // write parameterTYpes
                                output.writeObject(method.getParameterTypes());
                                System.out.println("parameterTypes : " + Arrays.toString(method.getParameterTypes()));
                                
                                // write arguments
                                output.writeObject(arguments);
                                System.out.println("arguments : " + Arrays.toString(arguments));

                                /* get response of the server*/
                                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                                try {
                                    Object result = input.readObject();
                                    if (result instanceof Throwable) {
                                        throw (Throwable)result;
                                    }
                                    System.out.println("\nClient recieves the reponse ： ");
                                    System.out.println("result : " + result);
                                    return result;
                                } finally {
                                    input.close();
                                }
                            } finally {
                                   output.close();
                            }
                    } finally {
                        socket.close();
                    }
                }
            });
```


## References
1. [Wikipedia](https://en.wikipedia.org/wiki/Remote_procedure_call)
