package com.ningmeng.manage_cms.service;

import com.alibaba.fastjson.JSON;
import com.ningmeng.framework.domain.cms.CmsPage;
import com.ningmeng.framework.domain.cms.request.QueryPageRequest;
import com.ningmeng.framework.domain.cms.response.CmsCode;
import com.ningmeng.framework.domain.cms.response.CmsPageResult;
import com.ningmeng.framework.exception.ExceptionCast;
import com.ningmeng.framework.model.response.CommonCode;
import com.ningmeng.framework.model.response.QueryResponseResult;
import com.ningmeng.framework.model.response.QueryResult;
import com.ningmeng.framework.model.response.ResponseResult;
import com.ningmeng.manage_cms.config.RabbitmqConfig;
import com.ningmeng.manage_cms.dao.CmsPageRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Lenovo on 2020/2/11.
 */
@Service
public class PageService {
    @Autowired
    CmsPageRepository cmsPageRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    //发布页面方法
    public ResponseResult postPage(String pageId){
        boolean flag = creatHtml();
        if(!flag){
            ExceptionCast.cast(CommonCode.FAIL);
        }

        //查询数据库
        CmsPage cmsPage = this.getById(pageId);
        if(cmsPage == null){
            ExceptionCast.cast(CommonCode.FAIL);
        }

        Map<String,String> msgMap = new HashMap<>();
        msgMap.put("pageId",pageId);

        //消息内容
        String msg = JSON.toJSONString(msgMap);
        //获取站点id作为routingKey
        String siteId = cmsPage.getSiteId();

        //发送json{pageId:"1"} sitId就是RoutingKey
        rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE,siteId,msg);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //创建静态页面
    private boolean creatHtml(){
        System.out.println("执行页面静态化程序，保存静态化文件完成。。。。。。");
        //成功
        return true;
    }

    //删除
    public ResponseResult delete(String id){
        CmsPage page = this.getById(id);
        if(page!=null){
            cmsPageRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }


    //根据id查询页面
    public CmsPage getById(String id){
        Optional<CmsPage> optional = cmsPageRepository.findById(id);
        if(optional.isPresent()){
            return optional.get();
        }
    //返回空
        return null;
    }

    //更新页面信息
    public CmsPageResult update(String id, CmsPage cmsPage) {
    //根据id查询页面信息
        CmsPage page = this.getById(id);
        if (page != null) {
            page.setTemplateId(cmsPage.getTemplateId());
            page.setSiteId(cmsPage.getSiteId());
            page.setPageAliase(cmsPage.getPageAliase());
            page.setPageName(cmsPage.getPageName());
            page.setPageWebPath(cmsPage.getPageWebPath());
            page.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            CmsPage save = cmsPageRepository.save(page);
            if (save != null) {
    //返回成功
                CmsPageResult cmsPageResult = new CmsPageResult(CommonCode.SUCCESS, save);
                return cmsPageResult;
            }
        }
    //返回失败
        return new CmsPageResult(CommonCode.FAIL,null);
    }


    //添加页面
    public CmsPageResult add(CmsPage cmsPage){
    //校验cmsPage是否为空
        if(cmsPage == null){
    //抛出异常，非法请求
            ExceptionCast.cast(CommonCode.FAIL);
        }
    //根据页面名称查询（页面名称已在mongodb创建了唯一索引）
        CmsPage cmsPage1 = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
    //校验页面是否存在，已存在则抛出异常
        if(cmsPage1 !=null){
    //抛出异常，已存在相同的页面名称
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }
        cmsPage.setPageId(null);//添加页面主键由spring data 自动生成
        CmsPage save = cmsPageRepository.save(cmsPage);
    //返回结果
        CmsPageResult cmsPageResult = new CmsPageResult(CommonCode.SUCCESS,save);
        return cmsPageResult;
    }

    /*public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest) {
        if (queryPageRequest == null) {
            queryPageRequest = new QueryPageRequest();
        }
        if (page <= 0) {
            page = 1;
        }
        page = page - 1;
        if (size <= 0) {
            size = 20;
        }
        return null;
    }*/

    /**
     * 页面列表分页查询
    * @param page 当前页码
    * @param size 页面显示个数
     * @param queryPageRequest 查询条件
     * @return 页
     *
     * */
    public QueryResponseResult findList(int page,int size,QueryPageRequest queryPageRequest){
    //条件匹配器
    //页面名称模糊查询，需要自定义字符串的匹配器实现模糊查询
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
    //条件值
        CmsPage cmsPage = new CmsPage();
    //站点ID
        if(StringUtils.isNotEmpty(queryPageRequest.getSiteId())){
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }
    //页面别名
        if(StringUtils.isNotEmpty(queryPageRequest.getPageAliase())){
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }
    //创建条件实例
        Example<CmsPage> example = Example.of(cmsPage, exampleMatcher);
    //页码
        page = page-1;
    //分页对象
        Pageable pageable = new PageRequest(page, size);
    //分页查询
        Page<CmsPage> all = cmsPageRepository.findAll(example,pageable);
        QueryResult<CmsPage> cmsPageQueryResult = new QueryResult<CmsPage>();
        cmsPageQueryResult.setList(all.getContent());
        cmsPageQueryResult.setTotal(all.getTotalElements());
    //返回结果
        return new QueryResponseResult(CommonCode.SUCCESS,cmsPageQueryResult);
    }
}
