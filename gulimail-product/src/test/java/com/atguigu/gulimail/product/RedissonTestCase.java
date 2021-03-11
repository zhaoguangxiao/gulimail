package com.atguigu.gulimail.product;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class RedissonTestCase {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Test
    public void assertNotNull() {
        Assert.assertNotNull(redissonClient);
    }

    /**
     * redisson 解决了2个问题
     * 1), 锁的自动续期,如果业务超长,它会在运行期间自动给锁续上新的时间,不用担心业务时间长,锁会被自动删掉 (阻塞式加锁,默认加的是30秒)
     * 2), 加锁的业务只要执行完成,就不会给当前锁续期,即使不手动解锁,锁也会在30s之后自动删除
     * <p>
     * 最佳实战
     * 1 lock.lock(10, TimeUnit.SECONDS) 省掉了一个续期的时间 ,手动解锁
     *
     * @throws InterruptedException
     */
    @Test
    public void redisTest() throws InterruptedException {
        //1 获取一把锁,只要锁的名字一样,就是同一把锁
        RLock lock = redissonClient.getLock("my-lock");
        //2 加锁
        lock.lock();

        //指定了超时时间,在锁的时间到期后不会自动续期
        //如果我们指定了超时时间,就会执行Luna脚本进行站锁,默认超时时间就是我们制定时间
        //如果我们没有指定超时间,就会使用默认的30000L毫秒,只要站锁成功,我们就会启动一个定时任务重新给锁设置过期时间,新的过期时间就是看门狗的默认事件30秒,30/3=3/1的续期时间,每隔10秒钟就会自动续期时间30秒
        //lock.lock(10, TimeUnit.SECONDS);
        // 尝试加锁，最多等待100秒，上锁以后10秒自动解锁
        //boolean res = lock.tryLock(100, 10, TimeUnit.SECONDS);
        try {
            log.info("加锁成功,执行业余---------------{}", Thread.currentThread().getId());
            TimeUnit.SECONDS.sleep(30);
        } finally {
            //解锁
            lock.unlock();
            log.info("释放锁成功{}", Thread.currentThread().getId());
        }
    }


    /**
     * ---ReadWriteLock
     * 读锁读取数据必须等写锁写完才能读取到数据
     * 保证一定能读到最新数据,修改期间,写锁是一个排它锁(互斥锁),读锁是一个共享锁
     * 写+读 :等待写锁释放,才进行读
     * 读+读 :共享,相当于无锁,只会在redis中记录好,所有当前的读锁,他们都会同时加锁成功
     * 读+写 :有读锁,,写锁也需要等待
     * 写+写 :阻塞方式
     * 结论: 只要有写的存在都必须等待
     */
    @Test
    public void writeLock() {
        RReadWriteLock writeLock = redissonClient.getReadWriteLock("wr-lock");
        writeLock.writeLock().lock();
        try {
            //业务代码
            TimeUnit.SECONDS.sleep(30);//睡眠30秒
            //redis 写入数据
            String uuid = IdUtil.simpleUUID();
            log.info("redis 写入的uuid 为 {}", uuid);
            stringRedisTemplate.opsForValue().set("uuid", uuid);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //手动释放锁
            writeLock.writeLock().unlock();
        }
    }

    @Test
    public void readLock() {
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("wr-lock");
        //加锁
        readWriteLock.readLock().lock();

        try {
            String uuid = stringRedisTemplate.opsForValue().get("uuid");
            log.info("读锁拿到结果为 : {}", uuid);
        } finally {
            //解锁
            readWriteLock.readLock().unlock();
        }
    }


    /**
     * 信号量 -- Semaphore
     * 停车方法
     */
    @Test
    public void parking() {
        RSemaphore semaphore = redissonClient.getSemaphore("semaphore");
        try {
            // 获取一个锁  如果信号量为0 会一直阻塞等待有新的停车位才能继续执行
            //semaphore.acquire();

            //尝试获取锁,如果当前信号量为0 直接返回false
            boolean acquire = semaphore.tryAcquire();
            log.info("信号量获一个锁已经执行acquire {}", acquire);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void going() {
        RSemaphore semaphore = redissonClient.getSemaphore("semaphore");
        semaphore.release();//释放一个锁
        log.info("信号量+1执行完成");
    }


    /**
     * 闭锁 --- CountDownLatch
     */
    @Test
    public void classesClose() {
        RCountDownLatch countDownLatch = redissonClient.getCountDownLatch("down");
        try {
            countDownLatch.trySetCount(10);//设置人数
            countDownLatch.await();//等待班级人走完才能关门
            log.info("班级关门了");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void classesMethod() {
        RCountDownLatch countDownLatch = redissonClient.getCountDownLatch("down");
        for (int i = 0; i < 10; i++) {
            log.info(i + "---学生已经走了");
            countDownLatch.countDown();//计数器减一
        }
        log.info("班级的学生走完了");
    }


}
