package com.ningmeng.manage_cms_client.mq;

import com.alibaba.fastjson.JSON;
import com.ningmeng.manage_cms_client.service.CmsPageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 先被spring管理
 */
@Component
public class ConsumerPostPage {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerPostPage.class);

    @Autowired
    private CmsPageService pageService;


    /**
     * String 是Json字符串(传递更多的信息、方便拓展、有同意规则、简单)
     * {
     *     id:"1"
     * }
     * RoutingKey
     * 页面Id:发布页面的id
     * @param msg
     * 在没有通知之前 生产者已经先有了静态页面，
     * 这个静态页面字mongodb数据库中存在，再通知消费者消费
     */


    @RabbitListener(queues={"${ningmeng.mq.queue}"})
    public void postPage(String msg){
        //消费者应该做什么？
        //获得页面id从mongodb数据库下载页面到本地
        // (1)得到静态页面
        //调用dao查询页面信息，获取到页面的物理路径，调用dao查询站点信息，得到站点的物理路径
        // (2)所属站点的物理路径和页面的物理路径和页面名称
        //页面物理路径=站点物理路径+页面物理路径+页面名称。
        //从GridFS查询静态文件内容，将静态文件内容保存到页面物理路径下。


        //解析消息
        Map map = JSON.parseObject(msg, Map.class);
        LOGGER.info("receive cms post page:{}",msg.toString());
        //取出页面id
        String pageId = (String) map.get("pageId");
        //查询页面信息
        //将页面保存到服务器物理路径
        pageService.TestRabbit((String) map.get("pageId"));
    }

}
