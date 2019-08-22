package com.xuecheng.test.rabbitmq;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class Producer_springboot {

    @Autowired
    RabbitTemplate rabbitTemplate;

 @Test
    public void test01(){

     String message = "send email to user";

      rabbitTemplate.convertAndSend(RabbitmqConfig.EXCHANGE_TOPICS_INFORM,"inform.email",message);
 }

}
