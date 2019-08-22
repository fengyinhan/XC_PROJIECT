package com.xuecheng.learning.dao;

import com.xuecheng.framework.domain.task.XcTaskHis;
import org.springframework.data.jpa.repository.JpaRepository;
//查看历史
public interface XcTaskHisRepository extends JpaRepository<XcTaskHis,String> {

}
