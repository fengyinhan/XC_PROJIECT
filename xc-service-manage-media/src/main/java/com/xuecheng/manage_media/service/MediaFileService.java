package com.xuecheng.manage_media.service;

import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class MediaFileService {

    @Autowired
    MediaFileRepository mediaFileRepository;

    //文件列表分页查询
    public QueryResponseResult findList(int page, int size, QueryMediaFileRequest
            queryMediaFileRequest){
          //查询条件
        MediaFile mediaFile = new MediaFile();
        //判断查询条件是否为空 为空则new一个
        if(queryMediaFileRequest == null){
            queryMediaFileRequest = new QueryMediaFileRequest();
        }
        //创建条件匹配器
        ExampleMatcher matcher = ExampleMatcher.matching().
                withMatcher("tag",ExampleMatcher.GenericPropertyMatchers.contains()). //根据tag模糊匹配
                withMatcher("fileOriginalName",ExampleMatcher.GenericPropertyMatchers.contains());//根据原始名称模糊匹配
            //查询对象
        if(StringUtils.isNotEmpty(queryMediaFileRequest.getTag())){
                mediaFile.setTag(queryMediaFileRequest.getTag());
        }
        if(StringUtils.isNotEmpty(queryMediaFileRequest.getFileOriginalName())){
                mediaFile.setFileOriginalName(queryMediaFileRequest.getFileOriginalName());
        }
        if(StringUtils.isNotEmpty(queryMediaFileRequest.getProcessStatus())){
               mediaFile.setProcessStatus(queryMediaFileRequest.getProcessStatus());
        }
        //定义example实例
        Example<MediaFile> ex = Example.of(mediaFile, matcher);

            if(page <= 0){
                page = 1;
            }
            page = page -1;
            if(size <= 0){
                size = 10;
            }
            //分页参数
        Pageable pageable = new PageRequest(page,size);
            //分页查询
        Page<MediaFile> files = mediaFileRepository.findAll(ex, pageable);
        QueryResult<MediaFile> queryResult = new QueryResult<>();
        //数据列表
        queryResult.setList(files.getContent());
        //总记录数
        queryResult.setTotal(files.getTotalElements());

        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }

}
