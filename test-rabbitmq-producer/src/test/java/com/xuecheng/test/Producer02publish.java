package com.xuecheng.test;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


public class Producer02publish {
    //队列名称
    private static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
    private static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    //交换机名称
    private static final String EXCHANGE_FANOUT_INFORM="exchange_fanout_inform";

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
            channel.queueDeclare(QUEUE_INFORM_EMAIL,true,false,false,null);
            channel.queueDeclare(QUEUE_INFORM_SMS,true,false,false,null);

            /**
             * 参数一 exchange  交换机  如果不指定就用默认的mq交换机  ""
             * 参数二 routingKey  路由key
             * 参数三  props  消息的属性
             * 参数四   body   消息的内容
             */

            //声明一个交换机
            channel.exchangeDeclare(EXCHANGE_FANOUT_INFORM, BuiltinExchangeType.FANOUT);
            //进行交换机和队列的绑定
            channel.queueBind(QUEUE_INFORM_EMAIL,EXCHANGE_FANOUT_INFORM,"");
            channel.queueBind(QUEUE_INFORM_SMS,EXCHANGE_FANOUT_INFORM,"");


            //发送信息
            for(int i=0;i<5;i++){
                //消息内容
                String message = "send inform message to user";
                channel.basicPublish(EXCHANGE_FANOUT_INFORM,"",null,message.getBytes());
                System.out.println("send to mq "+message);
            }
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
