package com.xuecheng.manage_course.controller;
import com.xuecheng.api.course.CourseControllerApi;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.XcOauth2Util;
import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/course")
public class CourseController extends BaseController implements CourseControllerApi {

  @Autowired
    CourseService courseService;


//查询课堂分类列表

    @Override
    @GetMapping("/teachplan/list/{courseId}")
    public TeachplanNode findTeachplanList(@PathVariable("courseId") String courseId) {
        return courseService.findTeachplanList(courseId);
    }

    //添加分类
  @Override
  @PostMapping("/teachplan/add")
  public ResponseResult addTeachplan(@RequestBody Teachplan teachplan) {
    return courseService.addTeachplan(teachplan);
  }


//分页查询课堂信息
  @Override
  @GetMapping("/coursebase/list/{page}/{size}")
  public QueryResponseResult<CourseInfo> findCourseList(@PathVariable("page") int page, @PathVariable("size") int size, CourseListRequest courseListRequest) {
     //获取当前用户信息
    XcOauth2Util xcOauth2Util = new XcOauth2Util();
    XcOauth2Util.UserJwt  userJwt = xcOauth2Util.getUserJwtFromHeader(request);
    //当前用户所属单位的id
    String companyId = userJwt.getCompanyId();
    QueryResponseResult<CourseInfo> queryResponseResult = courseService.findCourseList(companyId,page,size,courseListRequest);
     return queryResponseResult;
     }

  //添加课堂信息
  @Override
  @PostMapping("/coursebase/add")
  public AddCourseResult addCourseBase(@RequestBody CourseBase courseBase) {
    return courseService.addCourseBase(courseBase);
  }

  //查找课堂信息
  @Override
  @GetMapping("/courseBase/{courseId}")
  public CourseBase findByCourseId(@PathVariable("courseId") String courseId) {
    return courseService.findByCourseId(courseId);
  }

  //更新课堂信息
  @Override
  @PutMapping("/updateCourseBase/{courseId}")
  public ResponseResult updateCourseBase(@PathVariable("courseId") String courseId, @RequestBody CourseBase courseBase) {
    return courseService.updateCourseBase(courseId,courseBase);
  }

  //查询课堂营销信息
  @Override
  @GetMapping("/findCourseMaker/{courseId}")
  public CourseMarket getCourseMarketById(@PathVariable("courseId") String courseId) {
    return courseService.findMarkerById(courseId);
  }

  //更新课堂营销信息
  @Override
  @PutMapping("/updateCourseMarker/{courseId}")
  public ResponseResult updateCourseMarket(@PathVariable("courseId") String id,@RequestBody CourseMarket courseMarket) {
    CourseMarket courseMarket1 = courseService.updateMarker(id, courseMarket);
    if(courseMarket1 != null){
      return new ResponseResult(CommonCode.SUCCESS);
    }else{
      return new ResponseResult(CommonCode.FAIL);
    }
  }

  /**
   * 添加图片
   * @param courseId   课堂id
   * @param pic  图片id
   * @return
   */
  @Override
  @PostMapping("/coursepic/add")
  public ResponseResult addCoursePic(@RequestParam("courseId") String courseId, @RequestParam("pic")String pic) {
    return courseService.saveCoursePic(courseId,pic);
  }

  //查询课程图片
  @Override
  @PreAuthorize("hasAuthority('course_find_pic')")
  @GetMapping("/coursepic/list/{courseId}")
  public CoursePic findBycourseId(@PathVariable("courseId") String course) {
    return courseService.findPicById(course);
  }

  //通过courseId删除课堂图片
  @Override
  @DeleteMapping("/coursepic/delete")
  public ResponseResult deleteById(@RequestParam("courseId") String courseId) {
    return courseService.deleteById(courseId);
  }

  @Override
  @GetMapping("/courseview/{id}")
  public CourseView courseview(@PathVariable("id") String id) {
    return courseService.getCoruseView(id);
  }

  @Override
  @PostMapping("/preview/{id}")
  public CoursePublishResult preview(@PathVariable("id") String id) {
    return courseService.preview(id);
  }

  @Override
  @PostMapping("/publish/{id}")
  public CoursePublishResult publish(@PathVariable("id") String id) {
    return courseService.publish(id);
  }

  @Override
  @PostMapping("/savemedia")
  public ResponseResult savemedia(@RequestBody TeachplanMedia teachplanMedia) {
    return courseService.savemedia(teachplanMedia);
  }

}
