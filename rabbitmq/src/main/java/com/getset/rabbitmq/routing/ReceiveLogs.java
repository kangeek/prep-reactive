package com.getset.rabbitmq.routing;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ReceiveLogs {
    private final static String EXCHANGE_NAME = "direct_logs";
    private final static String[] routingKeys = new String[]{"INFO", "WARN", "ERROR"};

    private String[] listeningKeys;
    private String name;

    public ReceiveLogs(String name, String... listeningKeys) {
        this.name = name;
        this.listeningKeys = listeningKeys;
    }

    public void receiveLogs() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "direct");
        String queueName = channel.queueDeclare().getQueue();

        for (String routingKey : listeningKeys) {
            channel.queueBind(queueName, EXCHANGE_NAME, routingKey);
        }

        System.out.println(name + " [*] Waiting on queue " + queueName + " for messages. Listening for " + listeningKeys);
        channel.basicQos(2);

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body);
                System.out.println(name + " [x] Received '" + message + "'");
            }
        };

        channel.basicConsume(queueName, true, consumer);
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        ReceiveLogs logger1 = new ReceiveLogs("Logger1", "ERROR");
        ReceiveLogs logger2 = new ReceiveLogs("Logger2", "INFO", "WARN", "ERROR");
        logger1.receiveLogs();
        logger2.receiveLogs();
    }
}
