import util.StringUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args){
        try {
            ServerSocket serverSocket = new ServerSocket(8080);

            //使用线程池
            Executor threadPool = Executors.newFixedThreadPool(50);
            System.out.println("等待请求，开始阻塞");

            while(true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("收到请求，创建一个线程来处理请求");
                threadPool.execute(new HandleRequestThread(clientSocket));
            }
        }
        catch (IOException e){
            System.out.println("I/O错误");
            e.printStackTrace();
        }
    }

    //每个请求对应一个线程
    private static class HandleRequestThread extends Thread{

        Socket clientSocket;

        HandleRequestThread(Socket clientSocket){
            this.clientSocket = clientSocket;
        }

        @Override
        public void run(){
            try {
                /* 读取网络I/O内容 */
                InputStream inputStream = clientSocket.getInputStream();

                //java.io的装饰器模式，再外面套一层，来实现字节流转字符流的功能
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                //开始读取请求内容
                String requestContent = "";
                while (!StringUtil.isEmpty(requestContent = reader.readLine())) {
                    System.out.println(requestContent);
                }
                System.out.println("请求处理完毕，关闭socket");
                clientSocket.close();
            }
            catch (IOException e){
                System.out.println("I/O错误");
                e.printStackTrace();
            }
        }


    }

}
