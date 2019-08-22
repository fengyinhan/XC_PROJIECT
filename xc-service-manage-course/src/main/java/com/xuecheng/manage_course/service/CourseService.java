package com.xuecheng.manage_course.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
//import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.dao.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    CourseBaseRepository courseBaseRepository;

    @Autowired
    TeachplanRepository teachplanRepository;

    @Autowired
    CourseMapper courseMapper;

    @Autowired
    CourseMarkerRepository courseMarkerRepository;


    @Autowired
    CoursePicRepository coursePicRepository;

    @Autowired
    CmsPageClient cmsPageClient;

    @Autowired
    CoursePubRepository coursePubRepository;

    @Autowired
    TeachplanMediaRepository teachplanMediaRepository;

    @Autowired
    TeachplanMediaPubRepository teachplanMediaPubRepository;

    @Value("${course-publish.dataUrlPre}")
    private String publish_dataUrlPre;
    @Value("${course-publish.pagePhysicalPath}")
    private String publish_page_physicalpath;
    @Value("${course-publish.pageWebPath}")
    private String publish_page_webpath;
    @Value("${course-publish.siteId}")
    private String publish_siteId;
    @Value("${course-publish.templateId}")
    private String publish_templateId;
    @Value("${course-publish.previewUrl}")
    private String previewUrl;

    //查询课程计划
    public TeachplanNode findTeachplanList(String courseId) {

           return teachplanMapper.selectList(courseId);
    }


    //获取课程根节点,如果没有就添加根节点
    public String getTeachplanRoot(String courseId){
        //查询课程信息
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if(!optional.isPresent()){
            return null;
        }
        //获取课程信息
        CourseBase courseBase = optional.get();

        //得到课程计划根节点
        List<Teachplan> teachplans = teachplanRepository.findByCourseidAndParentid(courseId, "0");
          if(teachplans == null || teachplans.size() ==0){
              //证明它就是1级节点   新增一个根节点
              Teachplan teachplanRoot = new Teachplan();
              teachplanRoot.setCourseid(courseId);
              teachplanRoot.setGrade("1");  //1级节点
              teachplanRoot.setPname(courseBase.getName());
              teachplanRoot.setParentid("0"); //1级节点的父节点为0
              teachplanRoot.setStatus("0");  //未发布

              teachplanRepository.save(teachplanRoot);
              return teachplanRoot.getId();
          }
          return teachplans.get(0).getId();
    }

    //添加课程计划
    @Transactional
    public ResponseResult addTeachplan(Teachplan teachplan){
      //检查课程id和课程计划信息
        if(teachplan == null || StringUtils.isEmpty(teachplan.getCourseid()) || StringUtils.isEmpty(teachplan.getPname())){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
      //得到课程id
        String courseid = teachplan.getCourseid();
        //得到父节点Id
        String parentid = teachplan.getParentid();
        //如果父节点为空 证明没有选择 有2种情况 一种是1级节点 一种的2级节点
        if(StringUtils.isEmpty(parentid)){
            parentid = this.getTeachplanRoot(courseid);
        }
        //取出父节点的信息
        Optional<Teachplan> optional = teachplanRepository.findById(parentid);
        if(!optional.isPresent()){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        Teachplan teachplanParent = optional.get();

        String grade = teachplanParent.getGrade();//获取父节点等级

        if(grade.equals("1")){
            teachplan.setGrade("2");
        }else {
            teachplan.setGrade("3");
        }

        //设置父节点id
        teachplan.setParentid(parentid);
        //设置未发布
        teachplan.setStatus("0");
        //设置课程id
        teachplan.setCourseid(teachplanParent.getCourseid());

        teachplanRepository.save(teachplan);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //课程列表分页查询
    public QueryResponseResult<CourseInfo> findCourseList(String company_id,int page, int size, CourseListRequest
            courseListRequest) {
        if(courseListRequest == null){
            courseListRequest = new CourseListRequest();
        }
        //将公司id参数传入dao
        courseListRequest.setCompanyId(company_id);
        //先判断传入的参数 page,size
        if(page <= 0){
            page = 1;
        }
        if(size <=0){
            size = 10;
        }
        //设置分页参数
        PageHelper.startPage(page,size);
        Page<CourseInfo> courseListPage = courseMapper.findCourseListPage(courseListRequest);
        //获取查询列表
        List<CourseInfo> list = courseListPage.getResult();
        //总记录数
        long total = courseListPage.getTotal();
        //查询结果集
        QueryResult<CourseInfo> queryResult = new QueryResult<>();
        queryResult.setList(list);
        queryResult.setTotal(total);
        return new QueryResponseResult<CourseInfo>(CommonCode.SUCCESS,queryResult);
    }


    //添加课程提交
    //添加数据时必须返回一个成功或者失败的结果
    public AddCourseResult addCourseBase(CourseBase courseBase) {
        courseBase.setStatus("202002");
        courseBaseRepository.save(courseBase);
        return new AddCourseResult(CommonCode.SUCCESS,courseBase.getId());
    }

    //获取课程信息
    public CourseBase findByCourseId(String course){
        return  courseMapper.findCourseBaseById(course);
    }


    //更新课程信息
    public ResponseResult updateCourseBase(String id,CourseBase courseBase){
        CourseBase base = courseMapper.findCourseBaseById(id);
        if(base == null){
            return new ResponseResult(CommonCode.INVALID_PARAM);
        }
          base.setName(courseBase.getName());
          base.setStatus(courseBase.getStatus());
          base.setMt(courseBase.getMt());
          base.setGrade(courseBase.getGrade());
          base.setStudymodel(courseBase.getStudymodel());
          base.setTeachmode(courseBase.getTeachmode());
          base.setDescription(courseBase.getDescription());
          base.setUsers(courseBase.getUsers());
          base.setSt(courseBase.getSt());
          courseBaseRepository.save(courseBase);
          return new ResponseResult(CommonCode.SUCCESS);

    }

    //获取课堂营销信息

       public CourseMarket findMarkerById(String courseId){
           Optional<CourseMarket> optional = courseMarkerRepository.findById(courseId);
           if(optional.isPresent()){
               CourseMarket courseMarket = optional.get();
               return courseMarket;
           }
           return null;
       }

    //更新课堂营销信息
    @Transactional
    public CourseMarket updateMarker(String courseId,CourseMarket courseMarket){
         //先判断是否有值
        CourseMarket marker = this.findMarkerById(courseId);
        if(marker == null){
            //证明课程营销信息为null  为添加
            marker = new CourseMarket();
            //设置id
            marker.setId(courseId);
            BeanUtils.copyProperties(courseMarket,marker);
            courseMarkerRepository.save(marker);
        }else{
            //则为更新
            marker.setPrice(courseMarket.getPrice());
            marker.setPrice_old(courseMarket.getPrice_old());
            marker.setCharge(courseMarket.getCharge());
            marker.setEndTime(courseMarket.getEndTime());
            marker.setQq(courseMarket.getQq());
            marker.setStartTime(courseMarket.getStartTime());
            marker.setValid(courseMarket.getValid());
             courseMarkerRepository.save(marker);
        }
                return marker;
    }

    //添加课程图片
    @Transactional
    public ResponseResult saveCoursePic(String courseId,String pic) {

        CoursePic coursePic = null;

        //查询课程图片
        Optional<CoursePic> optional = coursePicRepository.findById(courseId);


        if(optional.isPresent()){
           coursePic = optional.get();
        }
       //由于查询表结构后发现 每个课程只能有一张图片
        if(coursePic == null){
            coursePic = new CoursePic();
        }
        coursePic.setCourseid(courseId);
        coursePic.setPic(pic);

        coursePicRepository.save(coursePic);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    public CoursePic findPicById(String courseId) {
        Optional<CoursePic> optional = coursePicRepository.findById(courseId);
        if(optional.isPresent()){
            return optional.get();
        }
        return null;
    }

    //删除图片
    @Transactional
    public ResponseResult deleteById(String courseId) {
        long flag = coursePicRepository.deleteByCourseid(courseId);
        //当返回值大于0则证明删除成功
        if(flag > 0){
            return new ResponseResult(CommonCode.SUCCESS);
        }else {
            return new ResponseResult(CommonCode.FAIL);
        }
    }

    //查询课程的相关信息  基本信息  图片 营销信息 教学计划
    public CourseView getCoruseView(String id) {
        CourseView courseView = new CourseView();
        //首先存入基本信息
        Optional<CourseBase> baseOptional = courseBaseRepository.findById(id);
        if(baseOptional.isPresent()){
            courseView.setCourseBase(baseOptional.get());
        }
        //存入图片
        Optional<CoursePic> picOptional = coursePicRepository.findById(id);
        if(picOptional.isPresent()){
            courseView.setCoursePic(picOptional.get());
        }
        //存入营销信息
        Optional<CourseMarket> marketOptional = courseMarkerRepository.findById(id);
        if(marketOptional.isPresent()){
            courseView.setCourseMarket(marketOptional.get());
        }
        //存入教学计划
        TeachplanNode teachplanNode = teachplanMapper.selectList(id);
        courseView.setTeachplanNode(teachplanNode);
            return courseView;
    }
      //存入课程页面便于预览
    public CoursePublishResult preview(String id) {
        //查询课程
        CourseBase courseBase = this.findByCourseId(id);

        //请求cms页面查询
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId(publish_siteId);//站点id
        cmsPage.setDataUrl(publish_dataUrlPre+id);//数据模型url
        cmsPage.setPageName(id+".html");//
        cmsPage.setPageAliase(courseBase.getName());//页面别名，就是课程名称页面名称
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);//页面物理路径
        cmsPage.setPageWebPath(publish_page_webpath);//页面webpath
        cmsPage.setTemplateId(publish_templateId);//页面模板id
        //调用方法
        CmsPageResult cmsPageResult = cmsPageClient.save(cmsPage);
        if(!cmsPageResult.isSuccess()){
            return new CoursePublishResult(CommonCode.FAIL,null);
        }

        CmsPage cmsPage1 = cmsPageResult.getCmsPage();
        String pageId = cmsPage1.getPageId();
        //拼装页面预览的url
        String url = previewUrl+pageId;
        //返回CoursePublishResult对象(当中包含了页面预览)

        return new CoursePublishResult(CommonCode.SUCCESS,url);
    }

    //一键发布
    @Transactional
    public CoursePublishResult publish(String id) {
        //查询课程
        CourseBase courseBase = this.findByCourseId(id);

        //请求cms页面查询
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId(publish_siteId);//站点id
        cmsPage.setDataUrl(publish_dataUrlPre+id);//数据模型url
        cmsPage.setPageName(id+".html");//
        cmsPage.setPageAliase(courseBase.getName());//页面别名，就是课程名称页面名称
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);//页面物理路径
        cmsPage.setPageWebPath(publish_page_webpath);//页面webpath
        cmsPage.setTemplateId(publish_templateId);//页面模板id
        //调用cms一键发布接口将课程详情页面发布到服务器
        CmsPostPageResult cmsPostPageResult = cmsPageClient.postPageQuick(cmsPage);
        if(!cmsPostPageResult.isSuccess()){
            return new CoursePublishResult(CommonCode.FAIL,null);
        }
         //保存的课程发布状态为发布状态 202002
        CourseBase courseBase1 = this.saveCourseBasePubSatu(id);
        if(courseBase1 == null){
            return new CoursePublishResult(CommonCode.FAIL,null);
        }

        //创建课程索引
        //创建课程索引信息
        CoursePub coursePub = createCoursePub(id);
        //向数据库保存课程索引信息
        saveCoursePub(id,coursePub);

        //获取url
        String pageUrl = cmsPostPageResult.getPageUrl();

        //向teachplanMediaPub中保存课程媒资信息
          this.saveTeachPlanMediaPub(id);
        return new CoursePublishResult(CommonCode.SUCCESS,pageUrl);

    }
    //向teachplanMediaPub中保存课程媒资信息
    public void saveTeachPlanMediaPub(String id){
        //先删除course_mdeia_pub中的数据
        teachplanMediaPubRepository.deleteByCourseId(id);
        //从teachplanMedia中查询
        List<TeachplanMedia> mediaList = teachplanMediaRepository.findByCourseId(id);
        List<TeachplanMediaPub> teachplanMediaPubs = new ArrayList<>();
        for (TeachplanMedia teachplanMedia : mediaList) {
            //属性都一样 可以直接copy
            TeachplanMediaPub teachplanMediaPub = new TeachplanMediaPub();
            BeanUtils.copyProperties(teachplanMedia,teachplanMediaPub);
            //添加时间戳
            teachplanMediaPub.setTimestamp(new Date());
            teachplanMediaPubs.add(teachplanMediaPub);
        }
        //将teachplanMediaList插入到teachplanMediaPub
           teachplanMediaPubRepository.saveAll(teachplanMediaPubs);
    }

    //更新课程的发布状态
    public CourseBase saveCourseBasePubSatu(String courseId){
        CourseBase base = this.findByCourseId(courseId);
        base.setStatus("202002");
        courseBaseRepository.save(base);
        return base;
    }

    //保存CoursePub信息
    public CoursePub saveCoursePub(String id,CoursePub coursePub){
        if(StringUtils.isEmpty(id)){
            ExceptionCast.cast(CourseCode.COURSE_PUBLISH_COURSEIDISNULL);
        }
        CoursePub coursePubNew = null;
        Optional<CoursePub> coursePubOptional = coursePubRepository.findById(id);
        if(coursePubOptional.isPresent()){
            coursePubNew = coursePubOptional.get();
        }
        if(coursePubNew == null){
            coursePubNew = new CoursePub();
        }
        BeanUtils.copyProperties(coursePub,coursePubNew);
        //设置主键id
        coursePubNew.setId(id);
        //更新时间戳为最新时间
        coursePub.setTimestamp(new Date());
        //更新发布时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY‐MM‐dd HH:mm:ss");
        String date = simpleDateFormat.format(new Date());
        coursePub.setPubTime(date);
        coursePubRepository.save(coursePub);
        return coursePub;
    }

    //创建CoursePub

    private CoursePub createCoursePub(String id){
          CoursePub coursePub = new CoursePub();
          //设置id
        coursePub.setId(id);
        //设置基本信息
        Optional<CourseBase> optional = courseBaseRepository.findById(id);
        if(optional.isPresent()){
            CourseBase courseBase = optional.get();
            //复制到coursePub对象中
            BeanUtils.copyProperties(courseBase,coursePub);
        }
        //设置图片
        Optional<CoursePic> picoptional1 = coursePicRepository.findById(id);
        if(picoptional1.isPresent()){
            CoursePic coursePic = picoptional1.get();
            BeanUtils.copyProperties(coursePic,coursePub);
        }
        //设置营销信息
        Optional<CourseMarket> marketOptional = courseMarkerRepository.findById(id);
        if(marketOptional.isPresent()){
            CourseMarket courseMarket = marketOptional.get();
             BeanUtils.copyProperties(courseMarket,coursePub);
        }
        //设置课程计划
        TeachplanNode teachplanNode = teachplanMapper.selectList(id);
        String jsonString = JSON.toJSONString(teachplanNode);
        coursePub.setTeachplan(jsonString);

        return coursePub;
    }

  //保存课程计划与媒资文件的关联信息
    public ResponseResult savemedia(TeachplanMedia teachplanMedia){
        if(teachplanMedia == null || StringUtils.isEmpty(teachplanMedia.getTeachplanId())){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //校验课程计划是否是3级
        //课程计划
        String teachplanId = teachplanMedia.getTeachplanId();

        //查询到课程计划
        Optional<Teachplan> optional = teachplanRepository.findById(teachplanId);
        if(!optional.isPresent()){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        Teachplan teachplan = optional.get();
        //只允许为叶子结点课程计划选择视频  等级
        String grade = teachplan.getGrade();
        if (StringUtils.isEmpty(grade) || !grade.equals("3")){
                   ExceptionCast.cast(CourseCode.COURSE_MEDIA_TEACHPLAN_GRADEERROR);
        }
         //查询teachplanMedia
        Optional<TeachplanMedia> optional1 = teachplanMediaRepository.findById(teachplanId);
         TeachplanMedia one = null;
         if(optional1.isPresent()){
             one = optional1.get();
         }else{
             one = new TeachplanMedia();
         }

        //将one保存到数据库
        one.setCourseId(teachplan.getCourseid());//课程id
        one.setMediaId(teachplanMedia.getMediaId());//媒资文件的id
        one.setMediaFileOriginalName(teachplanMedia.getMediaFileOriginalName());//媒资文件的原始名称
        one.setMediaUrl(teachplanMedia.getMediaUrl());//媒资文件的url
        one.setTeachplanId(teachplanId);
        teachplanMediaRepository.save(one);
        return new ResponseResult(CommonCode.SUCCESS);
    }


    }
