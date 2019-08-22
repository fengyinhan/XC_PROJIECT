package com.xuecheng.test.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ReciverHandler {


    @RabbitListener(queues = RabbitmqConfig.QUEUE_INFORM_EMAIL)
    public void send_msg(String msg){
        System.out.println(msg);
    }
}
