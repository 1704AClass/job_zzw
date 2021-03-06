package com.ningmeng.producer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

public class Producer01 {
    //队列名称
    private static final String QUEUE= "helloworld";

    public static void main(String[] args) throws IOException, TimeoutException {
       try {
           //创建初始化连接工厂
           ConnectionFactory factory=new ConnectionFactory();
           factory.setHost("127.0.0.1");
           //浏览器使用端口15672，后端使用端口5672
           factory.setPort(5672);
           factory.setUsername("guest");
           factory.setPassword("guest");
           //rabbitmq默认虚拟机名称为“/”，虚拟机相当于一个独立的mq服务
           factory.setVirtualHost("/");
            //创建连接
           Connection connection=factory.newConnection();
           //创建与Exchange的通道，每个连接可以创建多个通道，每个通道代表一个会话任务
           Channel channel = connection.createChannel();
           /**
            * 声明队列，如果Rabbit中没有此队列将自动创建
            * param1:队列名称
            * param2:是否持久化
            * param3:队列是否独占此连接
            * param4:队列不再使用时是否自动删除此队列
            * param5:队列参数
            */
           channel.queueDeclare(QUEUE, true, false, false, null);
           String message = "helloworld小明"+System.currentTimeMillis();
           /**
            * 消息发布方法
            * param1：Exchange的名称，如果没有指定，则使用Default Exchange
            * param2:routingKey,消息的路由Key，是用于Exchange（交换机）将消息转发到指定的消息队列
            * param3:消息包含的属性
            * param4：消息体
            *
            * 这里没有指定交换机，消息将发送给默认交换机，每个队列也会绑定那个默认的交换机，但是不能显
            示绑定或解除绑定
            * 默认的交换机，routingKey等于队列名称
            */
           String mange="小明你好";
           System.out.println("send:"+mange+",时间:"+new Date());
           channel.basicPublish("", QUEUE, null, message.getBytes());
       }catch (Exception e){
           e.printStackTrace();
       }
    }
}
