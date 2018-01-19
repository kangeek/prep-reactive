package com.getset.rabbitmq.topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeoutException;

public class Publisher {
    private final static String EXCHANGE_NAME = "topic_logs";
    private final static String[] keys = new String[]{
            "quick.orange.rabbit",
            "lazy.orange.elephant",
            "quick.orange.fox",
            "lazy.brown.fox",
            "quick.brown.fox",
            "quick.orange.male.rabbit",
            "lazy.orange.male.rabbit"};
    private final static Random random = new Random();

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "topic");

        for (int i = 0; i < 10; i++) {
            String routingKey = keys[random.nextInt(3)];
            String message = ">>" + routingKey + "<< Hello, World! - " + i;
            channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes());
            System.out.println("EmitLog: '" + message + "'");
        }

        channel.close();
        connection.close();
    }
}
