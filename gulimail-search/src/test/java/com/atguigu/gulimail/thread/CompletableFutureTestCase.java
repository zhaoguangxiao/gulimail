package com.atguigu.gulimail.thread;

import com.atguigu.gulimail.elasticsearch.GulimailElasticsearchMainApplication12000;
import lombok.extern.slf4j.Slf4j;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.*;

@Slf4j
@SpringBootTest(classes = GulimailElasticsearchMainApplication12000.class)
@RunWith(SpringRunner.class)
public class CompletableFutureTestCase {
    static ExecutorService executorService = Executors.newFixedThreadPool(10);


    public static void main(String[] args) throws ExecutionException, InterruptedException {

        log.info("main 开始");
        CompletableFuture<Integer> runAsync1 = CompletableFuture.supplyAsync(() -> {
            log.info("任务1 this thread id= {}", Thread.currentThread().getId());
            //int a = 10 / 0;
            return 100;
        }, executorService);

        CompletableFuture<Integer> runAsync2 = CompletableFuture.supplyAsync(() -> {

            log.info("任务2 this thread id= {}", Thread.currentThread().getId());
            return 200;
        }, executorService);

        CompletableFuture<Integer> either = runAsync1.applyToEither(runAsync2, res -> {
            log.info("任务3 res={}", res);
            return res * 2;
        });


        log.info("main 结束");
        Object o = either.get();
        System.out.println("结果为"+o.toString());
    }


}
