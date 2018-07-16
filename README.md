java_rpc
------------------------------------------------
Implementation of an RPC (Remote Procedure Call Protocal) Framework.


## What is Remote Procedure Call Protocal?
In distributed computing, a remote procedure call (RPC) is when a computer program causes a procedure (subroutine) to execute in a different address space (commonly on another computer on a shared network), which is coded as if it were a normal (local) procedure call, without the programmer explicitly coding the details for the remote interaction. [1, wikipedia]

## Required Components
    java.net.ServerSocket
    java.net.Socket
    java.lang.reflect.InvocationHandler
    java.lang.reflect.Proxy
  
## How to export a service to providers?

```java
ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
try {
    System.out.println("\nServer get request： ");
    // which method did the provider call?
    String methodName = input.readUTF();
    System.out.println("methodName : " + methodName);
    Class<?>[] parameterTypes = (Class<?>[])input.readObject();
    
    System.out.println("parameterTypes : " + Arrays.toString(parameterTypes));
    
    Object[] arguments = (Object[])input.readObject();
    System.out.println("arguments : " + Arrays.toString(arguments));


    /* Server handles the request*/
    ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
    try {
        // Reflect
        Method method = service.getClass().getMethod(methodName,parameterTypes);
        Object result = method.invoke(service, arguments);
        
        System.out.println("\nServer response ：");
        System.out.println("result : " + result);
        
        output.writeObject(result);
    } catch (Throwable t) {
        output.writeObject(t);
    } finally {
        output.close();
}
```

