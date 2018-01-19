package com.getset.rabbitmq.routing;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeoutException;

public class EmitLogs {
    private final static String EXCHANGE_NAME = "direct_logs";
    private final static String[] routingKeys = new String[]{"INFO", "WARN", "ERROR"};
    private final static Random random = new Random();

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "direct");

        for (int i = 0; i < 10; i++) {
            String routingKey = routingKeys[random.nextInt(3)];
            String message = "[" + routingKey + "] Hello, World! - " + i;
            channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes());
            System.out.println("EmitLog: '" + message + "'");
        }

        channel.close();
        connection.close();
    }
}
