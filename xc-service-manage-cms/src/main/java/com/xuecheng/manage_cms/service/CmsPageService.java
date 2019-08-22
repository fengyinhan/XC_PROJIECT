package com.xuecheng.manage_cms.service;
import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.config.RabbitmqConfig;
import com.xuecheng.manage_cms.dao.CmsConfigRepository;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsSiteRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Service
public class CmsPageService {

    @Autowired
    private CmsPageRepository cmsPageRepository;

    @Autowired
    private CmsConfigRepository cmsConfigRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    @Autowired
    private CmsTemplateRepository cmsTemplateRepository;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    CmsSiteRepository cmsSiteRepository;

    /**\
     *   分页页面查询
     * @param page  页码从1开始
     * @param size   每页显示的条数
     * @param queryPageRequest
     * @return
     */
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest){

        if(queryPageRequest ==null){
            queryPageRequest = new QueryPageRequest();
        }

        //定义条件匹配器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching();
        exampleMatcher = exampleMatcher.withMatcher("pageAliase",ExampleMatcher.GenericPropertyMatchers.contains());
        exampleMatcher = exampleMatcher.withMatcher("pageName",ExampleMatcher.GenericPropertyMatchers.contains());
        //
        CmsPage cmsPage = new CmsPage();
        //站点ID
        if (StringUtils.isNotEmpty(queryPageRequest.getSiteId())){
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }
        //模板ID
        if (StringUtils.isNotEmpty(queryPageRequest.getTemplateId())){
           cmsPage.setTemplateId(queryPageRequest.getTemplateId());
        }
        //页面别名
        if (StringUtils.isNotEmpty(queryPageRequest.getPageAliase())){
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }
        //页面名称
        if(StringUtils.isNotEmpty(queryPageRequest.getPageName())){
            cmsPage.setPageName(queryPageRequest.getPageName());
        }
        //页面类型
        if(StringUtils.isNotEmpty(queryPageRequest.getPageType())){
            cmsPage.setPageType(queryPageRequest.getPageType());
        }
        //先判断传入的参数 page,size
        if(page <= 0){
            page = 1;
        }
        if(size <=0){
            size = 10;
        }
        //创建条件实例
        Example<CmsPage> example =Example.of(cmsPage,exampleMatcher);
        page = page -1;
        Pageable pageable = PageRequest.of(page, size);
        Page<CmsPage> cmsPages = cmsPageRepository.findAll(example,pageable);

        QueryResult queryResult = new QueryResult();

        queryResult.setList(cmsPages.getContent());//数据列表
        queryResult.setTotal(cmsPages.getTotalElements());//数据总记录数

        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS,queryResult);

        return queryResponseResult;
    }

    //添加页面
    public CmsPageResult add(CmsPage cmsPage){
            if(cmsPage == null){
                //抛出异常,非法请求
            }
        //校验页面是否存在,根据页面名称,站点id,页面webpath查询
        CmsPage cmsPage1 = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
               if(cmsPage1 != null){
                   //抛出异常,页面已经存在
                   ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
               }
                   cmsPage.setPageId(null);//让它自动生成
        CmsPage save = cmsPageRepository.save(cmsPage);
        CmsPageResult cmsPageResult = new CmsPageResult(CommonCode.SUCCESS, save);

        return cmsPageResult;

    }

    //根据id查询页面
    public CmsPage getById(String id){
        Optional<CmsPage> optional = cmsPageRepository.findById(id);
        if(optional.isPresent()){
            return optional.get();
        }
        //返回null
        return null;
    }

    //更新页面信息
    public CmsPageResult update(String id,CmsPage cmsPage){
        //根据id查询页面信息
        CmsPage one = this.getById(id);
        if(one != null){
            //更新模板id
            one.setTemplateId(cmsPage.getTemplateId());
            //更新所属站点
            one.setSiteId(cmsPage.getSiteId());
            //更新页面别名
            one.setPageAliase(cmsPage.getPageAliase());
            //更新页面名称
            one.setPageName(cmsPage.getPageName());
            //更新访问路径
            one.setPageWebPath(cmsPage.getPageWebPath());
            //更新物理路径
            one.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            //更新dataUrl
            one.setDataUrl(cmsPage.getDataUrl());
            //提交修改
            cmsPageRepository.save(one);
            return new CmsPageResult(CommonCode.SUCCESS,one);
        }
           return new CmsPageResult(CommonCode.FAIL,null);
    }

    //删除页面
       public ResponseResult deleteById(String id){
           Optional<CmsPage> optional = cmsPageRepository.findById(id);
           if(optional.isPresent()){
               cmsPageRepository.deleteById(id);
               return new ResponseResult(CommonCode.SUCCESS);
           }
               return new ResponseResult(CommonCode.FAIL);
       }

    //根据id查询cmsConfig
    
    public CmsConfig getConfigById(String id) {
        Optional<CmsConfig> optional = cmsConfigRepository.findById(id);
        if (optional.isPresent()) {
            CmsConfig cmsConfig = optional.get();
            return cmsConfig;
        }
        return null;
    }


    /*----页面静态化----*/

    //页面静态化方法汇总
    
    public String getPageHtml(String pageId) {
        //获取页面模型数据
        Map model = this.getModelByPageId(pageId);
        if (model == null){
            //获取的页面模型数据为空
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }
        //获取页面模板
        String templateContent = this.getTemplateByPageId(pageId);
        if (StringUtils.isEmpty(templateContent)){
            //页面模板为空
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }

        //执行静态化
        String html = generateHtml(templateContent, model);
        if (StringUtils.isEmpty(html)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }
        return html;
    }

    //静态化过程方法
    
    public String generateHtml(String template, Map model) {
        try {
            //生成配置类
            Configuration configuration = new Configuration(Configuration.getVersion());
            //模板加载器
            StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
            stringTemplateLoader.putTemplate("template", template);
            //配置模板加载器
            configuration.setTemplateLoader(stringTemplateLoader);

            //获取模板
            Template template1 = configuration.getTemplate("template");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template1, model);

            return html;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取页面模板的过程方法
    
    public String getTemplateByPageId(String pageId) {
        //查询页面信息
        CmsPage cmsPage = this.getById(pageId);
        if (cmsPage == null) {
            //页面不存在
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        //获取页面模板ID
        String templateId = cmsPage.getTemplateId();
        if (StringUtils.isEmpty(templateId)) {
            //页面模板id为空,即模板不存在
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }

        Optional<CmsTemplate> optional = cmsTemplateRepository.findById(templateId);
        if (optional.isPresent()) {
            CmsTemplate cmsTemplate = optional.get();
            //获取模板文件的id
            String templateFileId = cmsTemplate.getTemplateFileId();
            //从GridFS中取模板文件内容
            //根据文件id查询文件
            GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));

            //打开一个下载流对象
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
            //创建GridFsResource对象，获取流
            GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);

            try {
                //从流中取出数据
                String content = IOUtils.toString(gridFsResource.getInputStream(),"utf-8");
                return content;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //获取数据模型的过程方法
    public Map getModelByPageId(String pageId) {
        //取出页面的信息
        CmsPage cmsPage = this.getById(pageId);
        if (cmsPage == null) {
            //页面不存在
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }

        //取出页面的dataUrl
        String dataUrl = cmsPage.getDataUrl();
        if (StringUtils.isEmpty(dataUrl)) {
            //页面的dataUrl为空
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }
        //通过restTemplate请求dataUrl获取数据
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
        Map body = forEntity.getBody();
        return body;
    }


    /*页面发布*/

    public ResponseResult postPage(String pageId){
        //执行页面静态化
        String pageHtml = this.getPageHtml(pageId);
        //将页面静态化文件存储到GridFs中
        CmsPage cmsPage = saveHtml(pageId, pageHtml);
        //向MQ发消息
        sendPostPage(pageId);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //保存html到GridFS
    private CmsPage saveHtml(String pageId,String htmlContent){
        //先得到页面信息
        CmsPage cmsPage = this.getById(pageId);
        if(cmsPage == null){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        ObjectId objectId = null;
        try {
            //将html内容htmlContent转成输入流
            InputStream inputStream = IOUtils.toInputStream(htmlContent, "utf-8");
            //将html文件内容保存到GridFS
            objectId = gridFsTemplate.store(inputStream, cmsPage.getPageName());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //将html文件的id更新到cmsPage中
        cmsPage.setHtmlFileId(objectId.toHexString());
        cmsPageRepository.save(cmsPage);
        return cmsPage;
    }



    //向mq 发送消息
    private void sendPostPage(String pageId){
        //得到页面信息
        CmsPage cmsPage = this.getById(pageId);
        if(cmsPage == null){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //创建消息对象
        Map<String,String> msg = new HashMap<>();
        msg.put("pageId",pageId);
        //转成json串
        String jsonString = JSON.toJSONString(msg);
        //发送给mq
        //站点id
        String siteId = cmsPage.getSiteId();
        rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE,siteId,jsonString);
    }


    //保存页面
    public CmsPageResult save(CmsPage cmsPage) {
        //检查页面是否存在,根据页面名称,站点id 页面webpath查询
        CmsPage page = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if(page != null){
            //更新
            return this.update(page.getPageId(),page);
        }else {
            return this.add(page);
        }
    }

    //一键发布
    public CmsPostPageResult postPageQuick(CmsPage cmsPage) {
           //将页面存到cms_page
        CmsPageResult save = this.save(cmsPage);
        //判断是否保存成功
        if(!save.isSuccess()){
            ExceptionCast.cast(CommonCode.FAIL);
        }
        //获取页面id
        CmsPage saveCmsPage = save.getCmsPage();
        String pageId = saveCmsPage.getPageId();
        //执行静态化,向MQ发送消息
        ResponseResult responseResult = this.postPage(pageId);
        if(!responseResult.isSuccess()){
            ExceptionCast.cast(CommonCode.FAIL);
        }
        //拼接页面Url= cmsSite.siteDomain+cmsSite.siteWebPath+ cmsPage.pageWebPath + cmsPage.pageName
        //获取站点id
        String siteId = saveCmsPage.getSiteId();
        //获取站点内容
        CmsSite cmsSite = this.findCmsSiteById(siteId);
        //页面url
        String url = cmsSite.getSiteDomain()+cmsSite.getSiteWebPath()+saveCmsPage.getPageWebPath()+saveCmsPage.getPageName();
        return new CmsPostPageResult(CommonCode.SUCCESS,url);

    }

    //根据站点查询站点信息
    public CmsSite findCmsSiteById(String siteId){
        Optional<CmsSite> optional = cmsSiteRepository.findById(siteId);
        if(optional.isPresent()){
            return optional.get();
        }
        return null;
    }

}
