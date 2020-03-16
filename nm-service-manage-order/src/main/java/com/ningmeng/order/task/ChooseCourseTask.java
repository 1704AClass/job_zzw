package com.ningmeng.order.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by wangb on 2020/3/15.
 */
@Component
public class ChooseCourseTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChooseCourseTask.class);
    // @Scheduled(fixedRate = 5000) //上次执行开始时间后5秒执行
    // @Scheduled(fixedDelay = 5000) //上次执行完毕后5秒执行
    // @Scheduled(initialDelay=3000, fixedRate=5000) //第一次延迟3秒，以后每隔5秒执行一次
    //6 7 个  多了个年  秒-分-时-日-月-周-年
    @Scheduled(cron="0/3 * * * * ?")//每隔3秒执行一次
    public void task1(){
        System.out.println(new Date());
        /*LOGGER.info("===============测试定时任务1开始===============");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info("===============测试定时任务1结束===============");*/
    }
}
