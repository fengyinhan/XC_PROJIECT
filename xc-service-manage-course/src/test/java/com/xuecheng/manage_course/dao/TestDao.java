package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.manage_course.ManageCourseApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import com.github.pagehelper.PageHelper;

import javax.sound.midi.Soundbank;
import java.util.List;
import java.util.Optional;


@SpringBootTest
@RunWith(SpringRunner.class)
public class TestDao {

    @Autowired
    CourseBaseRepository courseBaseRepository;

    @Autowired
    CourseMapper courseMapper;
    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    CategoryMapper categoryMapper;

    @Test
    public void testCourseBaseRepository(){
        Optional<CourseBase> optional = courseBaseRepository.findById("402885816240d276016240f7e5000002");
        if(optional.isPresent()){
            CourseBase courseBase = optional.get();
            System.out.println(courseBase);
        }

    }

    @Test
    public void testCourseMapper(){
        CourseBase courseBase = courseMapper.findCourseBaseById("402885816240d276016240f7e5000002");
        System.out.println(courseBase);

    }

    @Test
    public void testFindBycourseId(){
        TeachplanNode teachplanNode = teachplanMapper.selectList("4028e581617f945f01617f9dabc40000");
        System.out.println(teachplanNode);
    }

    @Test
    public void test001(){
        CourseBase courseBaseById = courseMapper.findCourseBaseById("40288581625c7e7301625c8ed6af0000");
        System.out.println(courseBaseById.getSt());
        System.out.println(courseBaseById.getMt());
    }

}
