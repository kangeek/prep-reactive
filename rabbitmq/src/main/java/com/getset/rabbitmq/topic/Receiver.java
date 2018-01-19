package com.getset.rabbitmq.topic;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Receiver {
    private final static String EXCHANGE_NAME = "topic_logs";

    private String[] topics;
    private String name;

    public Receiver(String name, String... topics) {
        this.name = name;
        this.topics = topics;
    }

    public void receiveLogs() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        String queueName = channel.queueDeclare().getQueue();

        for (String routingKey : topics) {
            channel.queueBind(queueName, EXCHANGE_NAME, routingKey);
        }

        System.out.println(name + " [*] Waiting on queue " + queueName + " for messages. Listening for " + topics);
        channel.basicQos(1);

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
        Receiver logger1 = new Receiver("Logger1", "*.*.rabbit", "lazy.#");
        Receiver logger2 = new Receiver("Logger2", "*.orange.*");
        logger1.receiveLogs();
        logger2.receiveLogs();
    }
}
