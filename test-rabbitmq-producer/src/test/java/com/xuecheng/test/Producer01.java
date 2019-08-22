package com.xuecheng.test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;



public class Producer01 {
    //队列名称
    private static  final String QUEUE = "hello world";

    public static void main(String[] args) {
       //通过连接工厂创建新的连接和mq建立连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);//端口
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        //设置虚拟机,一个mq服务可以设置多个虚拟机,每个虚拟机就想当于一个独立的mq
        connectionFactory.setVirtualHost("/");

        Connection connection = null;
        Channel channel = null;

        //建立连接
        try {
            connection = connectionFactory.newConnection();
            //创建回话通道,生产者和mq服务所有通信都在channel通道中完成
            channel = connection.createChannel();
            /**
             * 参数一   队列名称
             * 参数二  是否持久化
             * 参数三  是否独占连接
             * 参数四  自动删除
             * 参数五   参数     比如存活时间
             */
            channel.queueDeclare(QUEUE,true,false,false,null);

            /**
             * 参数一 exchange  交换机  如果不指定就用默认的mq交换机  ""
             * 参数二 routingKey  路由key
             * 参数三  props  消息的属性
             * 参数四   body   消息的内容
             */
            //发送信息
            String message = "hello rabbit";
            channel.basicPublish("",QUEUE,null,message.getBytes());
            System.out.println("send to mq "+message);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                channel.close();   //先关闭通道
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            try {
                connection.close(); //关闭连接
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
