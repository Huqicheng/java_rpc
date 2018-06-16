package rpc;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class RPCFramework {
    /**
     * export services to the indicated port
     *
     * @param service implementation of Service
     * @param port    
     * @throws Exception
     */
    public static void export(final Object service, int port) throws Exception {
        if (service == null) {
            throw new IllegalArgumentException("service instance == null");
        }
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("Invalid port " + port);
        }
        System.out.println("Export service " + service.getClass().getName() + " on port " + port);
        // create a server
        ServerSocket server = new ServerSocket(port);
        for (; ; ) {
            try {
                // listening port to get requests
                final Socket socket = server.accept();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            try {
                                /* Server decodes the request*/
                                ObjectInputStream input = new ObjectInputStream(
                                    socket.getInputStream());
                                try {

                                    System.out.println("\nServer get request： ");
                                    String methodName = input.readUTF();
                                    System.out.println("methodName : " + methodName);
                                    Class<?>[] parameterTypes = (Class<?>[])input.readObject();
                                    System.out.println(
                                        "parameterTypes : " + Arrays.toString(parameterTypes));
                                    Object[] arguments = (Object[])input.readObject();
                                    System.out.println("arguments : " + Arrays.toString(arguments));


                                    /* Server handles the request*/
                                    ObjectOutputStream output = new ObjectOutputStream(
                                        socket.getOutputStream());
                                    try {
                                        // Reflect
                                        Method method = service.getClass().getMethod(methodName,
                                            parameterTypes);
                                        Object result = method.invoke(service, arguments);
                                        System.out.println("\nServer response ：");
                                        System.out.println("result : " + result);
                                        output.writeObject(result);
                                    } catch (Throwable t) {
                                        output.writeObject(t);
                                    } finally {
                                        output.close();
                                    }
                                } finally {
                                    input.close();
                                }
                            } finally {
                                socket.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * reference the service
     *
     * @param <T>            interface
     * @param interfaceClass interface type
     * @param host           host name
     * @param port           host port
     * @return Service
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static <T> T refer(final Class<T> interfaceClass, final String host, final int port)
        throws Exception {
        if (interfaceClass == null) {
            throw new IllegalArgumentException("Interface class == null");
        }
        // Proxy of interface
        if (!interfaceClass.isInterface()) {
            throw new IllegalArgumentException(
                "The " + interfaceClass.getName() + " must be interface class!");
        }
        if (host == null || host.length() == 0) {
            throw new IllegalArgumentException("Host == null!");
        }
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("Invalid port " + port);
        }
        System.out.println(
            "Get remote service " + interfaceClass.getName() + " from server " + host + ":" + port);

        // JDK proxy
        T proxy = (T)Proxy.newProxyInstance(interfaceClass.getClassLoader(),
            new Class<?>[] {interfaceClass}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] arguments)
                    throws Throwable {
                    // create a socket client
                    Socket socket = new Socket(host, port);
                    try {
                        ObjectOutputStream output = new ObjectOutputStream(
                            socket.getOutputStream());
                        try {
                            // send requests
                            System.out.println("\nClient send the request ： ");
                            output.writeUTF(method.getName());
                            System.out.println("methodName : " + method.getName());
                            output.writeObject(method.getParameterTypes());
                            System.out.println("parameterTypes : " + Arrays.toString(method
                                .getParameterTypes()));
                            output.writeObject(arguments);
                            System.out.println("arguments : " + Arrays.toString(arguments));


                            /* get response of the server*/
                            ObjectInputStream input = new ObjectInputStream(
                                socket.getInputStream());
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
        return proxy;
    }
}
