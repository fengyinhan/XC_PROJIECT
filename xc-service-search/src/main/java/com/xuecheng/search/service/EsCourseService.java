package com.xuecheng.search.service;


import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EsCourseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EsCourseService.class);

    @Value("${xuecheng.course.index}")
    private String index;
    @Value("${xuecheng.course.type}")
    private String type;
    @Value("${xuecheng.course.source_field}")
    private String source_field;
    @Value("${xuecheng.media.index}")
    private String media_index;
    @Value("${xuecheng.media.type}")
    private String media_type;
    @Value("${xuecheng.media.source_field}")
    private String media_source_field;

    @Autowired
    RestHighLevelClient restHighLevelClient;


    //课程搜索
    public QueryResponseResult<CoursePub> list(int page, int size, CourseSearchParam courseSearchParam)  {
         if(courseSearchParam == null){
             courseSearchParam = new CourseSearchParam();
         }
         //创建搜索请求对象
        SearchRequest searchRequest = new SearchRequest(index);
        //设置类型
         searchRequest.types(type);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //source源字段过滤
        String[] source_fields = source_field.split(",");
        searchSourceBuilder.fetchSource(source_fields,new String[]{});

        //创建布尔查询对象
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        //关键字
           if(StringUtils.isNotEmpty(courseSearchParam.getKeyword())){
               //匹配关键字
               MultiMatchQueryBuilder multiMatchQueryBuilder =
                       QueryBuilders.multiMatchQuery(courseSearchParam.getKeyword(),"name","teachplan","description" );
               //设置匹配占比
               multiMatchQueryBuilder.minimumShouldMatch("70%");
               //提升另个字段的Boost值
               multiMatchQueryBuilder.field("name",10);
               boolQueryBuilder.must(multiMatchQueryBuilder);
           }

        if(StringUtils.isNotEmpty(courseSearchParam.getMt())){
            //根据一级分类
            boolQueryBuilder.filter(QueryBuilders.termQuery("mt",courseSearchParam.getMt()));
        }
        if(StringUtils.isNotEmpty(courseSearchParam.getSt())){
            //根据二级分类
            boolQueryBuilder.filter(QueryBuilders.termQuery("st",courseSearchParam.getSt()));
        }
        if(StringUtils.isNotEmpty(courseSearchParam.getGrade())){
            //根据难度等级
            boolQueryBuilder.filter(QueryBuilders.termQuery("grade",courseSearchParam.getGrade()));
        }

        //分页
        if(page <= 0){
               page = 1;
        }
        if(size <= 0){
               size = 20;
        }
        int start = (page - 1)*size;
           searchSourceBuilder.from(start);
           searchSourceBuilder.size(size);

           //设置高亮字段
        HighlightBuilder  highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<font class='eslight'>");
        highlightBuilder.postTags("</font>");

        //设置高亮字段
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));


        //设置boolQueryBuilder到searchSourceBuilder
               //布尔查询
        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);

        QueryResult<CoursePub> queryResult = new QueryResult();
        List<CoursePub> list = new ArrayList<>();
        try {
            //执行搜索
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            //获取响应结果
            SearchHits hits = searchResponse.getHits();
            //匹配的总记录数
            long totalHits = hits.totalHits;
            SearchHit[] searchHits = hits.getHits();

            queryResult.setTotal(totalHits);

            for(SearchHit hit:searchHits){
                CoursePub coursePub = new CoursePub();
                //源文档
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                //取出name
                String name = (String) sourceAsMap.get("name");
                coursePub.setName(name);
                //取出id
                String id = (String) sourceAsMap.get("id");
                coursePub.setId(id);
                //图片
                String pic = (String) sourceAsMap.get("pic");
                coursePub.setPic(pic);
                //价格
                Double price = null;
                try {
                    if(sourceAsMap.get("price")!=null ){
                        price = (Double) sourceAsMap.get("price");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                coursePub.setPrice(price);
                //旧价格
                Double price_old = null;
                try {
                    if(sourceAsMap.get("price_old")!=null ){
                        price_old = (Double) sourceAsMap.get("price_old");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                coursePub.setPrice_old(price_old);
                //将coursePub对象放入list
                list.add(coursePub);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

           queryResult.setList(list);
           QueryResponseResult<CoursePub> queryResponseResult = new QueryResponseResult<CoursePub>(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;
    }

     //通过es客户端访问es服务端
    public Map<String, CoursePub> getall(String id) {
        //创建搜索请求对象
        SearchRequest searchRequest = new SearchRequest(index);
        //设置类型
        searchRequest.types(type);
        //创建搜索源对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //使用termQuery通过id搜索
        SearchSourceBuilder sourceBuilder = searchSourceBuilder.query(QueryBuilders.termQuery("id", id));
        //不需要过滤源字段 全都要
        searchRequest.source(sourceBuilder);
        Map<String,CoursePub> map = new HashMap<>();
        //执行搜索
        try {
            SearchResponse search = restHighLevelClient.search(searchRequest);
             //获取数据列表
            SearchHits searchHits = search.getHits();

            for (SearchHit searchHit : searchHits) {
                //获得源文档内容
                Map<String, Object> source = searchHit.getSourceAsMap();
                CoursePub coursePub = new CoursePub();
                String courseId = (String) source.get("id");
                String name = (String) source.get("name");
                String grade = (String) source.get("grade");
                String charge = (String) source.get("charge");
                String pic = (String) source.get("pic");
                String description = (String) source.get("description");
                String teachplan = (String) source.get("teachplan");
                coursePub.setId(courseId);
                coursePub.setName(name);
                coursePub.setGrade(grade);
                coursePub.setCharge(charge);
                coursePub.setPic(pic);
                coursePub.setDescription(description);
                coursePub.setTeachplan(teachplan);
                map.put(courseId,coursePub);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }


     //根据多个课程计划查询课程的媒资管理
    public QueryResponseResult<TeachplanMediaPub> getmedia(String[] teachplanIds)   {
        //定义一个搜索请求对象
        SearchRequest searchRequest = new SearchRequest(media_index);
        //指定type
        searchRequest.types(media_type);

        //定义SearchSourceBuilder
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置使用termsQuery根据多个id 查询
        searchSourceBuilder.query(QueryBuilders.termsQuery("teachplan_id",teachplanIds));
        //过虑源字段
        String[] includes = media_source_field.split(",");
        searchSourceBuilder.fetchSource(includes,new String[]{});
        searchRequest.source(searchSourceBuilder);
        //使用es客户端进行搜索请求Es
        List<TeachplanMediaPub> teachplanMediaPubList = new ArrayList<>();
        long total = 0;
        try {
            //执行搜索
            SearchResponse search = restHighLevelClient.search(searchRequest);
            SearchHits hits = search.getHits();
            total = hits.totalHits;
            SearchHit[] searchHits = hits.getHits();
            for(SearchHit hit:searchHits){
                TeachplanMediaPub teachplanMediaPub= new TeachplanMediaPub();
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                //取出课程计划媒资信息
                String courseid = (String) sourceAsMap.get("courseid");
                String media_id = (String) sourceAsMap.get("media_id");
                String media_url = (String) sourceAsMap.get("media_url");
                String teachplan_id = (String) sourceAsMap.get("teachplan_id");
                String media_fileoriginalname = (String) sourceAsMap.get("media_fileoriginalname");

                teachplanMediaPub.setCourseId(courseid);
                teachplanMediaPub.setMediaUrl(media_url);
                teachplanMediaPub.setMediaFileOriginalName(media_fileoriginalname);
                teachplanMediaPub.setMediaId(media_id);
                teachplanMediaPub.setTeachplanId(teachplan_id);
                teachplanMediaPubList.add(teachplanMediaPub);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        //数据集合
        QueryResult<TeachplanMediaPub> queryResult = new QueryResult<>();
        queryResult.setList(teachplanMediaPubList);
        queryResult.setTotal(total);
        QueryResponseResult<TeachplanMediaPub> queryResponseResult = new QueryResponseResult<>(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;

    }
}
