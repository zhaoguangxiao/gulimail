package com.atguigu.gulimail.thread;

import com.atguigu.gulimail.elasticsearch.GulimailElasticsearchMainApplication12000;
import lombok.extern.slf4j.Slf4j;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.*;

/**
 *  面试题: 一个线程池,常驻线程数量为7,最大线程数量是20,阻塞队列为50 100的并发进来时怎么分配的
 *      7个会立即执行
 *      50个进行阻塞队列
 *      然后在开启13个线程数量 来进行执行
 *      剩下30使用拒绝策略进行执行
 *      如果不想抛弃可以使用这个拒绝策略 CallerRunsPolicy
 */
@Slf4j
@SpringBootTest(classes = GulimailElasticsearchMainApplication12000.class)
@RunWith(SpringRunner.class)
public class CreateThreadTestCase {

    static ExecutorService executorService = Executors.newFixedThreadPool(10);


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        log.info("--------第1种方式 extentds thread类----------");
        log.info("main 开始了");
        Thead01 thead01 = new Thead01();
        thead01.start();
        log.info("main 结束了");
        log.info("--------第2种方式 实现 runable类----------");
        log.info("main 开始了");
        Thread02 thread02 = new Thread02();
        new Thread(thread02).start();
        log.info("main 结束了");
        log.info("--------第3种方式 实现 callable接口---- futureTask------");

        FutureTask<Integer> task = new FutureTask<Integer>(new Thread03());
        Thread thread = new Thread(task);
        thread.start();
        Integer integer = task.get();
        log.info(integer.toString());
        log.info("--------第3种方式 线程池------");

        //可以控制资源,性能稳定
        //我们以后再业务代码块里面,以上三种会导致我们的资源泄漏,以后的异步任务都交给线程池直接执行
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                log.info("线程池创建");
            }
        });



    }

    public static class Thead01 extends Thread {
        @Override
        public void run() {
            log.info("this method thread id : {}", Thread.currentThread().getId());
        }
    }

    public static class Thread02 implements Runnable {

        @Override
        public void run() {
            log.info("this method name {}", Thread.currentThread().getName());
        }
    }

    public static class Thread03 implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            return 1024;
        }
    }
}
