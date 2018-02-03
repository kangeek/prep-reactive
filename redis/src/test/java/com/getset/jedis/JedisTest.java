package com.getset.jedis;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.*;

import java.util.concurrent.TimeUnit;

public class JedisTest {
    private Jedis jedis;
    private JedisPool jedisPool;

    @Before
    public void conn() {
        // 1. 连接池配置
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMinIdle(10);
        jedisPoolConfig.setMaxTotal(30);
        // 2. 获取连接池
        jedisPool = new JedisPool(jedisPoolConfig, "localhost", 6379);
        // 3. 获取连接
        jedis = jedisPool.getResource();
    }

    @Test
    public void jedisStringTest() {

        // 字符串设置与获取
        jedis.set("name", "zhangsan");
        System.out.println(jedis.get("name"));
        System.out.println(jedis.getSet("name", "lisi"));
        System.out.println(jedis.get("name"));
        jedis.append("name", ", hello");
        System.out.println(jedis.get("name"));
        jedis.del("name");
        System.out.println(jedis.get("name"));

        // 字符串累加
        jedis.set("a", "1");
        jedis.incr("a");
        System.out.println(jedis.get("a"));
        jedis.incr("b");
        System.out.println(jedis.get("b"));
        jedis.decrBy("b", 5);
        System.out.println(jedis.get("b"));
    }

    @Test
    public void jedisHashSetTest() {
        jedis.hset("user1", "name", "zhangsan");
        jedis.hset("user1", "age", "12");
        jedis.hset("user1", "school", "shiyan");
        System.out.println(jedis.hget("user1", "name"));
        System.out.println(jedis.hgetAll("user1"));
        jedis.hdel("user1", "school");
        System.out.println(jedis.hgetAll("user1"));
        jedis.hincrBy("user1", "age", 1);
        System.out.println(jedis.hgetAll("user1"));

        System.out.println(jedis.hexists("user1", "name"));
        System.out.println(jedis.hexists("user1", "school"));

        System.out.println(jedis.hkeys("user1"));

        System.out.println(jedis.hlen("user1"));
    }

    @Test
    public void jedisListTest() {
        jedis.del("list1");
        jedis.del("list2");

        // 两端添加与弹出
        jedis.rpush("list1", "a", "b", "c");
        jedis.lpush("list1", "d");
        jedis.rpush("list1", "e");
        System.out.println(jedis.lrange("list1", 0, -1));
        System.out.println(jedis.lrange("list1", 1, -2));
        System.out.println(jedis.lpop("list1"));
        System.out.println(jedis.rpop("list1"));
        jedis.rpushx("list1", "f");
        jedis.rpushx("list2", "f");
        System.out.println(jedis.lrange("list1", 0, -1));
        System.out.println(jedis.lrange("list2", 0, -1));

        // 删除元素
        jedis.rpush("list1", "a", "b", "c", "b", "c", "d");
        System.out.println(jedis.lrem("list1", 2, "b"));
        System.out.println(jedis.lrange("list1", 0, -1));
        System.out.println(jedis.lrem("list1", 0, "c"));
        System.out.println(jedis.lrange("list1", 0, -1));

        // 设置与插入
        jedis.lset("list1", 2, "set");
        jedis.linsert("list1", BinaryClient.LIST_POSITION.BEFORE, "b", "insert-before-b");
        System.out.println(jedis.lrange("list1", 0, -1));


        // 消息队列
        jedis.rpush("producer", "a", "b", "c");
        jedis.rpush("backup", "d", "e");
        jedis.rpoplpush("producer", "backup");
        System.out.println(jedis.lrange("producer", 0, -1));
        System.out.println(jedis.lrange("backup", 0, -1));
    }

    @Test
    public void jedisSetTest() {
        // 添加删除
        jedis.sadd("set1", "a", "b", "c", "d");
        jedis.sadd("set1", "b");
        jedis.srem("set1", "c");
        System.out.println("set1:" + jedis.smembers("set1"));

        // 查询
        System.out.println(jedis.sismember("set1", "a"));
        System.out.println(jedis.sismember("set1", "c"));

        // 差集，交集与并集
        jedis.sadd("set2", "b", "c", "d", "e");
        System.out.println("set2:" + jedis.smembers("set2"));

        System.out.println(jedis.sdiff("set1", "set2"));
        System.out.println(jedis.sdiff("set2", "set1"));
        System.out.println(jedis.sdiffstore("set1diffset2", "set1", "set2"));
        System.out.println(jedis.smembers("set1-diff-set2"));

        System.out.println(jedis.sinter("set1", "set2"));
        System.out.println(jedis.sinterstore("set1-inter-set2", "set1", "set2"));

        System.out.println(jedis.sunion("set1", "set2"));
        System.out.println(jedis.sunionstore("set1-union-set2", "set1", "set2"));

    }

    @Test
    public void jedisSortedSetTest() {
        jedis.zadd("scores", 70, "zhangsan");
        jedis.zadd("scores", 92, "lisi");
        jedis.zadd("scores", 85, "wangwu");
        jedis.zadd("scores", 87, "zhangsan");
        jedis.zadd("scores", 66, "xiongda");
        System.out.println(jedis.zrange("scores", 0, -1));
        jedis.zrem("scores", "zhangsan");
        System.out.println(jedis.zscore("scores", "lisi"));
        System.out.println(jedis.zscore("scores", "zhangsan"));
        System.out.println(jedis.zcard("scores"));


        jedis.zadd("scores", 72, "tom");
        jedis.zadd("scores", 94, "jack");
        jedis.zadd("scores", 87, "jim");
        jedis.zadd("scores", 89, "mike");
        jedis.zadd("scores", 68, "kite");
        System.out.println(jedis.zrange("scores", 0, -1));
        System.out.println(jedis.zrevrange("scores", 0, -1));
        System.out.println(jedis.zrangeByScore("scores", 70, 90));
        System.out.println(jedis.zrangeWithScores("scores", 0, -1));

        System.out.println(jedis.zscore("scores", "jim"));
        System.out.println(jedis.zcount("scores", 80, 90));
    }

    @Test
    public void jedisKeysTest() throws InterruptedException {
        System.out.println(jedis.keys("*"));
        System.out.println(jedis.keys("b*"));
        System.out.println(jedis.exists("backup"));
//        System.out.println(jedis.rename("backup", "bk-queue"));
        System.out.println(jedis.keys("b*"));

        jedis.set("key-with-timeout", "zhangsan");
        jedis.expire("key-with-timeout", 2);
        System.out.println(jedis.exists("key-with-timeout"));
        TimeUnit.SECONDS.sleep(1);
        System.out.println(jedis.ttl("key-with-timeout"));
        TimeUnit.SECONDS.sleep(2);
        System.out.println(jedis.exists("key-with-timeout"));

        System.out.println(jedis.type("scores"));

    }

    @Test
    public void jedisDBTest() {
        // Redis 有 16 个数据库，通过 select index 进行切换
        jedis.select(1);
        System.out.println(jedis.keys("*"));
        jedis.select(0);
        System.out.println(jedis.keys("*"));
        jedis.move("scores", 1);
        jedis.select(1);
        System.out.println(jedis.keys("*"));
        jedis.move("scores", 2);
    }

    @Test
    public void jedisTransactionTest() {
        jedis.select(2);
        Transaction transaction = jedis.multi();
        transaction.lpush("list1", "a", "b", "c");
        transaction.zadd("set1", 10, "hello");
        transaction.exec();
        System.out.println(jedis.exists("list1"));
        System.out.println(jedis.exists("set1"));

        transaction = jedis.multi();
        transaction.lpush("list2", "a", "b", "c");
        transaction.zadd("set2", 10, "hello");
        transaction.discard();
        System.out.println(jedis.exists("list2"));
        System.out.println(jedis.exists("set2"));
    }

    @After
    public void close() {
        jedis.close();
        jedisPool.close();
    }
}
