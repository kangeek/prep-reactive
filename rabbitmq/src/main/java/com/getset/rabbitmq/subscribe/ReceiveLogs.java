package com.getset.rabbitmq.subscribe;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ReceiveLogs {
    private final static String EXCHANGE_NAME = "logs";

    private String name;

    public ReceiveLogs(String name) {
        this.name = name;
    }

    public void receiveLogs() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, "");

        System.out.println(name + " [*] Waiting on queue " + queueName + " for messages. To exit press CTRL+C");
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
        ReceiveLogs logger1 = new ReceiveLogs("Logger1");
        ReceiveLogs logger2 = new ReceiveLogs("Logger2");
        logger1.receiveLogs();
        logger2.receiveLogs();
    }
}
