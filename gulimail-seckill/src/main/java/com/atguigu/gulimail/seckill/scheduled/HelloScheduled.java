package com.atguigu.gulimail.seckill.scheduled;


import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/** springBoot支持异步任务
 *
 */
@Slf4j
@Component
public class HelloScheduled {


    /** spring cron 与 quarz 区别
     * 1 在spring 的cron 表达式中只允许6位 不允许第7位的年
     * 2 在周几位置,1-7代表周一-周日
     * 3 定时任务不应该阻塞,默认是阻塞的 解决办法 1),可以让业务运行以异步(CompleableFuture.runAsync)的方式,提交到线程池
     *                                      2), 设置 spring.task.scheduling.size=? (有时不太好使)
     *                                      3),让定时任务异步执行  EnableAsync 给希望异步执行的方法上标注 Async
     *
     *  最终解决了异步任务不阻塞
     */
    @Async
    @Scheduled(cron = "* * * * * 3")
    public void hello() throws InterruptedException {
        Thread.sleep(3000);
        log.info("hello word");
    }

}
