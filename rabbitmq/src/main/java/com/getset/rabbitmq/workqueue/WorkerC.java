package com.getset.rabbitmq.workqueue;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class WorkerC {
    protected final static String QUEUE_NAME = "task_queue";
    protected final static Random random = new Random();
    private String name;

    public WorkerC(String name) {
        this.name = name;
    }

    public void receiveWork() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, true, false,false,null);
        System.out.println(name + " [*] Waiting for messages. To exit press CTRL+C.");
        // 每次从队列中获取数量
        channel.basicQos(1);

        final Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(name + " [x] Received '" + message + "'");
                try{
                    doWork();
                } finally {
                    // 消息处理完成的确认
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            }
        };

        channel.basicConsume(QUEUE_NAME, false, consumer);
    }

    private void doWork() {
        try {
            TimeUnit.MILLISECONDS.sleep(random.nextInt(2000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println(name + " [x] Done");

        }
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        WorkerC worker1 = new WorkerC("Worker1");
        WorkerC worker2 = new WorkerC("Worker2");
        worker1.receiveWork();
        worker2.receiveWork();
    }
}
